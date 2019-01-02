package com.pinyougou.search.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.text.Highlighter.Highlight;

import org.aspectj.internal.lang.reflect.SignaturePatternImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;

import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.druid.filter.Filter;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;



@Service(timeout=3000)
public class ItemSearchServiceImpl implements ItemSearchService {

	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	
	@Override
	public Map<String, Object> search(Map searchMap) {
		Map<String,Object> map=new HashMap<>();
       
		String  keywords =(String)searchMap.get("keywords");
		
		searchMap.put("keywords", keywords.replace(" ", ""));
		
		map.putAll(searchList(searchMap));
      
        //2.根据关键字查询商品分类
         List categoryList = searchCategoryList(searchMap);
         map.put("categoryList",categoryList);
        
         //3.查询品牌和规格列表
         String categoryName=(String)searchMap.get("category");
         
         if(!"".equals(categoryName)){//如果有分类名称
        	 
         map.putAll(searchBrandAndSpecList(categoryName));
         
         }else{//如果没有分类名称，按照第一个查询
        	
             if(categoryList.size()>0){
            	
            	map.putAll(searchBrandAndSpecList((String)categoryList.get(0))); 
             }
             
        	}
         
         
          return map;
	}
	
	
	//导入数据
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
	}
	
	//删除数据
	public void deleteByGoodsIds(List goodsIdList) {
		System.out.println("删除商品 ID"+goodsIdList);
		Query query=new SimpleQuery();
		Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
	
	
	
	
	
	
	/**
	 * 查询品牌和规格列表
	 * @param category分类名称的查询
	 * @return
	 */
	public Map searchBrandAndSpecList(String category ) {
	
		Map map=new HashMap<>();
		
		//得到模版的id
	    Long id = (Long)redisTemplate.boundHashOps("itemCat").get(category);
	   
	    //得到商品列表
	    List brandList = (List)redisTemplate.boundHashOps("brandList").get(id);
	    map.put("brandList", brandList);
	    
	    //得到规格列表
		List specList = (List)redisTemplate.boundHashOps("specList").get(id);
		map.put("specList", specList);
		return map;
	}
	
	
	
	
	//高亮分页
	private Map<String, Object> searchList(Map searchMap){
		
		Map map=new HashMap<>();
		HighlightQuery query=new SimpleHighlightQuery();
		HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");//设置高亮域
		highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
		highlightOptions.setSimplePostfix("</em>");//高亮后缀
		query.setHighlightOptions(highlightOptions);//设置高亮选项
		
		//按照关键 字进行查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		//1.2 按分类筛选
	
		if(!"".equals(searchMap.get("category"))) {
			Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		
		//1.3 按品牌筛选
		if(!"".equals(searchMap.get("brand"))) {
			Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		
		//1.4 按品牌筛选
		if(searchMap.get("spec")!=null) {
			Map<String,String> specMap= (Map) searchMap.get("spec");
			for(String key:specMap.keySet() ){
				Criteria filterCriteria=new Criteria("item_spec_"+key).is( specMap.get(key) );
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
				
			}
			
				}
		
		
		//1.5价格的查询
				if(!"".equals(searchMap.get("price"))) {
				
					String price=(String)searchMap.get("price");
					String[] prices = price.split("-");
					
					if(!prices[0].equals("0")){
						
						Criteria criteria2=new Criteria("item_price").greaterThanEqual(prices[0]);
						FilterQuery filterQuery=new SimpleFilterQuery(criteria2);
						query.addFilterQuery(filterQuery);
					}
					
					if(!prices[1].equals("*")){
						Criteria criteria2=new Criteria("item_price").lessThanEqual(prices[1]);
						FilterQuery  filterQuery=new SimpleFilterQuery(criteria2);
						query.addFilterQuery(filterQuery);
					}
					
				}
		
		//1.6分页显示
				Integer pageNo=(Integer)searchMap.get("pageNo");//当前页数
				
				if(pageNo==null) {
					pageNo=1;
				
				}
				
				Integer pageSize=(Integer)searchMap.get("pageSize");//每页显示的条数
				if(pageSize==null) {
					pageSize=20;
				}
				System.out.println(pageNo);
				System.out.println(pageSize);
				query.setOffset(pageNo);//设置开始页
				query.setRows(pageSize);//设置每页显示的条数
				
				
				//1.7 排序
				String sortValue=(String)searchMap.get("sort");//ASC DESC 
				String sortField= (String) searchMap.get("sortField");//排序字段
				if(sortValue!=null&&!("".equals(sortValue))) {
					if("ASC".equals(sortValue)) {
						Sort sort=new Sort(Sort.Direction.ASC, "item_"+sortField);
                         query.addSort(sort);
					}else {
						Sort sort=new Sort(Sort.Direction.DESC, "item_"+sortField);
						 query.addSort(sort);
					}
				}
				
				
				
		
	    //高亮显示
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query,TbItem.class);
		
		//循环高亮的入口集合
		List<HighlightEntry<TbItem>> lists = page.getHighlighted();
		
		for(HighlightEntry<TbItem> h:lists) {
	 
			TbItem item =h.getEntity();
		     
		     if(h.getHighlights().size()>0&&h.getHighlights().get(0).getSnipplets().size()>0) {
		    	 //设置高亮结果
		    	item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));
		     }
		}
		
		map.put("rows", page.getContent());
		map.put("totalPages", page.getTotalPages());//返回总页数
		map.put("total", page.getTotalElements());//返回总记录数
		
		return map;
	}
	
	
	//商品分类
	private List<String> searchCategoryList(Map searchMap){
		
		List<String> list=new ArrayList<>();
		 Query query=new SimpleQuery();
		//按照关键字查询
		Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		//设置分组选项
		GroupOptions groupOptions=new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		
		
		
		//得到分组页
		GroupPage<TbItem> page=solrTemplate.queryForGroupPage(query, TbItem.class);
		
		//根据列得到分组结果集
		GroupResult<TbItem> groupResult=page.getGroupResult("item_category");
		
	   //得到分组结果入口页
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		
		//得到分组入口集合
	      List<GroupEntry<TbItem>> content = groupEntries.getContent();
	      
	      for(GroupEntry<TbItem> entry:content) {
	    	
	    	  list.add(entry.getGroupValue());
	      }
	     
		return list;
	}

	
	
}
