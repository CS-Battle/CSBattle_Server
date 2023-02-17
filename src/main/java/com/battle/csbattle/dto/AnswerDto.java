package com.battle.csbattle.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDto {
    private String battleId;
    private String userId;
    private String questionId;
    private String answer;

    @Builder
    public AnswerDto(String battleId, String userId, String questionId, String answer) {
        this.battleId = battleId;
        this.userId = userId;
        this.questionId = questionId;
        this.answer = answer;
    }
}
