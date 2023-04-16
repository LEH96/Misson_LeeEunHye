package com.ll.gramgram.boundedContext.likeablePerson.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/likeablePerson")
@RequiredArgsConstructor
public class LikeablePersonController {
    private final Rq rq;
    private final LikeablePersonService likeablePersonService;

    @AllArgsConstructor
    @Getter
    public static class AddForm {
        private final String username;
        private final int attractiveTypeCode;
    }

    @GetMapping("/add")
    public String showAdd() {
        return "usr/likeablePerson/add";
    }

    @PostMapping("/add")
    public String add(@Valid AddForm addForm) {
        RsData<LikeablePerson> likeRsData = likeablePersonService.like(rq.getMember(), addForm.getUsername(), addForm.getAttractiveTypeCode());

        if (likeRsData.isFail()) {
            return rq.historyBack(likeRsData);
        }

        return rq.redirectWithMsg("/likeablePerson/list", likeRsData);
    }

    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            List<LikeablePerson> likeablePeople = instaMember.getFromLikeablePeople();
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/list";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        //호감데이터 삭제 후 결과메세지를 RsData에 담는다
        RsData<LikeablePerson> deleteRsData = likeablePersonService.delete(rq.getMember(), id);

        //실패한 경우 실패 메시지 띄우기
        if (deleteRsData.isFail()) {
            return rq.historyBack(deleteRsData);
        }

        //성공한 경우 성공 메시지 띄우고 삭제된 결과 확인 가능
        return rq.redirectWithMsg("/likeablePerson/list", deleteRsData);
    }
}
