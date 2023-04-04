package com.battle.csbattle.controller;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.battle.UserStatus;
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

import java.util.TimerTask;


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
        Battle battle = battleService.findBattleById(battleId);
        UserDto player = battle.getPlayers().get(userId);

        QuestionDto questionDto = QuestionDto.clientForm(battle.getQuestionByUser(userId)); // 배포시 변경 되야함
        SseUtil.sendToClient(player.getEmitter(),"Question",questionDto);

        player.setUserStatus(UserStatus.AbleAnswer);
        player.getAnswerTimer().schedule(new TimerTask() {
            @Override
            public void run() {
                log.info(" 제한시간 만료" + " [ userID : " + userId + ", battleId : " + battleId + " ]");
                player.setUserStatus(UserStatus.Gaming);
                SseUtil.sendToClient(player.getEmitter(),"timeOut","제한시간이 만료되었습니다.");
            }
        },1000*10);

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
        System.out.println("=== battle id : " + answer.getBattleId() + ", user : " + answer.getUserId() + ", answer : " + answer.getAnswer());
        Response body;

        Battle battle = battleService.findBattleById(answer.getBattleId());
        UserDto player = battle.getPlayers().get(answer.getUserId());

        if (player.getUserStatus() == UserStatus.AbleAnswer) {
            int questionIdx = battle.getQuestionIdxByUserId(answer.getUserId()) + 1;
            Boolean isCorrect = questionService.checkAnswer(answer, battle);

            for (String key : battle.getPlayers().keySet()) {                           // 해당 배틀에 참여중인 상대방 & 자신에게 정답 여부 전달 (sse)
                SseEmitter emitter = battle.getPlayers().get(key).getEmitter();
                SseUtil.sendToClient(emitter, "answer-result",
                        AnswerResultDto.builder()
                        .userId(answer.getUserId())
                        .questionIdx(Integer.toString(questionIdx))
                        .isCorrect(isCorrect)
                        .build());
            }

            body = Response.builder()
                    .status(StatusEnum.OK)
                    .data("answer submit success")
                    .message("answer submit success")
                    .build();
        } else {
            body = Response.builder()
                    .status(StatusEnum.BAD_REQUEST)
                    .message("timeout")
                    .build();
        }
        return new ResponseEntity<>(body, Response.getDefaultHeader(), HttpStatus.OK);
    }
}
