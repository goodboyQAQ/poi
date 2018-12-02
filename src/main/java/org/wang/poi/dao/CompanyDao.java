package org.wang.poi.dao;

import org.springframework.stereotype.Component;
import org.wang.poi.bean.Company;

import java.util.ArrayList;
import java.util.List;

@Component
public class CompanyDao {
    public void addCompanyList(List<Company> list){
        System.out.println("添加了"+list.size()+"条数据");
    }

    public void updateCompany(Company company){
        System.out.println("修改:"+company);
    }

    public Company getCompanyById(String id){
        return null;
    }

    public List<Company> exportData(Company company){
        Company c1=new Company();
        c1.setId("1");
        c1.setName("张三");
        c1.setTel("111");
        Company c2=new Company();
        c2.setId("2");
        c2.setName("李四");
        c2.setTel("2222");
        List<Company> list= new ArrayList<>();
        list.add(c1);
        list.add(c2);
        return list;
    }
}
