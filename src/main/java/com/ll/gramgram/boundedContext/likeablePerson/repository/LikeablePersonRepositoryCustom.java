package com.ll.gramgram.boundedContext.likeablePerson.repository;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;

import java.util.List;
import java.util.Optional;

public interface LikeablePersonRepositoryCustom {
    Optional<LikeablePerson> findQslByFromInstaMemberIdAndToInstaMember_username(long fromInstaMemberId, String toInstaMemberUsername);

    List<LikeablePerson> findQslByToInstaMemberAndGender(List<LikeablePerson> likeablePeople, String gender);
    List<LikeablePerson> findQslByToInstaMemberAndAttractiveTypeCode(List<LikeablePerson> likeablePeople, int attractiveTypeCode);

    List<LikeablePerson> sortQslByOldCreateDate(List<LikeablePerson> likeablePeople);
    List<LikeablePerson> sortQslByMorePopularFromInstaMember(List<LikeablePerson> likeablePeople);
    List<LikeablePerson> sortQslByLessPopularFromInstaMember(List<LikeablePerson> likeablePeople);
    List<LikeablePerson> sortQslByGender(List<LikeablePerson> likeablePeople);
    List<LikeablePerson> sortQslByAttractiveType(List<LikeablePerson> likeablePeople);
}
