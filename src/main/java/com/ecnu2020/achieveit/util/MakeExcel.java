package com.ecnu2020.achieveit.util;

import com.ecnu2020.achieveit.entity.Feature;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    public String makeExcel(List<T> tList,String sheetName, String path) throws Exception {
        //检查文件夹是否存在，不存在就创建
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if(tList == null || tList.size() < 1)return null;
        //第一步，创建一个workbook对应一个excel文件
        XSSFWorkbook workbook = new XSSFWorkbook();
        //第二步，在workbook中创建一个sheet对应excel中的sheet
        XSSFSheet sheet = workbook.createSheet(sheetName);

        Field[]fields = tList.get(0).getClass().getDeclaredFields();


        //第三步，写入实体数据，实际应用中这些数据从数据库得到,对象封装数据，集合包对象。对象的属性值对应表的每行的值
        for (int i = 0; i < tList.size(); i++) {
            XSSFRow row1 = sheet.createRow(i);
            T t = tList.get(i);

            //创建单元格设值 j从1开始（去除id）
            for(int j = 0;j<fields.length;j++){
                fields[j].setAccessible(true);
                if(fields[j].get(t)!=null){
                    row1.createCell(j).setCellValue(fields[j].get(t)+"");
                }
            }
        }
        //将文件保存到指定的位置
        try {
            FileOutputStream fos = new FileOutputStream(path + "/" + sheetName + ".xlsx");
            workbook.write(fos);
            log.info("excel写入成功");
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    return path + "/" + sheetName + ".xlsx";
    }


    public static void main(String[] args) throws Exception {
        List<Feature> features = new ArrayList<>();
        features.add(Feature.builder().id(1).projectId("项目一").feature("功能").subFeature("子功能").build());
        features.add(Feature.builder().id(2).projectId("项目二").feature("功能2").subFeature("子功能2").build());
        System.out.println(new MakeExcel<Feature>().makeExcel(features, "sheetName", "excel"));
    }
}
