package com.battle.csbattle.util;

import com.battle.csbattle.battle.BattleType;
import com.battle.csbattle.battle.UserStatus;
import com.battle.csbattle.dto.UserDto;
import com.battle.csbattle.service.BattleService;
import com.battle.csbattle.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class Scheduler {
    @Autowired
    private  BattleService battleService;
    @Async
    @Scheduled(fixedDelay = 1000*60) //60초
    public void deleteZombieBattleScheduler() {
        Map<String,UserDto> allPlayers= SseService.getAllPlayers();
        System.out.println("connecting-check scheduler: " + System.currentTimeMillis()/1000);
        for(String key:allPlayers.keySet()){
            SseUtil.sendToClient(allPlayers.get(key).getEmitter(),"Connecting Check","Connecting Check");
        }
    }

    @Async
    @Scheduled(fixedDelay = 1000*10) //10초
    public void playerQueueScheduler() {
        int count=0;
        System.out.println("playerQueue Scheduler : " + System.currentTimeMillis()/1000);
        while (SseService.waitingPlayers.size()>=2) {
            List<String> userQueue=new ArrayList<>();
            Map<String,UserDto> playerGroup=new ConcurrentHashMap<>();
            for (String playerId:SseService.waitingPlayers.keySet()){
                SseEmitter checkingEmitter = SseService.waitingPlayers.get(playerId).getEmitter();
                if(SseUtil.sendToClient(checkingEmitter, "checking-connection", "checking connection")){
                   userQueue.add(playerId);
                }
                if(userQueue.size()==2) break;
            }
            for(String playerId:userQueue) {
                playerGroup.put(playerId,SseService.waitingPlayers.get(playerId));
                SseService.waitingPlayers.remove(playerId);
            }
            battleService.createBattle(BattleType.ONEQUESTION, playerGroup);
            for (String playerId : playerGroup.keySet()) {
                playerGroup.get(playerId).setUserStatus(UserStatus.Gaming);
            }
        }
        System.out.println("@@@Waiting Players : "+ SseService.waitingPlayers.toString());


    }
}
