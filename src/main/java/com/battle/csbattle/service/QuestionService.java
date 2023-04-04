package com.battle.csbattle.service;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.battle.BattleType;
import com.battle.csbattle.battle.UserStatus;
import com.battle.csbattle.dto.AnswerDto;
import com.battle.csbattle.dto.QuestionDto;
import com.battle.csbattle.dto.UserDto;
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
    public Boolean checkAnswer(AnswerDto answer, Battle battle) {
        UserDto player = battle.getPlayers().get(answer.getUserId());

        QuestionDto question = battle.getQuestionByUser(answer.getUserId());
        boolean isCorrect = false;

        if(question.getAnswer().equals(answer.getAnswer())){
            isCorrect = true;

            player.setUserStatus(UserStatus.AbleAnswer);
            if(battle.getType() == BattleType.GOTOEND){
                battle.increasingIndexByUserId(answer.getUserId());
            }else{
                battle.increasingIndex();
            }
        }

        return isCorrect;
    }
}