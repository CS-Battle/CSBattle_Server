package com.battle.csbattle.dto;

import com.battle.csbattle.entity.Question;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDto {
    private Long questionId;
    private String content;
    private String answer;

    @Builder
    public QuestionDto(Long questionId, String content, String answer) {
        this.questionId = questionId;
        this.content = content;
        this.answer = answer;
    }

    public static QuestionDto from(Question question){
        return QuestionDto.builder()
                .questionId(question.getId())
                .content(question.getContent())
                .answer(question.getAnswer())
                .build();
    }

    public static QuestionDto clientForm(QuestionDto questionDto){
        return QuestionDto.builder()
                .content(questionDto.getContent())
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        QuestionDto questionDto = (QuestionDto) obj;

        return Objects.equals(questionDto.getQuestionId(), this.questionId);
    }
}
