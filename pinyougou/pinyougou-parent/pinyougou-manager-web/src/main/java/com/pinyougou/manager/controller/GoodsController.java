package com.pinyougou.manager.controller;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.zookeeper.server.FinalRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
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
    
	/*@Reference(timeout=40000)
	private ItemPageService itemPageService;*/
	
	@Reference
	private GoodsService goodsService;
	
	@Autowired
	private Destination  queueSolrDestination;//用于导入solr索引库的消息目标（点对点）
	
	@Autowired
	private Destination queueSolrDeleteDestination;;//删除到sorl中
	
	@Autowired
	private Destination topicPageDestination;////用于生成商品详细页的消息目标(发布订阅)
	
	@Autowired
	private Destination topicPageDeleteDestination;//删除静态页面
                     
	@Autowired
	private JmsTemplate jmsTemplate;
	
	
	/*@Reference
	private ItemSearchService itemSearchService;*/

	/**
	 * 生成静态页面
	 * @param goodsId
	 */
	@RequestMapping("/genHtml")
	public void genHtml(Long goodsId) {
		/*itemPageService.genItemHtml(goodsId);*/
	
	}
	
	
	
	
	@RequestMapping("/updateStatus")
	public ResultSet updateStatus(Long[] ids, String status) {
		try {
			goodsService.updateStatus(ids,status);
			
			if("1".equals(status)) {
			
				List<TbItem> listItems = goodsService.findItemListByGoodsIdandStatus(ids, status);
				
				if(listItems!=null && listItems.size()>0) {
					
					final String items = JSON.toJSONString(listItems);
					//导入到solr
					/*itemSearchService.importList(listItems);*/
					jmsTemplate.send(queueSolrDestination,new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
							// TODO Auto-generated method stub
							System.out.println("sorl的消息发出");
							return session.createTextMessage(items);
						}
					} );
					
					
				}else {
					System.out.println("没有明细数据");
				}
				//****生成商品详细页
				for( final Long goodsId:ids) {
				/*itemPageService.genItemHtml(goodsId);*/
					jmsTemplate.send(topicPageDestination,new MessageCreator() {
						
						@Override
						public Message createMessage(Session session) throws JMSException {
							// TODO Auto-generated method stub
							System.out.println("生成商品详细页的消息发出");
							return session.createTextMessage(goodsId+"");
						}
					});
					
					
				}
				
			}
			
			
			return new ResultSet(true, "保存成功");
		} catch (Exception e) {
			// TODO: handle exception
			return new ResultSet(false, "保存失败");
		}
		
	}
	
	
	
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
	public ResultSet delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			//itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
		   jmsTemplate.send(queueSolrDeleteDestination,new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				// TODO Auto-generated method stub
				return session.createObjectMessage(ids);
			}
		});
			
		   
		   
		   jmsTemplate.send(topicPageDeleteDestination,new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				// TODO Auto-generated method stub
				return session.createObjectMessage(ids);
			}
		});
			
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
		return goodsService.findPage(goods, page, rows);		
	}
	
}
