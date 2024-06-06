package com.app.oc.service;

import com.app.oc.dto.shoppingmal.*;
import com.app.oc.entity.*;
import com.app.oc.repository.ItemRepository;
import com.app.oc.repository.MemberRepository;
import com.app.oc.repository.ShopRepository;
import com.app.oc.repository.repositoryImpl.ShopRepositoryImpl;
import com.app.oc.util.EmailUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {

    @Autowired
    HttpSession session;

    private final ItemRepository itemRepository;
    private final ShopRepository shopRepository;

    private final ShopRepositoryImpl shopRepositoryImpl;
    private final MemberRepository memberRepository;
    private final ItemService itemService;

    private final EmailUtil emailUtil;

    //
    // // item
    // public List<Item> findPerItem(Long id) {
    // return ItemRepository.findByshopItem(id);
    // }

    /**
     * 입점 신청 기능
     * 입점 신청 성공 시, 입점 신청할 때 입력한 이메일로 메일 발송.
     * 
     * @param requestDto
     * @return
     */
    @Transactional
    public SellerResponseDto sellerSignup(SellerRequestDto requestDto, HttpSession session) {
        requestDto.setMemberId((String) session.getAttribute("id"));

        Map<String, Object> result = emailUtil.sendEmail(requestDto.getEmail());

        if (result.get("resultCode").equals(200)) {
            Member findMember = shopRepositoryImpl.findMemberByMemberId(requestDto.getMemberId());
            findMember.setRole(MemberRole.SELLER);
            shopRepositoryImpl.saveMember(findMember);
            ShoppingMal shoppingmal = requestDto.toShoppingmal();

            shoppingmal.addShop(findMember); // 연관매핑

            shoppingmal.setMember(findMember);
            shoppingmal.setApproval(Approval.Y);
            shopRepositoryImpl.save(shoppingmal);

            return SellerResponseDto.of(findMember, shoppingmal);
        } else {
            throw new RuntimeException("입점 신청 접수가 안됐습니다. 한 번 더 입력해주세요");
        }
    }

    // 각 shop정보
    public ShoppingMal findMyshopById(Long id) {
        ShoppingMal shoppingMal = shopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("쇼핑몰이 없습니다.")); // 영속화

        return shoppingMal;
    }

    /**
     * Item 찾기
     * 
     * @param id
     * @return
     */
    public Item findByItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item이 없습니다.."));
    }

    /**
     * Member 찾기
     * 
     * @param id
     * @return
     */
    public Member findByMember(String id) {
        return memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Id를 찾지 못했습니다."));
    }

    /**
     *
     * 쇼핑몰 상세 페이지
     * 
     * @param id
     *
     */
    public MyShoppingmalDto findShopDetail(Long id, Pageable pageable) {

        // 패치로 : member, shop들 전체
        ShoppingMal findShop = findMyshopById(id);

        // 세션으로 Id가지고 옴
        String sessionId = (String) session.getAttribute("id");

        boolean myShop = false;
        if (findShop.getMember().getMemberId().equals(sessionId)) {
            myShop = true;
        }

        MyShoppingmalDto result = new MyShoppingmalDto(findShop, myShop);

        // Item 구하기
        Page<MainItemDto> byShopITem = itemService.findByShopITem(id, pageable);
        result.setMainItemDtoList(byShopITem);

        return result;
    }

    // 매장 정보 작성 하는 메서드, 불러오는 메서드.
    // 매장 정보 작성 메서드
    @Transactional
    public ShopIntroductionResponseDto saveIntroduction(Long shopId, ShopIntroductionRequestDto requestDto) {

        ShoppingMal findShop = shopRepositoryImpl.findByShopId(shopId);

        findShop.setShopLogo(requestDto.getShopLogo());
        findShop.setStyle(requestDto.getStyle());
        findShop.setContent(requestDto.getContent());

        shopRepositoryImpl.save(findShop);

        ShoppingMal newFindShop = shopRepositoryImpl.findByShopId(shopId);

        return ShopIntroductionResponseDto.introductionResponseDto(newFindShop);
    }

    @org.springframework.transaction.annotation.Transactional
    public ShopIntroductionResponseDto getIntroduction(Long shopId) {

        return ShopIntroductionResponseDto.introductionResponseDto(shopRepositoryImpl.findByShopId(shopId));
    }

    /**
     * 이미지 파일로 받은 이미지 데이터를 Base64로 변환하는 함수
     */
    // @SneakyThrows
    // private byte[] encodeImageToBase64(MultipartFile image) {
    // byte[] encodedImage = new byte[0];
    //
    // if (image != null) {
    // Base64.Encoder encoder = Base64.getEncoder();
    // encodedImage = encoder.encode(image.getBytes());
    // }
    // return encodedImage;
    // }
}