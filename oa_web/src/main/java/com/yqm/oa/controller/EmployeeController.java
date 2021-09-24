package com.yqm.oa.controller;

import com.yqm.oa.biz.DepartmentBiz;
import com.yqm.oa.biz.EmployeeBiz;
import com.yqm.oa.entity.Department;
import com.yqm.oa.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.yqm.oa.global.Contant;

import java.util.Map;

//声明为控制器并设置访问路径
@Controller("employeeController")
@RequestMapping("/employee")
public class EmployeeController {

//    从业务层获取部门信息，注入对象
    @Autowired
    private DepartmentBiz departmentBiz;

    @Autowired
    private EmployeeBiz employeeBiz;

    //    传递参数为Map类型，方便与SpringMVC框架解耦
    @RequestMapping("/list")
    public String list(Map<String,Object> map){
        map.put("list", employeeBiz.getAll());
        //       返回视图
        return "employee_list";
    }

    @RequestMapping("/to_add")
    public String toAdd(Map<String,Object> map){
//        要使用SpringMVC在前端使用form，则需要从控制器传递对象，沿用之前的map往其中塞值
        Employee employee = new Employee();
        map.put("employee",employee);
//        添加员工时需要选择部门
        map.put("dlist",departmentBiz.getAll());
        map.put("plist",Contant.getPosts());
        return "employee_add";
    }

    @RequestMapping("/add")
    public String add(Employee employee){
//      toadd方法中创建了department的键值对，此处传值department（与department_add.jsp中的form表单保持一致）
        employeeBiz.add(employee);
        return "redirect:list";
//        重定向到list路径
    }

//    需要在url中传递参数sn的值
    @RequestMapping(value = "/to_update",params = "sn")
    public String toUpdate(Map<String,Object> map,String sn){
//        要使用SpringMVC在前端使用form，则需要从控制器传递对象，沿用之前的map往其中塞值
        Employee employee = employeeBiz.get(sn);
        map.put("dlist",departmentBiz.getAll());
        map.put("plist",Contant.getPosts());
        map.put("employee",employee);
        return "employee_update";
    }

    @RequestMapping("/update")
    public String update(Employee employee){
        employeeBiz.edit(employee);
        return "redirect:list";
//        重定向到list路径
    }

    @RequestMapping(value = "/remove",params = "sn")
    public String remove(Map<String,Object> map,String sn){
//        要使用SpringMVC在前端使用form，则需要从控制器传递对象，沿用之前的map往其中塞值
        employeeBiz.remove(sn);
        return "redirect:list";
    }
}
