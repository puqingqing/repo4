package com.pinyougou.shop.controller;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;

import entry.PageResult;
import entry.ResultSet;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

	@Reference
	private ItemCatService itemCatService;
	
	private static int i=1;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbItemCat> findAll(){			
		return itemCatService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return itemCatService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/add")
	public ResultSet add(@RequestBody TbItemCat itemCat){
		try {
			itemCatService.add(itemCat);
			return new ResultSet(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultSet(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param itemCat
	 * @return
	 */
	@RequestMapping("/update")
	public ResultSet update(@RequestBody TbItemCat itemCat){
		try {
			itemCatService.update(itemCat);
			return new ResultSet(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultSet(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	
	@RequestMapping("/findOne")
	public TbItemCat findOne(Long id){
		
		
		return itemCatService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public ResultSet delete(Long [] ids){
		try {
			itemCatService.delete(ids);
			return new ResultSet(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultSet(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbItemCat itemCat, int page, int rows  ){
		return itemCatService.findPage(itemCat, page, rows);		
	}
	/**
	 * 根据父id查找下一级id
	 * @param parentId
	 * @return
	 */
	@RequestMapping("/findByParentId")
	public List<TbItemCat> findByParentId(Long parentId){
	
		List<TbItemCat> items=itemCatService.findByParentId(parentId);
		return items;
	}
}
