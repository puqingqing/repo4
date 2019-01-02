package com.pinyougou.sellergoods.service;
/**
 * 地址接口
 * @author Administrator
 *
 */
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbBrand;

import entry.PageResult;



public interface BrandService {
  
    public List<TbBrand> findAll();
    
   public  PageResult  findPage(int pageNum,int pageSize);
   
   public  PageResult  findPage01(TbBrand tbBrand ,int pageNum,int pageSize);
   
   public void  save(TbBrand tbBrand);
   
   public TbBrand findone(Long id);
   
   public void update(TbBrand tbBrand );
   
   public void deleteById(Long[] ids);
   
   public List<Map> selectOptionList();
}
