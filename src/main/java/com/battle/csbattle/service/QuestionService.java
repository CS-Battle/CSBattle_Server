package com.battle.csbattle.service;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.battle.BattleStatus;
import com.battle.csbattle.dto.AnswerDto;
import com.battle.csbattle.dto.QuestionDto;
import com.battle.csbattle.entity.Question;
import com.battle.csbattle.entity.QuestionType;
import com.battle.csbattle.repository.QuestionRepository;
import com.battle.csbattle.util.SseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;
    // TODO: 함수 작성
    public QuestionDto getOneQuestionByQuestionType(QuestionType questionType) { // TODO : 코드 추가하기
        QuestionDto questionDto = new QuestionDto();

        return questionDto;
    }

    public QuestionDto getQuestionByUserIndex(Battle battle, String userId){
        Integer index = battle.getOngoingQuestions().get(userId);
        List<QuestionDto> questions = battle.getQuestions();
        QuestionDto questionDto = questions.get(index);
        return questionDto;
    }

    public void addQuestionsToBattle(Battle battle, int count){
        List<QuestionDto> questions = battle.getQuestions();
        List<Question> questionList = questionRepository.findAll();
        count += questions.size();

        while(questions.size() < count){
            Random random = new Random();
            int index = random.nextInt(questionList.size());

            QuestionDto question = QuestionDto.from(questionList.get(index));

            if(!questions.contains(question)){
                questions.add(question);
                questionList.remove(index);
            }
        }

        battle.setQuestions(questions);
    }
    public Boolean checkAnswer(Long questionId, AnswerDto answer) {
        Question question = questionRepository.findById(questionId).get();

        Boolean isCorrect = false;
        if(question.getAnswer().equals(answer.getAnswer())){ isCorrect = true; }

        return isCorrect;
    }
}