package com.battle.csbattle.util;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.dto.UserDto;
import com.battle.csbattle.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class Scheduler {
    @Async
    @Scheduled(fixedDelay = 1000*60) //60초
    public void schedulerFixedDelayTask() {
        Map<String,UserDto> allPlayers= SseService.getAllPlayers();
        System.out.println("connecting-check scheduler: " + System.currentTimeMillis()/1000);
        for(String key:allPlayers.keySet()){
            SseUtil.sendToClient(allPlayers.get(key).getEmitter(),"Connecting Check","Connecting Check");
        }
    }
}
