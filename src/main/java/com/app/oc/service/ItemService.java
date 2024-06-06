package com.app.oc.service;

import com.app.oc.dto.fileDto.UploadFile;
import com.app.oc.dto.shoppingmal.DetailItemDto;
import com.app.oc.dto.shoppingmal.ItemFileRequestDto;
import com.app.oc.dto.shoppingmal.MainItemDto;
import com.app.oc.entity.File;
import com.app.oc.entity.Item;
import com.app.oc.entity.ShoppingMal;
import com.app.oc.repository.FileRepository;
import com.app.oc.repository.ItemRepository;
import com.app.oc.repository.ShopRepository;
import com.app.oc.util.FileStore;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ItemService {
    private final FileStore fileStore;
    private final FileService fileService;

    private final FileRepository fileRepository;
    private final ShopRepository shopRepositroy;
    private final ItemRepository itemRepository;

    @Autowired
    HttpSession session;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * Item 등록 / 수정
     */
    public String saveItem(ItemFileRequestDto itemFileRequestDto) throws IOException {

        // shoppingMal 조회 - 매핑 - 수정
        ShoppingMal shoppingMal = shopRepositroy.findById(itemFileRequestDto.getShopId()).orElseThrow();

        // 등록인지 수정인지 구분
        String name = "";

        // shop id
        // Item 등록
        Item item = null;
        if (itemFileRequestDto.getItemId() == null) {
            item = Item.createItem(itemFileRequestDto, shoppingMal);
            name = "등록";

        } else { // update
            name = "수정";
            // item_id
            item = itemRepository.findById(itemFileRequestDto.getItemId())
                    .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다."));

            // item update
            item.Itemupdate(itemFileRequestDto);

            // 기존 파일 삭제 -s3
            List<File> oldFiles = fileService.fileFindPerItem(item.getItemId());
            if (oldFiles != null) {
                for (File file : oldFiles) { // 파일 삭제
                    fileService.fileOneDelete(file.getStorefile());
                }
            }

        }

        // 파일 insert
        UploadFile thumb = fileStore.storeFile(itemFileRequestDto.getThumb(), true);

        LinkedList<UploadFile> list = new LinkedList<>();

        if (itemFileRequestDto.getImageFiles() != null) {
            List<UploadFile> files = fileStore.storeFiles(itemFileRequestDto.getImageFiles());
            list.addAll(files);
        }

        list.add(thumb);

        for (UploadFile file : list) {
            File fileOne = file.toEntity();

            // File 연관관계 매핑
            item.setFile(fileOne);

            itemRepository.save(item);
        }

        return name;
    }

    /**
     * Item 찾기
     *
     * 
     * @param id
     * @return
     */
    public Item findByItem(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("상품이 없습니다."));
    }

    /**
     * 썸네일만 추출(페이징)
     *
     * @param id
     * @return
     */
    // 여러개(썸네일만)
    public Page<MainItemDto> findByShopITem(Long id, Pageable pageable) {

        // item 페이징 List
        List<Item> getItems = itemRepository.getcontent(id, pageable);

        // id들로만 한번에
        List<Long> collectItemIds = getItems.stream().map(item -> item.getItemId()).collect(Collectors.toList());

        List<File> fileIn = fileRepository.findFileIn(collectItemIds);
        List<UploadFile> uploadFiles = fileIn.stream().map(file -> new UploadFile(file)).collect(Collectors.toList());

        Map<Long, UploadFile> fileMap = new HashMap<>();
        for (UploadFile uploadFile : uploadFiles) {
            if (uploadFile.getStoreFileName().startsWith("s_")) { // 썸네일만
                fileMap.put(uploadFile.getItem_seq(), uploadFile);
            }
        }

        List<MainItemDto> items = getMainItemDtos(getItems, fileMap);

        // 페이징으로 변환
        Page<MainItemDto> mainItemDtos = itemRepository.shopMainItems(items, id, pageable);
        List<MainItemDto> content = mainItemDtos.getContent();
        System.out.println(content);

        return mainItemDtos;

    }

    private static List<MainItemDto> getMainItemDtos(List<Item> getItems, Map<Long, UploadFile> fileMap) {
        return getItems.stream().map(item -> {
            MainItemDto mainItemDto = new MainItemDto(item);
            UploadFile uploadFile = fileMap.get(item.getItemId());
            mainItemDto.setUploadFile(uploadFile);
            return mainItemDto;
        }).collect(Collectors.toList());
    }

    /**
     * Item 상세페이지
     *
     * Myshop인지 확인
     */
    public DetailItemDto findDetailOne(Long id) {
        // Item
        Item itemAndFiles = itemRepository.findByIdWithFiles(id).orElseThrow(() -> new IllegalArgumentException("아이템이 없습니다."));
        Long shopId = itemAndFiles.getShoppingMal().getShopId();
        ShoppingMal findShop = shopRepositroy.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("shop이 없습니다."));

        DetailItemDto detailItemDto = new DetailItemDto(itemAndFiles, findShop.getShopName());
        String memberId = findShop.getMember().getMemberId();

        String sessionId = (String) session.getAttribute("id");

        if (memberId.equals(sessionId)) {
            detailItemDto.setMyshop(true);
        } else {
            detailItemDto.setMyshop(false);
        }

        List<File> files = itemAndFiles.getFiles();
        files.forEach(file -> itemAndFiles.setFile(file)); // 연관관계 매핑(file)연관관계

        List list = new ArrayList<>();
        files.stream().forEach(file -> {
            UploadFile uploadFile = new UploadFile(file);

            if (file.getStorefile().startsWith("s_")) {
                detailItemDto.setThumb(uploadFile);
            } else { // 그 외 파일들은 null일 수 있어서
                if (file != null) {
                    list.add(uploadFile);
                }
            }
        });

        // file이 있을 경우
        if (!list.isEmpty()) {
            detailItemDto.setImageFiles(list);
        }

        return detailItemDto;

    }

    /**
     * 
     * Item삭제
     * 
     * @param id item
     */
    public void DeleteOneItem(Long id) throws UnsupportedEncodingException {
        Item item = findByItem(id);

        List<File> files = fileService.fileFindPerItem(id);
        files.forEach(file -> item.setFile(file)); // 연관관계 매핑(file)연관관계

        // 기존 파일 삭제 - s3 삭제
        if (files != null) {
            for (File file : files) { // 파일 삭제npmnpm
                fileService.fileOneDelete(file.getStorefile());
            }
        }

        // item 삭제
        itemRepository.deleteById(id);
    }
}