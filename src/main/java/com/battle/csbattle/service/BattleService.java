package com.battle.csbattle.service;

import com.battle.csbattle.battle.Battle;
import com.battle.csbattle.battle.BattleType;
import com.battle.csbattle.util.SseUtil;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@NoArgsConstructor
public class BattleService {
    private Map<String, Battle> battles = new ConcurrentHashMap<>();

    public void createBattle(BattleType type, Map<String, SseEmitter> clients) {
        this.deleteZombieBattles();

        Battle battle = Battle.create(type, clients);
        String battleId = battle.getId();

        battles.put(battleId, battle);

        System.out.println("~~~ battle " + battleId + " created with players ");

        for (String key : battle.getPlayers().keySet()) {
            SseEmitter emitter = battle.getPlayers().get(key);
            System.out.println(emitter);
            SseUtil.sendToClient(emitter,"battle-start",battleId);                // 해당 배틀의 참여자들에게 배틀 시작 알림 전송
        }

        System.out.println("~~~ total battles : " + battles);
        System.out.println("~~~ total battles.size : " + battles.size());
    }

    public void deleteBattleById(String id) {
        battles.remove(id);
    }

    public Battle findBattleById(String id) {
        return battles.get(id);
    }

    public Battle findBattleOfUser(String userId) {
        for (String battleId : battles.keySet()) {
            Battle battle = battles.get(battleId);
            for (String playerId : battle.getPlayers().keySet()) {
                if (playerId == userId) {
                    return battle;
                }
            }
        }
        return null;
    }

    public void deleteZombieBattles() {
        for (String battleId : battles.keySet()) {
            Battle battle = battles.get(battleId);
            for (String playerId : battle.getPlayers().keySet()) {
                SseEmitter emitter = battle.getPlayers().get(playerId);
                SseUtil.sendToClient(emitter,"checking-connection","checking connection");
            }
        }
    }
}
