package com.ll.gramgram.boundedContext.likeablePerson.repository;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.ll.gramgram.boundedContext.likeablePerson.entity.QLikeablePerson.likeablePerson;

@RequiredArgsConstructor
public class LikeablePersonRepositoryImpl implements LikeablePersonRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<LikeablePerson> findQslByFromInstaMemberIdAndToInstaMember_username(long fromInstaMemberId, String toInstaMemberUsername) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(likeablePerson)
                        .where(
                                likeablePerson.fromInstaMember.id.eq(fromInstaMemberId)
                                        .and(
                                                likeablePerson.toInstaMember.username.eq(toInstaMemberUsername)
                                        )
                        )
                        .fetchOne()
        );
    }

    public List<LikeablePerson> findQslByToInstaMemberAndGender(List<LikeablePerson> likeablePeople, String gender){
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(
                        likeablePerson.in(likeablePeople)
                                .and(likeablePerson.fromInstaMember.gender.eq(gender))
                )
                .fetch();
    }

    public List<LikeablePerson> findQslByToInstaMemberAndAttractiveTypeCode(List<LikeablePerson> likeablePeople, int attractiveTypeCode) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(
                        likeablePerson.in(likeablePeople)
                                .and(likeablePerson.attractiveTypeCode.eq(attractiveTypeCode))
                )
                .fetch();
    }

    public List<LikeablePerson> sortQslByOldCreateDate(List<LikeablePerson> likeablePeople) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.in(likeablePeople))
                .orderBy(likeablePerson.createDate.asc())
                .fetch();
    }

    public List<LikeablePerson> sortQslByMorePopularFromInstaMember(List<LikeablePerson> likeablePeople) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.in(likeablePeople))
                .orderBy(likeablePerson.fromInstaMember.likes.desc())
                .fetch();
    }

    public List<LikeablePerson> sortQslByLessPopularFromInstaMember(List<LikeablePerson> likeablePeople) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.in(likeablePeople))
                .orderBy(likeablePerson.fromInstaMember.likes.asc())
                .fetch();
    }

    public List<LikeablePerson> sortQslByGender(List<LikeablePerson> likeablePeople) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.in(likeablePeople))
                .orderBy(likeablePerson.fromInstaMember.gender.desc())
                .fetch();
    }

    public List<LikeablePerson> sortQslByAttractiveType(List<LikeablePerson> likeablePeople) {
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.in(likeablePeople))
                .orderBy(likeablePerson.attractiveTypeCode.asc())
                .fetch();
    }
}
