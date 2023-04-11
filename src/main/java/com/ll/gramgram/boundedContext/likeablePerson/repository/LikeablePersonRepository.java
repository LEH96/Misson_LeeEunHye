package com.ll.gramgram.boundedContext.likeablePerson.repository;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeablePersonRepository extends JpaRepository<LikeablePerson, Long> {
    List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId);
    Optional<LikeablePerson> findById(Long likeablePersonId);

    Optional<LikeablePerson> findByFromInstaMemberIdAndToInstaMemberId(Long fromInstaId, Long toInstaId);

    @Modifying
    @Query("UPDATE LikeablePerson lp SET lp.attractiveTypeCode = :attractiveTypeCode, lp.modifyDate = NOW() WHERE lp.id = :id")
    void updateAttractiveTypeCode(@Param("attractiveTypeCode") int attractiveTypeCode,@Param("id") Long id);
}
