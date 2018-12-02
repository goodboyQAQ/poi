package org.wang.poi.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.wang.poi.annotation.ExcelTitle;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j   //可以直接使用日志方法  log.error(...);
public class PoiUtil<T> {
    //根据文件后缀生成响应的工作簿，我这里只支持xlsx格式
    public Workbook getWorkBook(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename().toLowerCase();
        InputStream is = null;
        try {
            if (fileName.endsWith("xlsx")) {
                return new XSSFWorkbook(file.getInputStream());
            } else {
                throw new Exception("excel文件类型错误");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new Exception("文件格式错误");
        } finally {
            try {
                if (is != null) {
                    {
                        is.close();
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    /**
     * 导入
     *
     * @param workbook 工作簿
     * @param clazz    对应实体类
     * @return 解析出的对应实体类数据   的集合
     */
    public List<T> importExcel(Workbook workbook, Class<T> clazz) {
        //利用反射获取我在注解里value定义好的字段顺序
        String[] fields = clazz.getAnnotation(ExcelTitle.class).value();
        List<T> list = new ArrayList<>();  //返回的对象列表
        //获取第一个工作簿，只支持解析第一个工作簿
        Sheet sheet = workbook.getSheetAt(0);
        for (Row row : sheet) {
            //第一次循环  表头跳过
            if (row == sheet.getRow(0)) {
                continue;
            }
            //第二次往后
            try {
                T t = clazz.newInstance();
                //row.getLastCellNum()获取的不是行数，是下标，所以+1
                for (int i = 0; i < row.getLastCellNum() + 1; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        //cell的值类型需要处理
                        String cellValue = getCellStringVal(cell);
                        //获取所有字段，包括private
                        Field f = clazz.getDeclaredField(fields[i]);
                        //设置为true后才能操作private属性
                        f.setAccessible(true);
                        //给该属性设置值
                        f.set(t, cellValue);
                    }
                }
                list.add(t);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return list;
    }

    //导出
    public void exportData(String fileName, List<T> list, HttpServletResponse response, Class<T> clazz) {
        Field[] field = clazz.getDeclaredFields();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = null;
        try {
            //获取注解中定义好的中文表头顺序
            String[] title = clazz.getAnnotation(ExcelTitle.class).title();
            //获取注解中定义好的字段顺序
            String[] fields = clazz.getAnnotation(ExcelTitle.class).value();
            for (int i = 0; i <= list.size(); i++) {
                row = sheet.createRow(i);
                Cell cell = null;
                if (i == 0) { //第一次创建标题行
                    for (int j = 0; j < title.length; j++) {
                        cell = row.createCell(j);
                        //安顺序设置excel表头
                        cell.setCellValue(title[j]);
                    }
                    continue;
                }
                //第二次循环开始设置数据
                T t = list.get(i - 1);
                for (int j = 0; j < fields.length; j++) {
                    Field f = t.getClass().getDeclaredField(fields[j]);
                    f.setAccessible(true);
                    cell = row.createCell(j);
                    if (f.get(t) != null) { //此条数据的该字段有值
                        cell.setCellValue(f.get(t).toString());
                    }
                }
            }
            //输出Excel文件
            OutputStream output = response.getOutputStream();
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentType("multipart/form-data");
            workbook.write(output);
            output.close();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getCellStringVal(Cell cell) {
        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
            case NUMERIC://日期类型和数字
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue()); //日期型
                } else {
                    // 解决问题：1，科学计数法(如2.6E+10)，2，超长小数小数位不一致（如1091.19649281798读取出1091.1964928179796），3，整型变小数（如0读取出0.0）
                    return NumberToTextConverter.toText(cell.getNumericCellValue());
                }
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            case ERROR:
                return String.valueOf(cell.getErrorCellValue());
            default:
                return "";
        }
    }
}