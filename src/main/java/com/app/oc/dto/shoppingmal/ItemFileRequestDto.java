package com.app.oc.dto.shoppingmal;



import com.app.oc.entity.SellState;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 필요 ID
 * :item id, shoppingMal id
 */

@Data
public class ItemFileRequestDto {

    //Item id
    private Long itemId; //등록 , 수정 구분

    //Item
    @NotEmpty(message = "상품명을 입력하세요")
    private String itemTitle;
    
    @NotEmpty(message = "상품 가격을 입력하세요")
    private Integer price;

    @NotEmpty(message = "상품 내용을 입력하세요")
    private  String content;

    //수정 시 변경 할 수 있음
    private SellState sellState;

    //cateGoryNumg
    @NotEmpty(message = "카테고리를 입력하세요")
    private Integer category;

    //shop_id
    private Long shopId;

//파일들
    private List<MultipartFile> imageFiles;//상세 이미지들
    
    @NotEmpty(message = "썸네일 이미지는 필수 입니다")
    private MultipartFile thumb; //썸네일 이미지

}
