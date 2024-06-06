package com.app.oc.service;


import com.app.oc.dto.event.EventRequestDto;
import com.app.oc.dto.event.MyPostResponseDto;
import com.app.oc.dto.event.ResponseLists;
import com.app.oc.dto.event.ResponseeventDto;
import com.app.oc.entity.*;
import com.app.oc.repository.EventRepository;
import com.app.oc.repository.MemberRepository;
import com.app.oc.repository.ShopRepository;
import com.app.oc.repository.repositoryImpl.ShopRepositoryImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EventService {


    private final EventRepository eventRepository;
    private final MemberRepository memberRepository;
    private final ShopRepository shopRepository;
    private final ShopRepositoryImpl shopRepositoryImpl;


    //게시글 등록
    public Event savePost(EventRequestDto requestDto, String memberId){
        Event post = requestDto.toEntity();
        post.setWriteDay(LocalDateTime.now());

        ShoppingMal findShop = shopRepositoryImpl.findByMember(memberId);
        post.setShoppingmall(findShop);

        if(post.getEventId() != null){
            return null;
        }
        return eventRepository.save(post);
    }


    //게시글 등록
    public Event updatePost(Long id, EventRequestDto requestDto){

        //조회
        Event target = eventRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        //업데이트
        target.updateEvent(requestDto);
        Event updated = eventRepository.save(target);

        return updated;
    }

    /**
     * 전체 폐점 할인점 조회
     * @return
     */
    public ResponseeventDto listAll(String state, HttpSession session) {

        String sessionId = (String) session.getAttribute("id");
        boolean isSeller = false;
        if (sessionId != null) {
            Member member = memberRepository.findById(sessionId).get();
            if (member.getRole().equals(MemberRole.SELLER)) {
                isSeller = true;
            }
        }

        List<Event> total = null;
        switch (state) {
            case "A": //전체
                total=  eventRepository.findAll();
                break;
            case "D": //할인
                total= eventRepository.findEventByEventType(EventType.D);
                break;
            case "C": //폐점
                total= eventRepository.findEventByEventType(EventType.C);
                break;
        }

        List<ResponseLists> lists = total.stream().map(e -> new ResponseLists(e)).collect(Collectors.toList());
        ResponseeventDto result = new ResponseeventDto(isSeller,lists);
        return result;
    }

    /**
     * 내가 작성한 게시글 리스트 조회
     * @param memberId
     * @return
     */
    public List<MyPostResponseDto> getMyEventPost(String memberId) {

        ShoppingMal findShop = shopRepository.findByMember(memberId);
        List<Event> findList = eventRepository.findAllByShoppingmall(findShop);

        return findList.stream().map(event -> {
           return MyPostResponseDto.myPostResponseDto(findShop, event);
        }).collect(Collectors.toList());
    }

    /**
     * 게시글 삭제
     * @param eventSeq
     */
    public void deletePost(Long eventSeq) {
        Event findEvent = eventRepository.findEventByEventId(eventSeq);
        eventRepository.deleteEventByEventId(findEvent.getEventId());
    }

    /**
     * 수정하려는 게시글의 기존 데이터 불러오기
     * @param id
     * @return
     */
    public Event getPost(Long id) {
        Event getEvent = eventRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        return getEvent;
    }
}
