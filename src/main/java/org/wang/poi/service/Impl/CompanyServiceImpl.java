package org.wang.poi.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wang.poi.bean.Company;
import org.wang.poi.dao.CompanyDao;
import org.wang.poi.service.CompanyService;
import org.wang.poi.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CompanyServiceImpl  implements CompanyService {
    @Autowired
    private CompanyDao companyDao;

    @Override
    public void importCompany(List<Company> list) {
        if(list.size()==0){
            return;
        }
        List<Company> insertList=new ArrayList<>();
        List<Company> updateList=new ArrayList<>();
        for(Company company:list){
            if(StringUtils.isNotEmpty(company.getId())){
                //通过id去数据库查找是否存在该条数据
                Company c=companyDao.getCompanyById(company.getId());
                //使用lombok的@EqualsAndHashCode重写了hashcode
                if(c!=null && !company.equals(c)){ //id查找的数据存在，且有更改
                    updateList.add(company);
                }
            }else{
                insertList.add(company);
            }
        }
        if(insertList.size()!=0) {
            companyDao.addCompanyList(insertList);
        }
        for(Company company:updateList){
            companyDao.updateCompany(company);
        }

    }

    @Override
    public List<Company> exportData(Company company) {
        return companyDao.exportData(company);
    }
}
