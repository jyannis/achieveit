package com.ecnu2020.achieveit.util;

import com.ecnu2020.achieveit.entity.Feature;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取excel
 * @author yan on 2020-03-07
 */
@Component
public class ReadExcel<T> {

    @Autowired
    GetXSSFCellValue getXSSFCellValue;

    public List<T> getXlsxExcelData(File file, T t) throws Exception {
        InputStream is;
        is = new FileInputStream(file);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);

        List<T> tList = new ArrayList<>();

        int id = 0;
        //取每一个工作薄
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            if(numSheet != 0)break;
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            // 获取当前工作薄的每一行
            for (int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {

                    Class c = t.getClass();
                    t = (T)c.getDeclaredConstructor().newInstance();
                    Field[] fields = c.getDeclaredFields();

                    //读取
                    for (int i = 0; i < xssfRow.getLastCellNum(); i++) {
                        fields[i].setAccessible(true);
                        try {
                            //从excel里取出，并转为fields[i]的类型，然后存到fields[i]里
                            fields[i].set(t, ConvertUtils.convert(getXSSFCellValue.getStringValueFromCell(xssfRow.getCell(i)), fields[i].getType()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    fields[0].setAccessible(true);
                    fields[0].set(t,id);
                    tList.add(t);
                    id++;
                }
            }
        }

        return tList;
    }


    public static void main(String[] args) throws Exception {
        //测试时请把@Autowired GetXSSFCellValue getXSSFCellValue去除并手动初始化
        List<Feature> xlsxExcelData = new ReadExcel<Feature>().getXlsxExcelData(new File("excel/sheetName.xlsx"), new Feature());
        xlsxExcelData.forEach(System.out::println);
    }
}
