package org.wang.poi.annotation;

import java.lang.annotation.*;

//指明修饰的注解，可以被例如javadoc此类的工具文档化
@Documented
// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Retention(RetentionPolicy.RUNTIME)
// 可作用在接口、类、枚举、注解
@Target(ElementType.TYPE)
public @interface ExcelTitle {
    //导入导出需要用到的字段顺序
    String[] value();
    //导入导出需要用到的字段对应的中文表头
    String[] title();
}
