package com.battle.csbattle.battle;

import com.battle.csbattle.dto.QuestionDto;
import com.battle.csbattle.dto.UserDto;
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
    private Map<String, UserDto> players = new HashMap<>();          // 참여 중인 플레이어
    private Map<String, QuestionDto> questions = new HashMap<>();       // 이 배틀에서의 문제 목록 (배틀 참여자들은 모두 동일한 문제를 풀어야 하기에 필요)
    private Map<String, Integer> ongoingQuestions = new HashMap<>();    // 현제 풀고 있는 문제의 인덱스

    public static Battle create(BattleType type, Map<String, UserDto> players) {
        Battle battle = new Battle();
        battle.id = UUID.randomUUID().toString();
        battle.type = type;
        for (String key : players.keySet()) {
            battle.players.put(key,players.get(key));
            battle.ongoingQuestions.put(key,0);
        }
        return battle;
    }

    public void addQuestion(String questionKey,QuestionDto questionDto) {
        this.questions.put(questionKey, questionDto);
    }
    public void deletePlayerById(String id){
        this.players.remove(id);
    }
}
