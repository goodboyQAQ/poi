package org.wang.poi.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.wang.poi.annotation.ExcelTitle;

@Data  //生成 getting 和 setting ，equals、canEqual、hashCode、toString 方法
//重写hashcode,equals判断时(id,name,tel相同就true)
@EqualsAndHashCode(callSuper = false, exclude = {"updateTime"})
//导入导出需要用到的字段
@ExcelTitle(value={"id","name","tel"},title={"编号（不能修改）","名称","电话"})
public class Company {
    private String id;
    private String name;
    private String tel;
    private String updateTime;

}
