package com.battle.csbattle.service;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.battle.BattleType;
import com.battle.csbattle.dto.UserDto;
import com.battle.csbattle.util.SseUtil;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000*10;                            // Sse 연결의 유효시간. 만료되면 자동으로 클라에서 재연결 요청. TODO: 게임에 걸리는 시간보다 길게 설정해두기
    private static final Map<String, UserDto> waitingPlayers = new ConcurrentHashMap<>();       // 배틀 시작 전 대기 큐의 역할

    @Getter
    public static final Map<String,UserDto> allPlayers=new ConcurrentHashMap<>();
    private final BattleService battleService;

    public SseService(BattleService battleService) {
        this.battleService = battleService;
    }

    public SseEmitter connect(String userId) {
        //String id = userId + "_" + System.currentTimeMillis();                            // emitter 의 id 를 뜻함. 필요 시 사용

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);                               // emitter 생성 후 waiting clients 에 집어넣기
        UserDto player=new UserDto(null,emitter,null);
        waitingPlayers.put(userId, player);
        allPlayers.put(userId,player);

        System.out.println("--- current clients : " + waitingPlayers);
        System.out.println("--- current clients size : " + waitingPlayers.size());

        emitter.onCompletion(() -> {
            System.out.println("@@@ onCompletion callback");

            allPlayers.remove(userId);

            Battle onGoingBattle = battleService.findBattleOfUser(userId);
            if (onGoingBattle != null) {

                if (onGoingBattle.getPlayers().size()==2) {
                    SseUtil.sendToClient(player.getOpponent().getEmitter(), "opponent-left", userId + " 님이 게임을 나갔습니다.");
                }
                onGoingBattle.deletePlayerById(userId);

                System.out.println(onGoingBattle.getPlayers());

                if (onGoingBattle.getPlayers().size() == 0) {
                    battleService.deleteBattleById(onGoingBattle.getId());
                }
            }
        });

        emitter.onTimeout(() -> {
            System.out.println("@@@ onTimeout callback");
            emitter.complete();                                                           // onCompletion() 콜백 호출
        });

        for (String clientId : waitingPlayers.keySet()) {
            SseEmitter checkingEmitter = waitingPlayers.get(clientId).getEmitter();
            SseUtil.sendToClient(checkingEmitter, "checking-connection", "checking connection");
        }

        if (waitingPlayers.size() == 2) {                                                       // 배틀 생성 (배틀 유형은 한문제 풀기)
            battleService.createBattle(BattleType.ONEQUESTION, waitingPlayers);
            waitingPlayers.clear();
        }

        SseUtil.sendToClient(emitter, "sse", "--------- EventStream Created. [userId=" + userId + "]");             // 503 에러를 방지하기 위한 더미 이벤트 전송

        return emitter;
    }
}
