package com.hcrcms.cms.manager.main.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hcrcms.cms.dao.main.ContentTypeDao;
import com.hcrcms.cms.entity.main.ContentType;
import com.hcrcms.cms.manager.main.ContentTypeMng;
import com.hcrcms.common.hibernate3.Updater;

@Service
@Transactional
public class ContentTypeMngImpl implements ContentTypeMng {
	@Transactional(readOnly = true)
	public List<ContentType> getList(boolean containDisabled) {
		return dao.getList(containDisabled);
	}

	@Transactional(readOnly = true)
	public ContentType getDef() {
		return dao.getDef();
	}

	@Transactional(readOnly = true)
	public ContentType findById(Integer id) {
		ContentType entity = dao.findById(id);
		return entity;
	}

	public ContentType save(ContentType bean) {
		dao.save(bean);
		return bean;
	}

	public ContentType update(ContentType bean) {
		Updater<ContentType> updater = new Updater<ContentType>(bean);
		ContentType entity = dao.updateByUpdater(updater);
		return entity;
	}

	public ContentType deleteById(Integer id) {
		ContentType bean = dao.deleteById(id);
		return bean;
	}

	public ContentType[] deleteByIds(Integer[] ids) {
		ContentType[] beans = new ContentType[ids.length];
		for (int i = 0, len = ids.length; i < len; i++) {
			beans[i] = deleteById(ids[i]);
		}
		return beans;
	}

	private ContentTypeDao dao;

	@Autowired
	public void setDao(ContentTypeDao dao) {
		this.dao = dao;
	}
}