package com.app.oc.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.app.oc.dto.ResultDto;
import com.app.oc.service.FileService;
import com.app.oc.util.S3Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final AmazonS3 amazonS3;
    private final S3Config s3Component;

    /**
     *
     * Item 파일 삭제
     * 파일만 삭제되게 구분(item삭제x)
     * UploadFile : datePath , filename
     *
     * OK
     *
     * 개발자 : 전유진
     *
     */

    @DeleteMapping("/deleteFile")
    public ResponseEntity<ResultDto> FileDelete(String storeFileName) throws UnsupportedEncodingException {
        fileService.fileOneDelete(storeFileName);
        return ResponseEntity.ok(new ResultDto("파일을 삭제하였습니다."));
    }

    /**
     * 파일 이미지 보여주기
     * 
     * @param fileName : 파일명(서버)
     * @return
     */

    @PostMapping("/display/{fileName}")
    public String display(@PathVariable String fileName) throws IOException {

        String string = amazonS3.getUrl(s3Component.getBucket(), fileName).toString();
        System.out.println("string = " + string);
        return string;

    }

}
