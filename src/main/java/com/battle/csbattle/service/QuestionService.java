package com.battle.csbattle.service;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.dto.AnswerDto;
import com.battle.csbattle.dto.QuestionDto;
import com.battle.csbattle.entity.Question;
import com.battle.csbattle.entity.QuestionType;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class QuestionService {

    // TODO: 함수 작성
    public QuestionDto getOneQuestionByQuestionType(QuestionType questionType) {
        QuestionDto questionDto = new QuestionDto();

        return questionDto;
    }

    // TODO: 1. getOneQuestion -> getQuestion 으로 변경
    //       2. battle 뒤에 인자로 count 추가해서 count 개만큼 문제 불러오는 함수로 변경
    public QuestionDto getOneQuestion(Battle battle) {              // 한문제배틀 문제 불러오기 (해당 battle 에 대해 출제해줄 문제 선정)
        QuestionDto returningQuestion;

        if (battle.getQuestions().isEmpty()) {
            System.out.println("*** 이 배틀의 문제 생성");

            QuestionDto question = QuestionDto.builder()           // TODO: 문제 DB (QuestionRepository)에서 문제 한개 불러오기 - 현재는 DB가 없기에, QuestionDto 를 생성해주는것으로 대신
                    .questionId("question1")
                    .content("문제를 풀어봐요")
                    .answer("답")
                    .build();

            battle.addQuestion(question);
            returningQuestion = question;
        }
        else {
            System.out.println("*** 이 배틀의 문제 불러오기");

            returningQuestion = battle.getQuestions().get(0);
        }
        return returningQuestion;
    }

    public Boolean checkAnswer(Battle battle, AnswerDto answer) {        // 정답 여부 확인
        QuestionDto question = battle.getQuestions().get(0);            // TODO: 여기 변경

        Boolean isCorrect = false;
        if(question.getAnswer().equals(answer.getAnswer())){ isCorrect = true; }

        return isCorrect;
    }
}
