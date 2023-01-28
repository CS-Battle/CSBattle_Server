package com.battle.csbattle.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue
    @Column(name = "question_id")
    private Long Id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private Type type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Integer timeLimit;
    private String content;
    private String description;
    private String attachmentPath;
    private String answer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Selection> selectionList = new ArrayList<>();
}
