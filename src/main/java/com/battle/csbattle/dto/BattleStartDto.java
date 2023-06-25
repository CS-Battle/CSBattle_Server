package com.battle.csbattle.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class BattleStartDto {
    String battleId;
    String userId;
}
