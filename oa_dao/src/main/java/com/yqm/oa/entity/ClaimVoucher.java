package com.yqm.oa.entity;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ClaimVoucher {
    private Integer id;

    private String cause;

//    创建人的id，再在接下来声明一个Employee类的属性作为关联的创建人
    private String createSn;
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
    private Date createTime;
    //    下一个处理人的id
    private String nextDealSn;

    private Double totalAmount;

    private String status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getCreateSn() {
        return createSn;
    }

    public void setCreateSn(String createSn) {
        this.createSn = createSn;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getNextDealSn() {
        return nextDealSn;
    }

    public void setNextDealSn(String nextDealSn) {
        this.nextDealSn = nextDealSn;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    与报销单相关联的创建者和处理者员工
    private Employee creater;
    private Employee dealer;

    public Employee getCreater() {
        return creater;
    }

    public void setCreater(Employee creater) {
        this.creater = creater;
    }

    public Employee getDealer() {
        return dealer;
    }

    public void setDealer(Employee dealer) {
        this.dealer = dealer;
    }
}