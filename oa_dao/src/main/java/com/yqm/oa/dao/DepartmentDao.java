package com.yqm.oa.dao;

import com.yqm.oa.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository("departmentDao")
public interface DepartmentDao {
    void insert(@Param("department") Department department);
    void update(@Param("department") Department department);
    void delete(@Param("sn") String sn);
    Department select(@Param("sn") String sn);
    List<Department> selectAll();
}
