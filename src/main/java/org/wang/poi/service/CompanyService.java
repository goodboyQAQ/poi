package org.wang.poi.service;

import org.wang.poi.bean.Company;

import java.util.List;

public interface CompanyService {
    void importCompany(List<Company> list);
    List<Company> exportData(Company company);
}
