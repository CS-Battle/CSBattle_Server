package com.battle.csbattle.controller;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.battle.BattleType;
import com.battle.csbattle.dto.AnswerDto;
import com.battle.csbattle.dto.AnswerResultDto;
import com.battle.csbattle.dto.QuestionDto;
import com.battle.csbattle.dto.UserDto;
import com.battle.csbattle.response.Response;
import com.battle.csbattle.response.StatusEnum;
import com.battle.csbattle.service.BattleService;
import com.battle.csbattle.service.QuestionService;
import com.battle.csbattle.util.SseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class BattleController {
    private final BattleService battleService;
    private final QuestionService questionService;

    public BattleController(BattleService battleService, QuestionService questionService) {
        this.battleService = battleService;
        this.questionService = questionService;
    }

    @GetMapping("/battle/one-question")
    public ResponseEntity<Response> oneQuestion(@RequestParam("battleId") String battleId) {
        Battle battle = battleService.findBattleById(battleId);

        QuestionDto question = questionService.getOneQuestion(battle);

        Response body = Response.builder()
                .status(StatusEnum.OK)
                .data(question)
                .message("get question success")
                .build();
        return new ResponseEntity<>(body, Response.getDefaultHeader(), HttpStatus.OK);
    }

    @GetMapping("/battle/questions")
    public ResponseEntity<Response> questions(
            @RequestParam("battleId") String battleId, @RequestParam("count") int count) {
        Battle battle = battleService.findBattleById(battleId);

        questionService.addQuestionsToBattle(battle, count);
        List<QuestionDto> questions = battle.getQuestions();

        Response body = Response.builder()
                .status(StatusEnum.OK)
                .data(questions)
                .message("add question to battle success")
                .build();
        return new ResponseEntity<>(body, Response.getDefaultHeader(), HttpStatus.OK);
    }

    @GetMapping("/battle/question")
    public ResponseEntity<Response> getQuestion(@RequestParam("battleId") String battleId){
        Battle battle = battleService.findBattleById(battleId);

        Integer index = battle.getOngoingQuestions().values().stream().findFirst().get();
        QuestionDto question = battle.getQuestions().get(index);

        Response body = Response.builder()
                .status(StatusEnum.OK)
                .data(question)
                .message("get question success")
                .build();
        return new ResponseEntity<>(body, Response.getDefaultHeader(), HttpStatus.OK);
    }

    @PostMapping("/battle/answer/")
    public ResponseEntity<Response> answer(
            @RequestBody AnswerDto answer) {
        System.out.println("=== answer submited");
        System.out.println("=== battle id : " + answer.getBattleId() + ", user : " + answer.getUserId() + ", questionId: " + answer.getQuestionId() + ", answer : " + answer.getAnswer());

        Battle battle = battleService.findBattleById(answer.getBattleId());
        Boolean isCorrect = questionService.checkAnswer(answer.getQuestionId(), answer);

        for (String key : battle.getPlayers().keySet()) {                           // 해당 배틀에 참여중인 상대방 & 자신에게 정답 여부 전달 (sse)
            SseEmitter emitter = battle.getPlayers().get(key).getEmitter();
            SseUtil.sendToClient(emitter,"answer-result", AnswerResultDto.builder()
                    .userId(answer.getUserId())
                    .questionId(answer.getQuestionId().toString())
                    .isCorrect(isCorrect)
                    .build());
        }

        if(isCorrect){
            if(battle.getType() == BattleType.GOTOEND){
                battle.increasingIndexOfOngoingQuestion(answer.getUserId());
            }else{
                for (String key : battle.getPlayers().keySet()) {
                    battle.increasingIndexOfOngoingQuestion(key);
                }
            }
        }

        Response body = Response.builder()
                .status(StatusEnum.OK)
                .data("answer submit success")
                .message("answer submit success")
                .build();
        return new ResponseEntity<>(body, Response.getDefaultHeader(), HttpStatus.OK);
    }
}
