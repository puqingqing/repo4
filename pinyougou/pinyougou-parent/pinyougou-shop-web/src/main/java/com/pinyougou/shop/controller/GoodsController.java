package com.pinyougou.shop.controller;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entry.PageResult;
import entry.ResultSet;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public  ResultSet add(@RequestBody Goods goods){
		
	   String sellId=SecurityContextHolder.getContext().getAuthentication().getName();
	   System.out.println(sellId);
	goods.getTbGoods().setSellerId(sellId);
		try {
			goodsService.add(goods);
			return new ResultSet(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultSet(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public ResultSet update(@RequestBody Goods goods){
		//校验是否是当前商家的 id
		Goods goods2 = goodsService.findOne(goods.getTbGoods().getId());
		String sellName = SecurityContextHolder.getContext().getAuthentication().getName();
		//如果传递过来的商家 ID 并不是当前登录的用户的 ID,则属于非法操作
		
		if(!goods2.getTbGoods().getSellerId().equals(sellName)||!goods.getTbGoods().getSellerId().equals(sellName)){
			return new ResultSet(false, "操作非法");
		}
		
		
		try {
			goodsService.update(goods);
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
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public ResultSet delete(Long [] ids){
		try {
			goodsService.delete(ids);
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
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
	    String username = SecurityContextHolder.getContext().getAuthentication().getName();
	    goods.setSellerId(username);
		
		return goodsService.findPage(goods, page, rows);		
	}
	
}
