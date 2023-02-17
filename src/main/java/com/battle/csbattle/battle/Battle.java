package com.battle.csbattle.battle;

import com.battle.csbattle.dto.QuestionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class Battle {
    private String id;                                                  // 배틀 id
    private BattleType type;                                            // 배틀 유형
    private Map<String, SseEmitter> players = new HashMap<>();          // 참여 중인 플레이어
    private List<QuestionDto> questions = new ArrayList<>();            // 이 배틀에서의 문제 목록 (배틀 참여자들은 모두 동일한 문제를 풀어야 하기에 필요)

    public static Battle create(BattleType type, Map<String, SseEmitter> clients) {
        Battle battle = new Battle();
        battle.id = UUID.randomUUID().toString();
        battle.type = type;
        for (String key : clients.keySet()) {
            battle.players.put(key,clients.get(key));
        }
        return battle;
    }

    public void addQuestion(QuestionDto questionDto) {
        this.questions.add(questionDto);
    }
}
