package com.ecnu2020.achieveit.util;

import com.ecnu2020.achieveit.entity.Feature;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成excel
 * @author yan on 2020-03-07
 */
@Component
@Slf4j
public class MakeExcel<T> {

    @Value("${file.excelPath}")
    private String excelPath;

    @Value("${file.excelUrl}")
    private String excelUrl;

    public String makeExcel(List<T> tList,String sheetName, String path) throws Exception {
        //检查文件夹是否存在，不存在就创建
        File dir = new File(excelPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if(tList == null || tList.size() < 1)return null;
        //第一步，创建一个workbook对应一个excel文件
        XSSFWorkbook workbook = new XSSFWorkbook();
        //第二步，在workbook中创建一个sheet对应excel中的sheet
        XSSFSheet sheet = workbook.createSheet(sheetName);

        Field[]fields = tList.get(0).getClass().getDeclaredFields();


        //2.2标题
        CellRangeAddress callRangeHeader = new CellRangeAddress(0, 0, 0, 4);//起始行,结束行,起始列,结束列
        //创建头标题行;并且设置头标题
        XSSFRow rower = sheet.createRow(0);
        XSSFCell celler = rower.createCell(0);
        //加载单元格样式
        //xxx功能列表
        XSSFCellStyle erStyle = createCellStyle(workbook, (short) 13, true, true);
        celler.setCellStyle(erStyle);
        celler.setCellValue(sheetName);

        sheet.addMergedRegion(callRangeHeader);

        //2.3列名
        XSSFRow propertyRow=sheet.createRow(1);
        propertyRow.createCell(0).setCellValue("序号");
        propertyRow.createCell(1).setCellValue("功能ID");
        propertyRow.createCell(2).setCellValue("功能");
        propertyRow.createCell(3).setCellValue("子功能");
        propertyRow.createCell(4).setCellValue("描述");
        sheet.setColumnWidth(3,25 * 256);
        sheet.setColumnWidth(4,30 * 256);


        //第三步，写入实体数据，实际应用中这些数据从数据库得到,对象封装数据，集合包对象。对象的属性值对应表的每行的值
        for (int i = 0; i < tList.size(); i++) {
            XSSFRow row1 = sheet.createRow(i+2);
            T t = tList.get(i);
            row1.createCell(0).setCellValue(i+1);
            int k=1;
            //创建单元格设值 j从1开始（去除id）项目id和逻辑删除位不展示
            for(int j = 0;j<fields.length;j++){
                fields[j].setAccessible(true);
                if(fields[j].get(t)!=null
                    &&!fields[j].getName().equals("projectId")
                    &&!fields[j].getName().equals("deleted")){
                    row1.createCell(k++).setCellValue(fields[j].get(t)+"");
                }
            }
        }
        //将文件保存到指定的位置
        try {
            FileOutputStream fos = new FileOutputStream(excelPath + "/" + sheetName + ".xlsx");
            workbook.write(fos);
            log.info("excel写入成功");
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return excelUrl + "/" + sheetName + ".xlsx";
    }

    private  XSSFCellStyle createCellStyle(XSSFWorkbook workbook, short fontsize, boolean midFlag,
                                           boolean boldFlag) {
        // TODO Auto-generated method stub
        XSSFCellStyle style = workbook.createCellStyle();

        //边框
        style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);

        //是否水平居中
        if (midFlag) {
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);//水平居中
        }

        style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);//垂直居中
        //创建字体
        XSSFFont font = workbook.createFont();
        //是否加粗字体
        if (boldFlag) {
            font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        }
        font.setFontHeightInPoints(fontsize);
        //加载字体
        style.setFont(font);
        return style;
    }



    public static void main(String[] args) throws Exception {
        List<Feature> features = new ArrayList<>();
        features.add(Feature.builder().id(1).projectId("项目一").feature("功能").subFeature("子功能").build());
        features.add(Feature.builder().id(2).projectId("项目二").feature("功能2").subFeature("子功能2").build());
        System.out.println(new MakeExcel<Feature>().makeExcel(features, "sheetName", "excel"));
    }
}
