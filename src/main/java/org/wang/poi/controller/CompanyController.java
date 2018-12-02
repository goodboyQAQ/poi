package org.wang.poi.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.wang.poi.bean.Company;
import org.wang.poi.bean.Result;
import org.wang.poi.service.CompanyService;
import org.wang.poi.util.FileUtil;
import org.wang.poi.util.PoiUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

@RestController
@Slf4j
public class CompanyController {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private PoiUtil poiUtil;

    @Autowired
    private CompanyService companyService;


    //下载导入模板
    @RequestMapping(value="temp",method=RequestMethod.GET)
    public void temp(HttpServletResponse response){
        String fileName="company.xlsx"; //生成下载文件名
        try{
            //user.xlsx模板文件放在resource/template下
            InputStream is=this.getClass().getResourceAsStream("/templates/company.xlsx");
            //获取输入流后实现下载
            fileUtil.download(is,fileName,response);
        }catch(Exception e){
            log.error(e.getMessage(),e);
        }
    }

    @RequestMapping(value="upload",method=RequestMethod.POST)
    public Result uplaod(@RequestParam("file")MultipartFile file){
        //MultipartFile spring支持的处理表单的file很方便
        Result result=new Result();
        try{
            //通过文件获得工作簿
            Workbook wb=poiUtil.getWorkBook(file);
            //将数据解析为我们的实体类集合
            List<Company> list=poiUtil.importExcel(wb,Company.class); //解析导入的数据
            //importCompany会再进行处理  更新或插入操作  importCompany方法写在controller后
            companyService.importCompany(list);  //存入数据库
            result.setMsg("导入成功");
            result.setSuccess(true);
        }catch(Exception e){
            log.error(e.getMessage(),e);
        }
        return result;
    }

    @RequestMapping(value="download",method=RequestMethod.GET)
    public Result download(Company company,HttpServletResponse response){
        Result result=new Result();
        try{
            //查询数据
            List<Company> list=companyService.exportData(company);
            if(list.size()==0){
                result.setMsg("数据为空");
                return result;
            }
            String fileName="company.xlsx";
            InputStream is=this.getClass().getResourceAsStream("/templates/company.xlsx");
            poiUtil.exportData(fileName,list,response,Company.class);
            result.setSuccess(true);
            result.setMsg("导出成功");
        }catch(Exception e){
            log.error(e.getMessage(),e);
        }
        return result;

    }
}
