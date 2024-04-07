package com.example.excel.controller;

import com.example.domain.ExcelVO;
import com.example.excel.util.ExcelUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ExcelController {

    /**
     * @Method         : download
     * @Description    : 엑셀 다운로드
     * @Author         : donguk
     * @Date           : 2024. 04. 07.
     * @param          : res, fileName
     * @return         : ResponseEntity
     */
    @PostMapping("/excel/download")
    public ResponseEntity<Map<String, Object>> download(HttpServletResponse res, String fileName) {

        ExcelUtils excelUtils = new ExcelUtils();
        Map<String, Object> map = new HashMap<>();

        try {
            excelUtils.excelDownload(res, fileName);
            map.put("data", "success");

            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("data", "fail");

            return new ResponseEntity<>(map, HttpStatus.EXPECTATION_FAILED);
        }

    }

    /**
     * @Method         : imageUploadToExcel
     * @Description    : 엑셀로 이미지 업로드
     * @Author         : donguk
     * @Date           : 2024. 04. 07.
     * @param          : excelVO
     * @return         : ResponseEntity
     */
    @PostMapping("/excel/upload/image")
    public ResponseEntity<Map<String, Object>> imageUploadToExcel(@RequestBody ExcelVO excelVO) throws Exception {

        Map<String, Object> map = new HashMap<>();
        ExcelUtils excelUtils = new ExcelUtils();

        try {
            excelUtils.uploadWithImage(excelVO.getImagePath(), excelVO.getOutputFilePath());
            map.put("data", "success");

            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("data", "fail");
            return new ResponseEntity<>(map, HttpStatus.EXPECTATION_FAILED);
        }

    }
    
    
}
