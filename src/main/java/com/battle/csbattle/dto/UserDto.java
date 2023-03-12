package com.battle.csbattle.dto;

import com.battle.csbattle.battle.Battle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Battle battle;
    private SseEmitter emitter;
    private UserDto Opponent;

    public UserDto(Battle battle, SseEmitter emitter, UserDto opponent) {
        this.battle = battle;
        this.emitter = emitter;
        Opponent = opponent;
    }
}
