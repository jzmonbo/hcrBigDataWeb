package com.hcrcms.cms.manager.main;

import com.hcrcms.cms.entity.main.Channel;
import com.hcrcms.cms.entity.main.ChannelExt;

public interface ChannelExtMng {
	public ChannelExt save(ChannelExt ext, Channel channel);

	public ChannelExt update(ChannelExt ext);
}