package com.ll.gramgram.boundedContext.likeablePerson.repository;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeablePersonRepository extends JpaRepository<LikeablePerson, Long> {
    List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId);

    Optional<LikeablePerson> findById(Long likeablePersonId);

    Optional<LikeablePerson> findByFromInstaMember_IdAndToInstaMember_Id(Long fromInstaId, Long toInstaId);

    List<LikeablePerson> findByToInstaMember_username(String toInstaMemberUsername);

    LikeablePerson findByFromInstaMemberIdAndToInstaMember_username(long fromInstaMemberId, String toInstaMemberUsername);

}
