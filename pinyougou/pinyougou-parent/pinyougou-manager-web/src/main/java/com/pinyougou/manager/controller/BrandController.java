package com.pinyougou.manager.controller;



import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entry.PageResult;
import entry.ResultSet;

@RestController
@RequestMapping("/brand")
public class BrandController {
	
	@Reference
	private BrandService  brandService;
	private PageResult findPage;
	/**
	 * 查询所有
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}
	
	/**
	 * 分页查询
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int size) {
		 
		return brandService.findPage(page, size);
	}

	/**
	 * 保存数据
	 * @param brand
	 * @return
	 */
	@RequestMapping("/save")
	public ResultSet addDate(@RequestBody TbBrand brand) {
		try {
			brandService.save(brand);
			return new ResultSet(true,"添加成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
			return new ResultSet(false,"添加失败");
		}
	}
	
	/**
	 * 根据id修改数据
	 */
	
	@RequestMapping("/findOne")
	public TbBrand findOne(long id) {
		return brandService.findone(id);
	}
	
	@RequestMapping ("/update")
	public ResultSet updateById(@RequestBody TbBrand tbBrand) {
		try {
			brandService.update(tbBrand);
			return new ResultSet(true,"添加成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		return new ResultSet(false,"添加失败");
		}
	}
	
	@RequestMapping("/delete")
	public ResultSet deleteById(Long [] ids ) {
		
		try {
			brandService.deleteById(ids);
			return new ResultSet(true, "删除成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new ResultSet(false, "删除失败");
		}
	}
	
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbBrand tbBrand,int page,int size) {
		PageResult pageResult=	brandService.findPage01(tbBrand, page, size);
		return pageResult;
	}
	
	@RequestMapping("/selectOptionList")
	 public  List<Map> selectOptionList(){
		
		 return brandService.selectOptionList();
		 
	 }
}
