package com.pinyougou.search.service.impl;

import java.util.Arrays;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;

import com.pinyougou.search.service.ItemSearchService;

public class ItemDeleteListener implements MessageListener {
    /**
    *  监听：用于删除索引库中记录
    */
	
	@Autowired
	private ItemSearchService itemSearchService;
	
	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		ObjectMessage objectMessage= (ObjectMessage)message;
		
		try {
			Long[] goodsIds= (Long[]) objectMessage.getObject();
			System.out.println("ItemDeleteListener 监听接收到消息..."+goodsIds);
			itemSearchService.deleteByGoodsIds(Arrays.asList(goodsIds));
			System.out.println("成功删除索引库中的记录");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("删除索引库中的记录失败");
		}
		
	}

}
