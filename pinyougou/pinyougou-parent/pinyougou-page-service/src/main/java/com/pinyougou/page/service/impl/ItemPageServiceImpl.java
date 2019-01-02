package com.pinyougou.page.service.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;


import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;

import freemarker.template.Configuration;
import freemarker.template.Template;
import javassist.tools.framedump;

@Service
public class ItemPageServiceImpl implements ItemPageService {

	@Value("${pagedir}")
	private String pagedir;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private FreeMarkerConfig freemarkerConfig;
	
	@Autowired
	private TbGoodsMapper goodsMapp;
	
	@Autowired 
	private TbGoodsDescMapper goodsDescMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	//删除静态页面
	@Override
	public boolean deleteItemHtml(Long[] goodsIds) {
		try {
			for(Long goodId:goodsIds) {
				new File(pagedir+goodId+".html").delete();
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
	}
	
	
	
	@Override
	public boolean genItemHtml(Long goodsId) {

		// TODO Auto-generated method stub
		Configuration configuration = freemarkerConfig.getConfiguration();
		try {
			Template template = configuration.getTemplate("item.ftl");
			Map dataModel=new HashMap<>();
			//1.加载商品表数据
			
			TbGoods tbGoods = goodsMapp.selectByPrimaryKey(goodsId);
			dataModel.put("goods", tbGoods);
			
			//2.加载商品扩展表数据
			TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			dataModel.put("goodsDesc", tbGoodsDesc);
			
			//3.商品分类
			String itemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
			String itemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
			String itemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
			
			dataModel.put("itemCat1", itemCat1);
			dataModel.put("itemCat2", itemCat2);
			dataModel.put("itemCat3", itemCat3);
			
			//sku列表
		TbItemExample example=new TbItemExample();
       com.pinyougou.pojo.TbItemExample.Criteria crtit=example.createCriteria();
       crtit.andStatusEqualTo("1");//状态1有限
       crtit.andGoodsIdEqualTo(goodsId);
       example.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默      认
       List<TbItem>  itemList= itemMapper.selectByExample(example);
       
       dataModel.put("itemList", itemList);
       
      
			
			Writer out=new FileWriter(pagedir+goodsId+".html");
			template.process(dataModel, out);
			out.close();
			return true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		
		
	}

}
