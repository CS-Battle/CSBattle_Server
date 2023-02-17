package com.battle.csbattle.service;

import com.battle.csbattle.battle.BattleType;
import com.battle.csbattle.util.SseUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;                            // Sse 연결의 유효시간. 만료되면 자동으로 클라에서 재연결 요청. TODO: 게임에 걸리는 시간보다 길게 설정해두기
    private static final Map<String, SseEmitter> clients = new ConcurrentHashMap<>();       // 배틀 시작 전 대기 큐의 역할

    private final BattleService battleService;

    public SseService(BattleService battleService) {
        this.battleService = battleService;
    }

    public SseEmitter connect(String userId) {
        //String id = userId + "_" + System.currentTimeMillis();                            // emitter 의 id 를 뜻함. 필요 시 사용

        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);                               // emitter 생성 후 clients 에 집어넣기
        clients.put(userId,emitter);

        System.out.println("--- current clients : " + clients);
        System.out.println("--- current clients size : " + clients.size());

        emitter.onCompletion(() -> {
            System.out.println("@@@ onCompletion callback");
            clients.remove(userId);

            // TODO: 해당 emitter 가 참여중인 battle 찾기 -> (참여중인 battle 이 있다면) 참여중인 상대방에게 배틀 종료 알림 전송 -> battle 삭제 -> 상대방의 emitter 또한 삭제

//             문제 1: 현재 브라우저가 닫혔을때 바로 onCompletion 콜백이 호출되는게 아니라, sendToClient 에서 exception 발생 시 호출됨.
//                   => 새로운 방안) 브라우저 닫을 시 or 나가기버튼 클릭 시 이 콜백이 바로 호출되도록 바꿔주기 => 그러면 상대 유저가 문제 풀던 도중에 강제 종료되게 됨ㅠㅜ
//                      현재 상태 유지) A 유저와 B 유저가 동시에 창을 닫는 경우 battle 종료 처리 못함. battle 좀비.. battle 관리해주는 스케쥴러같은거 따로 돌려야할듯
//             문제 2: 브라우저 새로고침 시, 바로 onCompletion 콜백이 호출되지 않아서 동일 브라우저에서 새로고침 이후 새로 접속하면 새로고침 이전 emitter 와 배틀시작됨.. 안 돼...
//
//             [ 정리 ]
//             plan A. 브라우저종료/새로고침/나가기버튼 클릭 시 onCompletion 바로 호출되도록 해주는게 best (될련지 몰겠)
//             plan B. 현재 상태 유지 + 새로고침 문제는 노션 스택오버플로우 참고해서 해결 + battle 관리 스케쥴러 돌리기(현재 접속된 유저들 간의 배틀이 아닌 경우 종료시켜주기)

            System.out.println("%%%%%%%%%%%% 중지된 emitter : " + emitter + " userId: " + userId);
        });
        emitter.onTimeout(() -> {
            System.out.println("@@@ onTimeout callback");
            emitter.complete();                                                           // onCompletion() 콜백 호출
        });

        if (clients.size() == 2) {                                                       // 배틀 생성 (배틀 유형은 한문제 풀기)
            battleService.createBattle(BattleType.ONEQUESTION, clients);
            clients.clear();
        }

        SseUtil.sendToClient(emitter, "sse", "--------- EventStream Created. [userId=" + userId + "]");             // 503 에러를 방지하기 위한 더미 이벤트 전송

        return emitter;
    }
}
