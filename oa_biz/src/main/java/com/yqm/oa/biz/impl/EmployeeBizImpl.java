package com.yqm.oa.biz.impl;

import com.yqm.oa.biz.EmployeeBiz;
import com.yqm.oa.dao.EmployeeDao;
import com.yqm.oa.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("employeeBiz")
public class EmployeeBizImpl implements EmployeeBiz {
    @Qualifier("employeeDao")
    @Autowired
    private EmployeeDao employeeDao;

    public void add(Employee employee) {
//        设置员工默认密码（业务层代码独有功能）
        employee.setPassword("000000");
        employeeDao.insert(employee);
    }

    public void edit(Employee employee) {
        employeeDao.update(employee);
    }

    public void remove(String sn) {
        employeeDao.delete(sn);
    }

    public Employee get(String sn) {
        return employeeDao.select(sn);
    }

    public List<Employee> getAll() {
        return employeeDao.selectAll();
    }
}
