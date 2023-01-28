package com.battle.csbattle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Type {
    @Id
    @GeneratedValue
    @Column(name = "type_id")
    private Long Id;
    private String name;
}
