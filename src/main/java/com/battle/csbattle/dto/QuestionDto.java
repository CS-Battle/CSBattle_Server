package com.battle.csbattle.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDto {
    private String questionId;
    private String content;
    private String answer;

    @Builder
    public QuestionDto(String questionId, String content, String answer) {
        this.questionId = questionId;
        this.content = content;
        this.answer = answer;
    }
}
