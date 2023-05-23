package com.ll.gramgram.boundedContext.likeablePerson.repository;

import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
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

    public List<LikeablePerson> findQslByToInstaMember(InstaMember toInstaMember, @Nullable String gender, int attractiveTypeCode){
        return jpaQueryFactory.selectFrom(likeablePerson)
                .where(
                        likeablePerson.toInstaMember.eq(toInstaMember)
                                .and(gender.isEmpty() ? null : likeablePerson.fromInstaMember.gender.eq(gender))
                                .and(attractiveTypeCode == 0 ? null : likeablePerson.attractiveTypeCode.eq(attractiveTypeCode))
                )
                .orderBy(likeablePerson.id.desc())
                .fetch();
    }

    public List<LikeablePerson> sortQslBySortCode(InstaMember toInstaMember, int sortCode){
        JPAQuery<LikeablePerson> query = jpaQueryFactory.selectFrom(likeablePerson)
                .where(likeablePerson.toInstaMember.eq(toInstaMember));

        query = switch(sortCode) {
                    case 2 //날짜순
                            -> query.orderBy(likeablePerson.id.asc());
                    case 3 //인기가 많은 사람 순
                            -> query.orderBy(likeablePerson.fromInstaMember.likes.desc(), likeablePerson.id.desc());
                    case 4 //인기가 적은 사람 순
                            -> query.orderBy(likeablePerson.fromInstaMember.likes.asc(), likeablePerson.id.desc());
                    case 5 //1. 성별 순(여성 -> 남성 순) 2. 최신순
                            -> query.orderBy(likeablePerson.fromInstaMember.gender.desc(), likeablePerson.id.desc());
                    case 6 //1. 호감사유순(외모 -> 성격 순) 2. 최신순
                            -> query.orderBy(likeablePerson.attractiveTypeCode.asc(), likeablePerson.id.desc());
                    default //최신순
                            -> query.orderBy(likeablePerson.id.desc());
        };

        return query.fetch();
    }
}
