package com.hcrcms.cms.action.front;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hcrcms.cms.action.admin.assist.SensitivewordFilter;
import com.hcrcms.cms.entity.assist.CmsComment;
import com.hcrcms.cms.entity.assist.CmsCommentExt;
import com.hcrcms.cms.entity.assist.CmsIpAddress;
import com.hcrcms.cms.entity.assist.CmsReviewHead;
import com.hcrcms.cms.entity.assist.CmsSensitivity;
import com.hcrcms.cms.manager.assist.CmsCommentExtMng;
import com.hcrcms.cms.manager.assist.CmsCommentMng;
import com.hcrcms.cms.manager.assist.CmsIpAddressMng;
import com.hcrcms.cms.manager.assist.CmsSensitivityMng;
import com.hcrcms.common.web.ResponseUtils;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.manager.CmsSiteMng;
import com.hcrcms.core.web.util.ChannelCacheUtils;
import com.hcrcms.core.web.util.RandomUtils;
import com.hcrcms.core.web.util.SortComment;

@Controller
public class CommentExtAct {

	private final String COMMENT_COOKIE_KEY = "dy_comment_info";	//保留客户COOKIE信息,里面保存"名称"和"图像"
	
	@RequestMapping(value = "/commentReply.jspx", method = RequestMethod.POST)
	public void update(String commentText,String reply,String commentId,int local,int rLocal, CmsComment bean, CmsCommentExt ext,
			HttpServletRequest request,HttpServletResponse response, ModelMap model)  throws JSONException {
		JSONObject json = new JSONObject();
		//若回复内容不为空而且回复更新，则设置回复时间，已最新回复时间为准
		if(StringUtils.isNotBlank(ext.getReply())){
			//这里判断回复是否包含敏感词汇，如果有返回结果
			if (isContainSensitivity(reply) || reply.indexOf("http:") > -1 || reply.indexOf("https:") > -1){
				json.put("success", true);
				json.put("status", 1);
			}else{
				//bean.setReplayTime(new Date());
				ext.setReplayTime(new Date());
				ext.setReply(reply);
				ext.setSort(local);
				ext.setRsort(rLocal);
				ext.setText(commentText);
				CmsComment cmsComment = cmsCommentMng.findById(Integer.parseInt(commentId));
				ext.setComment(cmsComment);
				
				int extId = 0;
				if (cmsComment.getCommentExts().size() == 1){
					extId = cmsComment.getCommentExts().iterator().next().getId();
				}
				
				
				//如果是未登录用户则给安置一个匿名用户头像
				String anonymousName = "";
				String anonymousImg = "";
				/*double result = RandomUtils.getProvinceRandom();
				List<CmsIpAddress> ipAddresss = cmsIpAddressMng.getList(result);
				if (ipAddresss != null && ipAddresss.size() > 0){
					anonymousName = ipAddresss.get(0).getCity()+"的网友";
				}else{
					anonymousName = "匿名的网友";
				}
				anonymousImg = "/hcrBigDataWeb/r/cms/www/default/headPic/pic"+RandomUtils.getHeadRandom()+".jpg";*/
				
				//先从cookie中获取,如果有直接保存数据库.如果没有,需要随机生成名称和头像,再保存到数据库,写用户COOKIE
				String cVal = getClientCookie(request,response);
				CmsSite cmsSite = cmsSiteMng.getList().get(0);
				if (cVal == null || cVal.length() == 0){
					int mingziRandom = RandomUtils.getMingziRandom();
					int headImgRandom = RandomUtils.getHeadRandom();
					CmsReviewHead reviewHead = cmsIpAddressMng.getCmsReviewHead(mingziRandom);
					if (reviewHead != null){
						anonymousName = "网友" + reviewHead.getName();
						//anonymousImg =  cmsSite.getUrlBuffer(true,true,true) + "/r/cms/www/default/headPic/pic"+headImgRandom+".jpg";
						anonymousImg = "/r/cms/www/default/headPic/pic"+headImgRandom+".jpg";
						cVal = mingziRandom + "," + headImgRandom;								//放COOKIE规则是:名称ID加头像ID形式,名称需要从数据库里获取,头像需要组装路径
						writeClientCookie(request,response,cVal);
					}
				}else{
					String[] args = cVal.split(",");
					CmsReviewHead reviewHead = cmsIpAddressMng.getCmsReviewHead(Integer.parseInt(args[0]));
					if (reviewHead != null){
						anonymousName = "网友" + reviewHead.getName();
					}
					//anonymousImg = cmsSite.getUrlBuffer(true,true,true) + "/r/cms/www/default/headPic/pic"+args[1]+".jpg";
					anonymousImg = "/r/cms/www/default/headPic/pic"+args[1]+".jpg";
				}
				
				ext.setAnonymousImg(anonymousImg);
				ext.setAnonymousName(anonymousName);
				
				//把用户COOKIE里的名字和头像放到页面请求中
				model.addAttribute("commentName",anonymousImg);
				model.addAttribute("commentLogo",anonymousName);
				
				//cmsCommentExtMng.updateReplay(bean,ext,reply,commentId);
				//1.查询该评论所有回复信息 2.修改排序位置 3.插入新评论信息 4.更新其它回复位置
				List<CmsCommentExt> replyExts = cmsCommentExtMng.getReplyByCommentId(commentId);
				if (replyExts != null && replyExts.size() > 0){
					if (replyExts.size() == 1){
						if (replyExts.get(0).getReply() == null){
							ext.setSort(1);
							ext.setRsort(1);
							ext.setId(extId);
							cmsCommentExtMng.update(ext);
						}else{
							boolean uFlag = false;
							for (CmsCommentExt replyExt : replyExts){
								if (replyExt.getSort() >= local){
									uFlag = true;
									replyExt.setSort(replyExt.getSort()+1);
								}
								replyExt.setRsort(rLocal);
							}
							cmsCommentExtMng.save(ext);
							if (uFlag){
								cmsCommentExtMng.update(replyExts);
							}
						}
					}else{
						//插入新层思路：1.先把外层rSort回复列表，正序排序
						//      2.找到小于传参rSort回复列表
						//      3.插入刚找到记录和当前新记录
						//如果不插入新层：则需要找到当前外序最大内序，判断传参sort大于数据库里最大内序，则继续添加新回复
						/*for (CmsCommentExt replyExt : replyExts){
							if (replyExt.getSort() >= local){
								replyExt.setSort(replyExt.getSort()+1);
							}
							replyExt.setRsort(rLocal);
						}
						cmsCommentExtMng.save(ext);
						cmsCommentExtMng.update(replyExts);*/
						
						SortComment sortComment = new SortComment();
						int maxRsort = -1;
						List<CmsCommentExt> outExts = new ArrayList<CmsCommentExt>();			//外序列表
						for (CmsCommentExt replyExt : replyExts){
							if (rLocal <= 1){
								if (rLocal == replyExt.getRsort()){
									outExts.add(replyExt);
								}
							}else{
								if ((rLocal-1) == replyExt.getRsort()){
									outExts.add(replyExt);
								}
							}
						}
						Collections.sort(outExts,sortComment);									//按内序正序排序
						maxRsort = outExts.get(outExts.size()-1).getSort();
						if (local > maxRsort){													//如果大于最大内序，则继续添加新加复
							cmsCommentExtMng.save(ext);
						}else{																	//如果是中间插入回复，则需要拼接插入回复群
							List<CmsCommentExt> innerExts = new ArrayList<CmsCommentExt>();		//内序列表
							for (CmsCommentExt oExt : outExts){
								if (oExt.getSort() < local){
									CmsCommentExt oldExt = new CmsCommentExt();
									oldExt.setAnonymousImg(oExt.getAnonymousImg());
									oldExt.setAnonymousName(oExt.getAnonymousName());
									oldExt.setComment(oExt.getComment());
									oldExt.setIp(oExt.getIp());
									oldExt.setReplayTime(oExt.getReplayTime());
									oldExt.setReply(oExt.getReply());
									oldExt.setRsort(rLocal);
									oldExt.setSort(oExt.getSort());
									oldExt.setText(oExt.getText());
									innerExts.add(oldExt);
								}
							}
							innerExts.add(ext);
							cmsCommentExtMng.saveMany(innerExts);
						}
					}
				}else{
					ext.setSort(1);
					ext.setRsort(1);
					cmsCommentExtMng.save(ext);
				}
				
				/*//重新加载评论内容
				List<CmsComment> list = cmsCommentMng.getListForTag(null, cmsComment.getId(),
						null, true, false, false, 100);
				//给回复做排序
				if (list != null && list.size() > 0){
					SortComment sortComment = new SortComment();
					for (CmsComment comment : list){
						List<CmsCommentExt> exts = new ArrayList<CmsCommentExt>(comment.getCommentExts());
						Collections.sort(exts,sortComment);
						comment.setCommentLists(exts);
						comment.setCommentContent(exts.get(0).getText());
						
						if (comment.getCommentUser() == null) {
							CmsUser cmsUser = new CmsUser();
							//cmsUser.setId(999999);
							String amousImg = "/hcrBigDataWeb/r/cms/www/default/headPic/pic"+RandomUtils.getHeadRandom()+".jpg";
							String anonymousName2 = "";
							double result2 = RandomUtils.getProvinceRandom();
							List<CmsIpAddress> ipAddresss2 = cmsIpAddressMng.getList(result2);
							if (ipAddresss2 != null && ipAddresss2.size() > 0){
								anonymousName2 = ipAddresss2.get(0).getCity()+"的网友";
							}else{
								anonymousName2 = "匿名的网友";
							}
							cmsUser.setUsername(anonymousName2);
							CmsUserExt cmsUserExt = new CmsUserExt();
							cmsUserExt.setUserImg(amousImg);
							Set set = new HashSet();
							set.add(cmsUserExt);
							cmsUser.setUserExtSet(set);
							comment.setCommentUser(cmsUser);
						}
					}
				}
				
				json.put("list", list);*/
				
				json.put("success", true);
				json.put("status", 0);
			}
		}
		
		ResponseUtils.renderJson(response, json.toString());
	}
	
