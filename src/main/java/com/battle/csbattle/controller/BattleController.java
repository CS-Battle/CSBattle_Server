package com.battle.csbattle.controller;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.dto.AnswerDto;
import com.battle.csbattle.dto.AnswerResultDto;
import com.battle.csbattle.dto.QuestionDto;
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

import java.util.Map;

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
    public ResponseEntity<Response> Question(
            @RequestParam("battleId") String battleId, @RequestParam("count") int count) {
        Battle battle = battleService.findBattleById(battleId);

        questionService.getQuestions(battle, count);
        Map<String, QuestionDto> questions = battle.getQuestions();

        // count 만큼 현제 문제 수 변화 테스트 부분
        Map<String, Integer> ongoingQuestions = battle.getOngoingQuestions();
        for (String key : battle.getPlayers().keySet()) {
            ongoingQuestions.replace(key, ongoingQuestions.get(key) + count);
        }



        Response body = Response.builder()
                .status(StatusEnum.OK)
                .data(questions)
                .message("get question success")
                .build();
        return new ResponseEntity<>(body, Response.getDefaultHeader(), HttpStatus.OK);
    }

    @PostMapping("/battle/answer")
    public ResponseEntity<Response> answer(@RequestBody AnswerDto answer) {
        System.out.println("=== answer submited");
        System.out.println("=== battle id : " + answer.getBattleId() + ", user : " + answer.getUserId() + ", questionId: " + answer.getQuestionId() + ", answer : " + answer.getAnswer());

        Battle battle = battleService.findBattleById(answer.getBattleId());

        Boolean isCorrect = questionService.checkAnswer(Long.parseLong(answer.getQuestionId()), answer);

        for (String key : battle.getPlayers().keySet()) {                           // 해당 배틀에 참여중인 상대방 & 자신에게 정답 여부 전달 (sse)
            SseEmitter emitter = battle.getPlayers().get(key).getEmitter();
            SseUtil.sendToClient(emitter,"answer-result", AnswerResultDto.builder()
                    .userId(answer.getUserId())
                    .questionId(answer.getQuestionId())
                    .isCorrect(isCorrect)
                    .build());
        }

        Response body = Response.builder()
                .status(StatusEnum.OK)
                .data("answer submit success")
                .message("answer submit success")
                .build();
        return new ResponseEntity<>(body, Response.getDefaultHeader(), HttpStatus.OK);
    }


}
