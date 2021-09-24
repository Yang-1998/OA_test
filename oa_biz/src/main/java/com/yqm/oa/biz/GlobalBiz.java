package com.yqm.oa.biz;

import com.yqm.oa.entity.Employee;

public interface GlobalBiz {
    Employee login(String sn,String password);
    void changePassword(Employee employee);
}
