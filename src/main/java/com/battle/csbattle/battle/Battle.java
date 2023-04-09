package com.battle.csbattle.battle;

import com.battle.csbattle.dto.QuestionDto;
import com.battle.csbattle.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class Battle {
    public static int MAX_ANSWER_COUNT = 2;
    private String id;                                                  // 배틀 id
    private BattleType type;                                            // 배틀 유형
    private Map<String, UserDto> players = new HashMap<>();          // 참여 중인 플레이어
    private List<QuestionDto> questions = new ArrayList<>();       // 이 배틀에서의 문제 목록 (배틀 참여자들은 모두 동일한 문제를 풀어야 하기에 필요)
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

    public void deletePlayerById(String id){
        UserDto player = this.players.get(id);
        player.setEmitter(null);
        player.setOpponent(null);
        this.players.remove(id);
    }

    public QuestionDto getQuestionByUser(String userId){
        return questions.get(ongoingQuestions.get(userId));
    }
    public void increasingIndexByUserId(String userId){ ongoingQuestions.replace(userId, ongoingQuestions.get(userId) + 1);}
    public void increasingIndex(){
        ongoingQuestions.replaceAll((k, v) -> v + 1);
    }
    public Integer getQuestionIdxByUserId(String userId){return ongoingQuestions.get(userId);}
}
