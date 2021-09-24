package com.yqm.oa.biz.impl;

import com.yqm.oa.biz.ClaimVoucherBiz;
import com.yqm.oa.dao.ClaimVoucherDao;
import com.yqm.oa.dao.ClaimVoucherItemDao;
import com.yqm.oa.dao.DealRecordDao;
import com.yqm.oa.dao.EmployeeDao;
import com.yqm.oa.entity.ClaimVoucher;
import com.yqm.oa.entity.ClaimVoucherItem;
import com.yqm.oa.entity.DealRecord;
import com.yqm.oa.entity.Employee;
import com.yqm.oa.global.Contant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("claimVoucherBiz")
public class ClaimVoucherBizImpl implements ClaimVoucherBiz {
    @Autowired
    @Qualifier("claimVoucherDao")
    private ClaimVoucherDao claimVoucherDao;
    @Autowired
    @Qualifier("claimVoucherItemDao")
    private ClaimVoucherItemDao claimVoucherItemDao;
    @Autowired
    @Qualifier("dealRecordDao")
    private DealRecordDao dealRecordDao;
    @Autowired
    @Qualifier("employeeDao")
    private EmployeeDao employeeDao;

    public void save(ClaimVoucher claimVoucher, List<ClaimVoucherItem> items) {
        claimVoucher.setCreateTime(new Date());
        claimVoucher.setNextDealSn(claimVoucher.getCreateSn());
        claimVoucher.setStatus(Contant.CLAIMVOUCHER_CREATED);
        claimVoucherDao.insert(claimVoucher);

        for(ClaimVoucherItem item:items){
            item.setClaimVoucherId(claimVoucher.getId());
            claimVoucherItemDao.insert(item);
        }
    }

    public ClaimVoucher get(int id) {
        return claimVoucherDao.select(id);
    }

    public List<ClaimVoucherItem> getItems(int cvid) {
        return claimVoucherItemDao.selectByClaimVoucher(cvid);
    }

    public List<DealRecord> getRecords(int cvid) {
        return dealRecordDao.selectByClaimVoucher(cvid);
    }

    public List<ClaimVoucher> getForSelf(String sn) {
        return claimVoucherDao.selectByCreateSn(sn);
    }

    public List<ClaimVoucher> getForDeal(String sn) {
        return claimVoucherDao.selectByNextDealSn(sn);
    }

    public void update(ClaimVoucher claimVoucher, List<ClaimVoucherItem> items) {
        claimVoucher.setNextDealSn(claimVoucher.getCreateSn());
        claimVoucher.setStatus(Contant.CLAIMVOUCHER_CREATED);
        claimVoucherDao.update(claimVoucher);

//        获取数据库中的条目集合
        List<ClaimVoucherItem> olds = claimVoucherItemDao.selectByClaimVoucher(claimVoucher.getId());
//        逐条比对网页传递的条目是否在数据库中存在，如果不存在则把数据库中的该条目删除
        for(ClaimVoucherItem old:olds){
            boolean isHave=false;
            for(ClaimVoucherItem item:items){
                if(item.getId()==old.getId()){
                    isHave=true;
                    break;
                }
            }
            if(!isHave){
                claimVoucherItemDao.delete(old.getId());
            }
        }

//        新传递的条目在数据库中逐条更新，如果不存在就插入数据库
        for(ClaimVoucherItem item:items){
//            为item设置报销单id，否则修改数据时新增item会发生空指针异常
            item.setClaimVoucherId(claimVoucher.getId());
            if(item.getId()>0){
                claimVoucherItemDao.update(item);
            }else{
                claimVoucherItemDao.insert(item);
            }
        }

    }

    public void submit(int id) {
        ClaimVoucher claimVoucher = claimVoucherDao.select(id);
        Employee employee = employeeDao.select(claimVoucher.getCreateSn());

//        设置报销单状态、待处理人（通过selectByDepartmentAndPost方法查找当前报销单创建人所在部门的经理）的编号
        claimVoucher.setStatus(Contant.CLAIMVOUCHER_SUBMIT);
        claimVoucher.setNextDealSn(employeeDao.selectByDepartmentAndPost(employee.getDepartmentSn(),Contant.POST_FM).get(0).getSn());
        claimVoucherDao.update(claimVoucher);

//        设置处理记录
        DealRecord dealRecord = new DealRecord();
        dealRecord.setDealWay(Contant.DEAL_SUBMIT);
        dealRecord.setDealSn(employee.getSn());
        dealRecord.setClaimVoucherId(id);
        dealRecord.setDealResult(Contant.CLAIMVOUCHER_SUBMIT);
        dealRecord.setDealTime(new Date());
        dealRecord.setComment("无");
        dealRecordDao.insert(dealRecord);
    }

    public void deal(DealRecord dealRecord) {
        ClaimVoucher claimVoucher = claimVoucherDao.select(dealRecord.getClaimVoucherId());
        Employee employee = employeeDao.select(dealRecord.getDealSn());
        dealRecord.setDealTime(new Date());

        if(dealRecord.getDealWay().equals(Contant.DEAL_PASS)){
//            如果报销单金额小于需要审核的金额下限或处理人已经是总经理的则不需要复审直接通过，进入财务打款的阶段
            if(claimVoucher.getTotalAmount()<=Contant.LIMIT_CHECK || employee.getPost().equals(Contant.POST_GM)){
                claimVoucher.setStatus(Contant.CLAIMVOUCHER_APPROVED);
                claimVoucher.setNextDealSn(employeeDao.selectByDepartmentAndPost(null,Contant.POST_CASHIER).get(0).getSn());

                dealRecord.setDealResult(Contant.CLAIMVOUCHER_APPROVED);
            }else{
//                通过的需要复审的：
                claimVoucher.setStatus(Contant.CLAIMVOUCHER_RECHECK);
                claimVoucher.setNextDealSn(employeeDao.selectByDepartmentAndPost(null,Contant.POST_GM).get(0).getSn());

                dealRecord.setDealResult(Contant.CLAIMVOUCHER_RECHECK);
            }
//            打回的：
        }else if(dealRecord.getDealWay().equals(Contant.DEAL_BACK)){
            claimVoucher.setStatus(Contant.CLAIMVOUCHER_BACK);
            claimVoucher.setNextDealSn(claimVoucher.getCreateSn());

            dealRecord.setDealResult(Contant.CLAIMVOUCHER_BACK);
        }else if(dealRecord.getDealWay().equals(Contant.DEAL_REJECT)){
            claimVoucher.setStatus(Contant.CLAIMVOUCHER_TERMINATED);
            claimVoucher.setNextDealSn(null);

            dealRecord.setDealResult(Contant.CLAIMVOUCHER_TERMINATED);
        }else if(dealRecord.getDealWay().equals(Contant.DEAL_PAID)){
//            打款：
            claimVoucher.setStatus(Contant.CLAIMVOUCHER_PAID);
            claimVoucher.setNextDealSn(null);

            dealRecord.setDealResult(Contant.CLAIMVOUCHER_PAID);
        }

        claimVoucherDao.update(claimVoucher);
        dealRecordDao.insert(dealRecord);
    }

}
