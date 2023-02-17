package com.battle.csbattle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

@Entity
@Getter
@Setter
public class CsCategory {
    @javax.persistence.Id
    @GeneratedValue
    @Column(name = "category_id")
    private Long Id;
    private String name;
}
