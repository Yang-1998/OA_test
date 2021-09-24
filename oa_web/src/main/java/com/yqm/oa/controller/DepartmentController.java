package com.yqm.oa.controller;

import com.yqm.oa.biz.DepartmentBiz;
import com.yqm.oa.entity.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

//声明为控制器并设置访问路径
@Controller("departmentController")
@RequestMapping("/department")
public class DepartmentController {

//    从业务层获取部门信息，注入对象
    @Autowired
    private DepartmentBiz departmentBiz;

    //    传递参数为Map类型，方便与SpringMVC框架解耦
    @RequestMapping("/list")
    public String list(Map<String,Object> map){
        //向map里传递部门信息
        map.put("list",departmentBiz.getAll());
        //       返回视图
        return "department_list";
    }

    @RequestMapping("/to_add")
    public String toAdd(Map<String,Object> map){
//        要使用SpringMVC在前端使用form，则需要从控制器传递对象，沿用之前的map往其中塞值
        Department department = new Department();
        map.put("department",department);
        return "department_add";
    }

    @RequestMapping("/add")
    public String add(Department department){
//      toadd方法中创建了department的键值对，此处传值department（与department_add.jsp中的form表单保持一致）
        departmentBiz.add(department);
        return "redirect:list";
//        重定向到list路径
    }

//    需要在url中传递参数sn的值
    @RequestMapping(value = "/to_update",params = "sn")
    public String toUpdate(Map<String,Object> map,String sn){
//        要使用SpringMVC在前端使用form，则需要从控制器传递对象，沿用之前的map往其中塞值
        Department department = departmentBiz.get(sn);
        map.put("department",department);
        return "department_update";
    }

    @RequestMapping("/update")
    public String update(Department department){
        departmentBiz.edit(department);
        return "redirect:list";
//        重定向到list路径
    }

    @RequestMapping(value = "/remove",params = "sn")
    public String remove(Map<String,Object> map,String sn){
//        要使用SpringMVC在前端使用form，则需要从控制器传递对象，沿用之前的map往其中塞值
        departmentBiz.remove(sn);
        return "redirect:list";
    }
}
