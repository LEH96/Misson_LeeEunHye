package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePersonUtils;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    private final long maxlikeablePersonNums = AppConfig.getLikeablePersonFromMax();

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {

        InstaMember fromInstaMember = member.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        //예외 처리 케이스를 확인하고 결과를 받아옴
        RsData<LikeablePerson> tryCreateRsData = tryCreateLikeablePerson(member, username, attractiveTypeCode);

        //실패했거나 수정인 경우 해당 결과 메세지를 반환해준다
        if(tryCreateRsData.isFail() || tryCreateRsData.getResultCode().equals("S-2"))
            return tryCreateRsData;

        //성공한 경우 호감 표시 등록
        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        //각 리스트에 새 호감표시 데이터 추가
        fromInstaMember.addFromLikeablePerson(likeablePerson);
        toInstaMember.addToLikeablePerson(likeablePerson);

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    private RsData<LikeablePerson> tryCreateLikeablePerson(Member member, String username, int newAttractiveTypeCode) {
        //인스타그램 아이디가 연결되었는지 확인
        if (!member.hasConnectedInstaMember()) {
            return RsData.of("F-1", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        //호감 표시하려는 아이디와 본인의 인스타그램 아이디가 같은 경우
        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-2", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember fromInstaMember = member.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        //호감 표시를 등록한 사람과 받은 사람의 아이디가 둘 다 같은 데이터가 존재하는지 확인
        Optional<LikeablePerson> sameLikeablePersonExists = likeablePersonRepository.findByFromInstaMember_IdAndToInstaMember_Id(fromInstaMember.getId(), toInstaMember.getId());

        //같은 데이터가 존재하는 경우
        if (sameLikeablePersonExists.isPresent()) {
            LikeablePerson likeablePerson = sameLikeablePersonExists.get();
            int oldAttractiveTypeCode = likeablePerson.getAttractiveTypeCode();

            //등록하려는 호감표시의 호감타입코드도 같은 경우
            if(oldAttractiveTypeCode == newAttractiveTypeCode)
                return RsData.of("F-3","중복된 호감 표시를 등록할 수 없습니다.");

            //등록하려는 호감표시의 호감타입코드를 다르게 수정하려고 하는 경우
            likeablePersonRepository.modifyAttractiveTypeCode(newAttractiveTypeCode, likeablePerson.getId());
            return RsData.of("S-2","%s의 호감 표시(%s -> %s)를 수정하였습니다.".formatted(username, getAttractionName(oldAttractiveTypeCode), getAttractionName(newAttractiveTypeCode)));
        }

        //11명 이상의 호감표시를 등록하려고 하는 경우
        if (member.getInstaMember().getFromLikeablePeople().size() >= maxlikeablePersonNums) {
            return RsData.of("F-4","최대 호감 표시 수는 %d개 입니다.".formatted(maxlikeablePersonNums));
        }

        //정상적인 등록
        return RsData.of("S-1", "생성이 가능합니다.");
    }

    @Transactional
    public RsData<LikeablePerson> delete(Member member, Long id) {
        //호감 데이터의 id로 LikeablePerson 객체를 가져옴
        Optional<LikeablePerson> olp = likeablePersonRepository.findById(id);

        //없는 데이터를 삭제하려는 경우
        if(olp.isEmpty()){
            return RsData.of("F-1","잘못된 삭제 입니다.");
        }

        LikeablePerson likeablePerson = olp.get();
        long actorInstaMemberId = member.getInstaMember().getId();
        long fromInstaMemberId = likeablePerson.getFromInstaMember().getId();

        //로그인된 사용자의 이름과 호감표시 데이터의 from 사용자(등록자)의 이름이 다른경우 권한없음 결과 반환
        if(actorInstaMemberId != fromInstaMemberId) {
            return RsData.of("F-2","삭제 권한이 없습니다.");
        }

        //권한이 확인되면 데이터를 삭제해주고 성공 결과 반환
        likeablePersonRepository.delete(likeablePerson);
        return RsData.of("S-1","%s님에 대한 호감을 삭제하였습니다.".formatted(likeablePerson.getToInstaMemberUsername()));
    }

    public String getAttractionName(int attractiveTypeCode) {
        return LikeablePersonUtils.getAttractiveTypeDisplayName(attractiveTypeCode);
    }

    public Optional<LikeablePerson> findById(Long id) {
        return likeablePersonRepository.findById(id);
    }
}