	@RequestMapping(value = "/commentDeleteReply.jspx", method = RequestMethod.POST)
	public void delete(String commentId,int local, CmsComment bean, CmsCommentExt ext,
			HttpServletRequest request,HttpServletResponse response, ModelMap model)  throws JSONException {
		JSONObject json = new JSONObject();
		try {
			cmsCommentExtMng.deleteComment(Integer.parseInt(commentId), local);
			json.put("success", true);
			json.put("status", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	public boolean isContainSensitivity(String reply){
		boolean result = false;
		try {
			if (ChannelCacheUtils.sensitivityWord.isEmpty()){
				List<CmsSensitivity> lists = sensitivityManager.getList(false);
				if (lists != null && lists.size() > 0){
					for (CmsSensitivity sensitivity : lists){
						ChannelCacheUtils.sensitivityWord.add(sensitivity.getSearch());
					}
				}
			}
			SensitivewordFilter filter = new SensitivewordFilter();
			Set<String> set = filter.getSensitiveWord(reply, 1);
			if (set != null && set.size() > 0){
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取客户cookie值
	 * @return
	 */
	public String getClientCookie(HttpServletRequest request, HttpServletResponse response){
		String result = "";
		try {
			Cookie[] cookies = request.getCookies();
			for (Cookie c : cookies) {
				if (COMMENT_COOKIE_KEY.equals(c.getName())){
					result = c.getValue();
					break;
				}
				//System.out.println(c.getName() + "--->" + c.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 写入客户端cookie
	 * @return
	 */
	public boolean writeClientCookie(HttpServletRequest request, HttpServletResponse response,String cVal){
		try {
			Cookie c2 = new Cookie(COMMENT_COOKIE_KEY,cVal);
			//设置生命周期为1个月，秒为单位
			c2.setMaxAge(2592000);
			response.addCookie(c2);
			response.getWriter().print("ok");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Autowired
	private CmsCommentExtMng cmsCommentExtMng;
	@Autowired
	private CmsCommentMng cmsCommentMng;
	@Autowired
	private CmsIpAddressMng cmsIpAddressMng;
	@Autowired
	private CmsSensitivityMng sensitivityManager;
	@Autowired
	private CmsSiteMng cmsSiteMng;
}
