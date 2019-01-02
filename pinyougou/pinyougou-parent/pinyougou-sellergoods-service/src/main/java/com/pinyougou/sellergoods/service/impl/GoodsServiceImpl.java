package com.pinyougou.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbGoodsExample;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;

import entry.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbBrandMapper brandMapper;
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbSellerMapper sellerMapper;

	
	public void updateStatus(Long []ids,String status) {
		for(Long id :ids) {
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(goods);
		}
		
	}
	
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);	
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();//非删除状态
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getTbGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getTbGoods()); //插入商品表
		goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());
		goodsDescMapper.insert(goods.getTbGoodsDesc());//插入商品扩展数据
		
		  saveItemList(goods);;//插入商品 SKU 列表数据
		
	}

/**
 * 插入 SKU 列表数据
 * @param goods
 */
public void saveItemList(Goods goods) {
	
	if("1".equals(goods.getTbGoods().getIsEnableSpec())){
		for(TbItem item :goods.getItemList()){
		//标题
		String title= goods.getTbGoods().getGoodsName();
		Map<String,Object> specMap = JSON.parseObject(item.getSpec());
		for(String key:specMap.keySet()){
		title+=" "+ specMap.get(key);
		}
		item.setTitle(title);
		
		setItemValus(goods,item);
		itemMapper.insert(item);
		}
		}else{
		TbItem item=new TbItem();
		item.setTitle(goods.getTbGoods().getGoodsName());//商品 KPU+规格描述串作为
		//SKU 名称
		item.setPrice( goods.getTbGoods().getPrice() );//价格
		item.setStatus("1");//状态
		item.setIsDefault("1");//是否默认
		item.setNum(99999);//库存数量
		item.setSpec("{}");
		setItemValus(goods,item);
		itemMapper.insert(item);}
}
	
	
	private void setItemValus(Goods goods,TbItem item) {
		item.setGoodsId(goods.getTbGoods().getId());//商品 SPU 编号
		item.setSellerId(goods.getTbGoods().getSellerId());//商家编号
		item.setCategoryid(goods.getTbGoods().getCategory3Id());//商品分类编号（3 级）
		item.setCreateTime(new Date());//创建日期
		item.setUpdateTime(new Date());//修改日期
		
		TbBrand brand = 
				brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
				item.setBrand(brand.getName());
				
	TbItemCat itemCat = 
						itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
						item.setCategory(itemCat.getName());
						
						//商家名称
   TbSeller seller = 
						sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
						item.setSeller(seller.getNickName());
						
						//图片地址（取 spu 的第一个图片）
	List<Map> imageList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(), 
						Map.class) ;
						if(imageList.size()>0){
						item.setImage ( (String)imageList.get(0).get("url"));
						}
						
	}
	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		
		goods.getTbGoods().setAuditStatus("0");//设置未申请状态:如果是经过修改的商品，需要重新设置状态
		goodsMapper.updateByPrimaryKey(goods.getTbGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
		
		TbItemExample example =new TbItemExample();
		
		com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		
		criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
		
		itemMapper.deleteByExample(example);
		//添加新的 sku 列表数据
		saveItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		  
		 Goods goods=new Goods();
		 TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		 goods.setTbGoods(tbGoods);
		 TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		 goods.setTbGoodsDesc(tbGoodsDesc);
		 
		 TbItemExample example=new TbItemExample();
		 com.pinyougou.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		 criteria.andGoodsIdEqualTo(id);
		 List<TbItem> items = itemMapper.selectByExample(example);
		 
		 goods.setItemList(items);
		 
		 return goods;
		 
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);
			
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
	
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		criteria.andIsDeleteIsNull();//非删除状态
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				//criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
							criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		
		public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status ){
			TbItemExample example=new TbItemExample();
            com.pinyougou.pojo.TbItemExample.Criteria criteria=example.createCriteria();
            criteria.andGoodsIdIn(Arrays.asList(goodsIds));
            criteria.andStatusEqualTo(status);
            
			return itemMapper.selectByExample(example);
		}
}
