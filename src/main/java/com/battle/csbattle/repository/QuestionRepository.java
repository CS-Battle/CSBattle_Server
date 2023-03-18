package com.battle.csbattle.repository;

import com.battle.csbattle.dto.QuestionDto;
import com.battle.csbattle.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findById(Long Id);
    Long countBy();
    @Query(value = "SELECT * FROM question order by RAND() limit 1",nativeQuery = true)
    Question findRandomOne();

    List<Question> findAll();
}