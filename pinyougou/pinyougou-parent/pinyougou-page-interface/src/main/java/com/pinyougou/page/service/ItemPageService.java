package com.pinyougou.page.service;
/**
 * 页面详细页接口
 * @author Administrator
 *
 */
public interface ItemPageService {
	
	/**
	 * 生成商品详细页面
	 */
  public boolean genItemHtml(Long goodsId);
  
  /**
   * 删除详细页
   * @param goodsIds
   * @return
   */
  public boolean deleteItemHtml(Long[] goodsIds);
}
