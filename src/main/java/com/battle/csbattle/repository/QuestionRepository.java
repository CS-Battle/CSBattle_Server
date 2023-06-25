package com.battle.csbattle.repository;

import com.battle.csbattle.entity.Question;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findById(Long Id);
    Long countBy();
    @Query(value = "SELECT * FROM question order by RAND() limit :count",nativeQuery = true)
    Question findRandom(@Param("count") Integer count); // TODO : question id 리스트를 받아와 where not in을 이용하여 중복체크하여 문제가져오기 기능 추가하기
    @EntityGraph(attributePaths={"csCategory","questionType"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Question> findAll();
}