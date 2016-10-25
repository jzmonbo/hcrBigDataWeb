package com.hcrcms.cms.manager.main;

import com.hcrcms.cms.entity.main.CmsThirdAccount;
import com.hcrcms.common.page.Pagination;

public interface CmsThirdAccountMng {
	public Pagination getPage(String username,String source,int pageNo, int pageSize);

	public CmsThirdAccount findById(Long id);
	
	public CmsThirdAccount findByKey(String key);

	public CmsThirdAccount save(CmsThirdAccount bean);

	public CmsThirdAccount update(CmsThirdAccount bean);

	public CmsThirdAccount deleteById(Long id);
	
	public CmsThirdAccount[] deleteByIds(Long[] ids);
}