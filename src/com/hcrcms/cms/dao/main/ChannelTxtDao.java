package com.hcrcms.cms.dao.main;

import com.hcrcms.cms.entity.main.ChannelTxt;
import com.hcrcms.common.hibernate3.Updater;

public interface ChannelTxtDao {
	public ChannelTxt findById(Integer id);

	public ChannelTxt save(ChannelTxt bean);

	public ChannelTxt updateByUpdater(Updater<ChannelTxt> updater);
}