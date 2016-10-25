package com.hcrcms.cms.dao.main;

import com.hcrcms.cms.entity.main.ChannelExt;
import com.hcrcms.common.hibernate3.Updater;

public interface ChannelExtDao {
	public ChannelExt save(ChannelExt bean);

	public ChannelExt updateByUpdater(Updater<ChannelExt> updater);
}