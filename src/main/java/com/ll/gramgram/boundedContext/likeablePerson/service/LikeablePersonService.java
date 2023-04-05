package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if ( member.hasConnectedInstaMember() == false ) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(member.getInstaMember()) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    @Transactional
    public RsData<LikeablePerson> delete(Member member, long likeableId) {
        //호감표시 데이터의 id를 기반으로 LikeablePerson의 데이터를 가져옴
        LikeablePerson lp = likeablePersonRepository.findById(likeableId);

        //없는 데이터를 삭제하려는 경우
        if(lp == null){
            return RsData.of("F-2","잘못된 삭제 입니다.");
        }

        //로그인된 사용자의 이름과 호감표시 데이터의 from 사용자 (등록자)의 이름이 다른경우 권한없음 결과 반환
        if(!member.getInstaMember().getUsername().equals(lp.getFromInstaMember().getUsername())) {
            return RsData.of("F-1","삭제 권한이 없습니다.");
        }

        //권한이 확인되면 데이터를 삭제해주고 성공 결과 반환
        likeablePersonRepository.deleteById(likeableId);
        return RsData.of("S-1","해당 호감표시가 삭제되었습니다.");
    }

    public LikeablePerson findByFromId(long likeableId) {
        LikeablePerson lp = likeablePersonRepository.findById(likeableId);
        return lp;
    }
}
