package com.pinyougou.sellergoods.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entry.PageResult;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper tbBrandMapper;
	
	@Override
	public List<TbBrand> findAll() {
		// TODO Auto-generated method stub
		return tbBrandMapper.selectByExample(null);
	}
	
	/**
	 * 分页查询
	 */
	@Override
	public PageResult findPage(int pageNum,int pageSize) {
		
		PageHelper.startPage(pageNum, pageSize);
		Page<TbBrand> page=(Page<TbBrand>)tbBrandMapper.selectByExample(null);
		
	
		return new PageResult(page.getTotal(), page.getResult());
		
	}
	
	/**
	 * 添加数据
	 */
	@Override
	 public void  save(TbBrand tbBrand) {
		  tbBrandMapper.insert(tbBrand);
	 }

	/**
	 * 根据id进行修改
	 * 	 */
	@Override
	public TbBrand findone(Long id) {
		
		return tbBrandMapper.selectByPrimaryKey(id);
	}
	
	@Override
	public void update(TbBrand tbBrand ) {
		tbBrandMapper.updateByPrimaryKeySelective(tbBrand);
		
	}
	/**
	 * 根据id进行删除
	 */
	public void deleteById(Long[] ids) {
		for(long id:ids) {
			tbBrandMapper.deleteByPrimaryKey(id);
		}		
	}
	/**
	 * 模糊查询
	 */
	  public  PageResult findPage01(TbBrand tbBrand ,int pageNum,int pageSize) {
		 
		  PageHelper.startPage(pageNum, pageSize);
		  TbBrandExample example=new TbBrandExample();
		  Criteria criteria =  example.createCriteria();
		  if(tbBrand!=null) {
			  if(tbBrand.getName()!=null&&tbBrand.getName().length()>0) {
				  criteria.andNameLike("%"+tbBrand.getName()+"%");
		        }
			  if(tbBrand.getFirstChar()!=null&& tbBrand.getFirstChar().length()>0) {
				  criteria.andFirstCharEqualTo(tbBrand.getFirstChar());
			  }
		  }
		  Page<TbBrand> page=(Page<TbBrand>)tbBrandMapper.selectByExample(example);
		  return new PageResult(page.getTotal(), page.getResult());
	  }
	  
	  //在  模块中添加品牌
	  public  List<Map> selectOptionList(){
	
		  List<Map> optionList = tbBrandMapper.selectOptionList();
		  
		  return optionList;
	  }
	  
}
