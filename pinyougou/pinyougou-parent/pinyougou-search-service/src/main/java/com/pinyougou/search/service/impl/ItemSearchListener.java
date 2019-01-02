package com.pinyougou.search.service.impl;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service
public class ItemSearchListener implements MessageListener {

	
	@Autowired
	private ItemSearchService  itemSearchService;
	
	@Override
	public void onMessage(Message message) {
		System.out.println("监听接收到消息...");
		// TODO Auto-generated method stub
        TextMessage msg=(TextMessage)message;
        
        try {
			String text = msg.getText();
			List<TbItem> tbItems = JSON.parseArray(text,TbItem.class);
			
			for(TbItem item:tbItems) {
				
				System.out.println(item.getId()+" "+item.getTitle());
				Map specMap= JSON.parseObject(item.getSpec());//将 spec 字段中的 json字符串转换为 map
				item.setSpecMap(specMap);//给带注解的字段赋值
			
			}
			
			itemSearchService.importList(tbItems);
			System.out.println("成功导入到索引库");
		
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

}
