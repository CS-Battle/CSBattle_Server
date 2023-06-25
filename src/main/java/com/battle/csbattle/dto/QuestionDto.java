package com.battle.csbattle.dto;

import com.battle.csbattle.entity.Question;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDto {
    private Long questionId;
    private String content;
    private String answer;
    private String csCategory;
    private String questionType;
    private ArrayList<String> description;
    private String attachmentPath;


    @Builder
    public QuestionDto(
            Long questionId, String content, String answer,
            String csCategory, String questionType, ArrayList<String> description,
            String attachmentPath) {
        this.questionId = questionId;
        this.content = content;
        this.answer = answer;
        this.csCategory = csCategory;
        this.questionType = questionType;
        this.description = description;
        this.attachmentPath = attachmentPath;
    }


    public static QuestionDto from(Question question){
        ArrayList<String> descriptionList = new ArrayList<>();
        String[] split = question.getDescription().split(",");
        for (String description : split) {
            descriptionList.add(description);
        }
        return QuestionDto.builder()
                .questionId(question.getId())
                .content(question.getContent())
                .answer(question.getAnswer())
                .csCategory(question.getCsCategory().getName())
                .questionType(question.getQuestionType().getName())
                .description(descriptionList)
                .attachmentPath(question.getAttachmentPath())
                .build();
    }

    @Override
    public boolean equals(Object obj) {
        QuestionDto questionDto = (QuestionDto) obj;

        return Objects.equals(questionDto.getQuestionId(), this.questionId);
    }
}
