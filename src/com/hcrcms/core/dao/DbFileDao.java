package com.hcrcms.core.dao;

import com.hcrcms.common.hibernate3.Updater;
import com.hcrcms.core.entity.DbFile;

public interface DbFileDao {
	public DbFile findById(String id);

	public DbFile save(DbFile bean);

	public DbFile updateByUpdater(Updater<DbFile> updater);

	public DbFile deleteById(String id);
}