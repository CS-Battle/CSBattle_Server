package com.battle.csbattle.service;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.dto.AnswerDto;
import com.battle.csbattle.dto.QuestionDto;
import com.battle.csbattle.entity.Question;
import com.battle.csbattle.entity.QuestionType;
import com.battle.csbattle.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public QuestionDto getOneQuestion(Battle battle) {              // 한 문제 불러오기 (해당 battle 에 대해 출제해줄 문제 선정)
        Map<String,QuestionDto> battleQuestion = battle.getQuestions();
        List<Question> questions = questionRepository.findAll();

        Random random = new Random();
        int index = random.nextInt(questions.size());

        if(battleQuestion.size() != 0) { // 먼끝도와 같이 이미 문제가 batlle에 존재할때 함수 호출시 이미 존재하는 문제와 중복 검사하는 부분
            while(!battleQuestion.containsKey(questions.get(index).getId())){
                questions.remove(index);
                index = random.nextInt(questions.size());
            }
        }

        QuestionDto returnQuestion = QuestionDto.from(questions.get(index));
        battle.addQuestion(returnQuestion.getQuestionId(),returnQuestion);

        return returnQuestion;
    }

    public void getQuestions(Battle battle, int count){
        Map<String,QuestionDto> questionMap = battle.getQuestions();
        List<Question> questionList = questionRepository.findAll();
        count += questionMap.size();

        while(questionMap.size() < count){
            Random random = new Random();
            int index = random.nextInt(questionList.size());

            QuestionDto question = QuestionDto.from(questionList.get(index));
            String questionId = question.getQuestionId();

            if(!questionMap.containsKey(questionId)){
                questionMap.put(questionId, question);
                questionList.remove(index);
            }
        }

        battle.setQuestions(questionMap);
    }
    public Boolean checkAnswer(Long questionId, AnswerDto answer) {
        Question question = questionRepository.findById(questionId).get();

        Boolean isCorrect = false;
        if(question.getAnswer().equals(answer.getAnswer())){ isCorrect = true; }

        return isCorrect;
    }
}