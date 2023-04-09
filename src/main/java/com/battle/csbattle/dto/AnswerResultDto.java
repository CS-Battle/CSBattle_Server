package com.battle.csbattle.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerResultDto {
    private String userId;
    private String questionIdx;
    private Boolean isCorrect;

    @Builder
    public AnswerResultDto(String userId, String questionIdx, Boolean isCorrect) {
        this.userId = userId;
        this.questionIdx = questionIdx;
        this.isCorrect = isCorrect;
    }
}
