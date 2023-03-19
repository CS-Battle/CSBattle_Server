package com.battle.csbattle.controller;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.battle.BattleStatus;
import com.battle.csbattle.battle.BattleType;
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


@RestController
@Slf4j
public class BattleController {
    private final BattleService battleService;
    private final QuestionService questionService;

    public BattleController(BattleService battleService, QuestionService questionService) {
        this.battleService = battleService;
        this.questionService = questionService;
    }

    @GetMapping("/battle/question")
    public ResponseEntity<Response> getQuestion(
            @RequestParam("battleId") String battleId, @RequestParam("userId") String userId)
    {
        log.info("question : " + battleId);
        Battle battle = battleService.findBattleById(battleId);

        QuestionDto questionDto = questionService.getQuestionByUserIndex(battle, userId);
        SseUtil.sendToClient(battle.getPlayers().get(userId).getEmitter(),"Question",questionDto);


        Thread thread = new Thread(() ->{
            try {
                Thread.sleep(1000*10);
            }catch (InterruptedException e){
                System.out.println(e.getMessage());
            }
            log.info("제한시간 만료");
            battle.setBattleStatus(BattleStatus.Gaming);
            SseUtil.sendToClient(battle.getPlayers().get(userId).getEmitter(),"timeLimit","제한시간이 만료되었습니다.");

        });
        thread.start();


        Response body = Response.builder()
                .status(StatusEnum.OK)
                .message("Get Question Success")
                .data(questionDto)
                .build();
        return new ResponseEntity<>(body, Response.getDefaultHeader(), HttpStatus.OK);
    }

    @PostMapping("/battle/answer")
    public ResponseEntity<Response> answer(
            @RequestBody AnswerDto answer) {
        System.out.println("=== answer submitted");
        System.out.println("=== battle id : " + answer.getBattleId() + ", user : " + answer.getUserId() + ", questionId: " + answer.getQuestionId() + ", answer : " + answer.getAnswer());
        Response body;

        Battle battle = battleService.findBattleById(answer.getBattleId());

        if (battle.getBattleStatus() == BattleStatus.AbleAnswer) {
            Boolean isCorrect = questionService.checkAnswer(answer.getQuestionId(), answer);

            for (String key : battle.getPlayers().keySet()) {                           // 해당 배틀에 참여중인 상대방 & 자신에게 정답 여부 전달 (sse)
                SseEmitter emitter = battle.getPlayers().get(key).getEmitter();
                SseUtil.sendToClient(emitter, "answer-result", AnswerResultDto.builder()
                        .userId(answer.getUserId())
                        .questionId(answer.getQuestionId().toString())
                        .isCorrect(isCorrect)
                        .build());
            }

            if (isCorrect) {
                if (battle.getType() == BattleType.GOTOEND) {
                    battle.increasingIndexOfOngoingQuestion(answer.getUserId());
                } else {
                    for (String key : battle.getPlayers().keySet()) {
                        battle.increasingIndexOfOngoingQuestion(key);
                    }
                }
            }

            body = Response.builder()
                    .status(StatusEnum.OK)
                    .data("answer submit success")
                    .message("answer submit success")
                    .build();
        } else {
            body = Response.builder()
                    .status(StatusEnum.BAD_REQUEST)
                    .message("get question failed")
                    .build();
        }
        return new ResponseEntity<>(body, Response.getDefaultHeader(), HttpStatus.OK);
    }
}
