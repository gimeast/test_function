package com.example.excel.util;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ExcelUtils {

    /**
     * @Method         : excelDownload
     * @Description    : 엑셀 다운로드
     * @Author         : gimeast
     * @Date           : 2024. 04. 07.
     * @params         : res, fileName
     * @return         :
     */
    public void excelDownload(HttpServletResponse res, String fileName) throws Exception {

        try {

            /**
             * excel sheet 생성
             */
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Sheet1"); // 엑셀 sheet 이름
            sheet.setDefaultColumnWidth(28); // 디폴트 너비 설정

            /**
             * header font style
             */
            XSSFFont headerXSSFFont = (XSSFFont) workbook.createFont();
            headerXSSFFont.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}));

            /**
             * header cell style
             */
            XSSFCellStyle headerXssfCellStyle = (XSSFCellStyle) workbook.createCellStyle();

            // 테두리 설정
            headerXssfCellStyle.setBorderLeft(BorderStyle.THIN);
            headerXssfCellStyle.setBorderRight(BorderStyle.THIN);
            headerXssfCellStyle.setBorderTop(BorderStyle.THIN);
            headerXssfCellStyle.setBorderBottom(BorderStyle.THIN);

            // 배경 설정
            headerXssfCellStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 34, (byte) 37, (byte) 41}));
            headerXssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerXssfCellStyle.setFont(headerXSSFFont);

            /**
             * body cell style
             */
            XSSFCellStyle bodyXssfCellStyle = (XSSFCellStyle) workbook.createCellStyle();

            // 테두리 설정
            bodyXssfCellStyle.setBorderLeft(BorderStyle.THIN);
            bodyXssfCellStyle.setBorderRight(BorderStyle.THIN);
            bodyXssfCellStyle.setBorderTop(BorderStyle.THIN);
            bodyXssfCellStyle.setBorderBottom(BorderStyle.THIN);

            /**
             * header data
             */
            int rowCount = 0; // 데이터가 저장될 행
            String headerNames[] = new String[]{"첫번째 헤더", "두번째 헤더", "세번째 헤더"};

            Row headerRow = null;
            Cell headerCell = null;

            headerRow = sheet.createRow(rowCount++);
            for (int i = 0; i < headerNames.length; i++) {
                headerCell = headerRow.createCell(i);
                headerCell.setCellValue(headerNames[i]); // 데이터 추가
                headerCell.setCellStyle(headerXssfCellStyle); // 스타일 추가
            }

            /**
             * body data
             */
            String bodyDataDoubleArr[][] = new String[][]{
                    {"첫번째 행 첫번째 데이터", "첫번째 행 두번째 데이터", "첫번째 행 세번째 데이터"},
                    {"두번째 행 첫번째 데이터", "두번째 행 두번째 데이터", "두번째 행 세번째 데이터"},
                    {"세번째 행 첫번째 데이터", "세번째 행 두번째 데이터", "세번째 행 세번째 데이터"},
                    {"네번째 행 첫번째 데이터", "네번째 행 두번째 데이터", "네번째 행 세번째 데이터"}
            };

            Row bodyRow = null;
            Cell bodyCell = null;

            for (String[] bodyDataArr : bodyDataDoubleArr) {

                bodyRow = sheet.createRow(rowCount++);

                for (int i = 0; i < bodyDataArr.length; i++) {

                    bodyCell = bodyRow.createCell(i);
                    bodyCell.setCellValue(bodyDataArr[i]); // 데이터 추가
                    bodyCell.setCellStyle(bodyXssfCellStyle); // 스타일 추가
                }
            }

            /**
             * download
             */
            res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            res.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            ServletOutputStream servletOutputStream = res.getOutputStream();

            workbook.write(servletOutputStream);
            workbook.close();
            servletOutputStream.flush();
            servletOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @Method         : uploadWithImage
     * @Description    : 엑셀에 image 넣기
     * @Author         : gimeast
     * @Date           : 2024. 04. 07.
     * @params         : imagePath, outputFilePath
     * @return         :
     */
    public void uploadWithImage(String imagePath, String outputFilePath) throws Exception {
        try {
            InputStream is = new FileInputStream(imagePath);
            byte[] byteArray = IOUtils.toByteArray(is);
            is.close();

            XSSFWorkbook wb = new XSSFWorkbook();
            int pictureIdx = wb.addPicture(byteArray, wb.PICTURE_TYPE_JPEG);

            CreationHelper helper = wb.getCreationHelper();
            Sheet sheet = wb.createSheet();
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();

            //anchor 인스턴스에 원하는 좌표를 입력하여 drawing의 createPicture를 통해 원하는 파일을 그릴 수 있다.
            anchor.setCol1(3);
            anchor.setRow1(2);
            Picture picture = drawing.createPicture(anchor, pictureIdx);
            picture.resize();

            try (FileOutputStream fileOut = new FileOutputStream(outputFilePath)) {
                wb.write(fileOut);
                wb.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }




}
