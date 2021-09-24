package com.yqm.oa.dao;

import com.yqm.oa.entity.Employee;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("employeeDao")
public interface EmployeeDao {
    void insert(Employee employee);
    void update(Employee employee);
    void delete(String sn);
    Employee select(String sn);
    List<Employee> selectAll();
//   根据部门和职位获取员工的方法，需要输入两个参数，通过注解让映射文件能够识别并对应
    List<Employee> selectByDepartmentAndPost(@Param("dsn") String dsn,@Param("post") String post);
}
