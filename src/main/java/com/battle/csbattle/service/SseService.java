package com.battle.csbattle.service;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.battle.BattleType;
import com.battle.csbattle.util.SseUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;                            // Sse 연결의 유효시간. 만료되면 자동으로 클라에서 재연결 요청. TODO: 게임에 걸리는 시간보다 길게 설정해두기
    private static final Map<String, SseEmitter> waitingClients = new ConcurrentHashMap<>();       // 배틀 시작 전 대기 큐의 역할
    private final BattleService battleService;

    public SseService(BattleService battleService) {
        this.battleService = battleService;
    }

    public SseEmitter connect(String userId) {
        //String id = userId + "_" + System.currentTimeMillis();                            // emitter 의 id 를 뜻함. 필요 시 사용

        SseEmitter emitter = new SseEmitter(0L);                               // emitter 생성 후 waiting clients 에 집어넣기
        waitingClients.put(userId,emitter);

        System.out.println("--- current clients : " + waitingClients);
        System.out.println("--- current clients size : " + waitingClients.size());

        emitter.onCompletion(() -> {
            System.out.println("@@@ onCompletion callback");

            waitingClients.remove(userId);

            Battle onGoingBattle = battleService.findBattleOfUser(userId);
            if (onGoingBattle != null) {
                for (String playerId : onGoingBattle.getPlayers().keySet()) {
                    if (playerId != userId) {
                        SseEmitter opponent = onGoingBattle.getPlayers().get(playerId);
                        SseUtil.sendToClient(opponent, "opponent-left", userId + " 님이 게임을 나갔습니다.");
                    }
                }
                battleService.deleteBattleById(onGoingBattle.getId());
            }
        });
        emitter.onTimeout(() -> {
            System.out.println("@@@ onTimeout callback");
            emitter.complete();                                                           // onCompletion() 콜백 호출
        });

        for (String clientId : waitingClients.keySet()) {
            SseEmitter checkingEmitter = waitingClients.get(clientId);
            SseUtil.sendToClient(checkingEmitter,"checking-connection","checking connection");
        }

        if (waitingClients.size() == 2) {                                                       // 배틀 생성 (배틀 유형은 한문제 풀기)
            battleService.createBattle(BattleType.ONEQUESTION, waitingClients);
            waitingClients.clear();
        }

        SseUtil.sendToClient(emitter, "sse", "--------- EventStream Created. [userId=" + userId + "]");             // 503 에러를 방지하기 위한 더미 이벤트 전송

        return emitter;
    }
}
