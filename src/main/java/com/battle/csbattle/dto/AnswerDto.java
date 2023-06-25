package com.battle.csbattle.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDto {
    private String userId;
    private String answer;

    @Builder
    public AnswerDto(String userId, String answer) {
        this.userId = userId;
        this.answer = answer;
    }
}
