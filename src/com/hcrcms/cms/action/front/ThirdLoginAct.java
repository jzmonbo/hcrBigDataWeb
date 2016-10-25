package com.hcrcms.cms.action.front;


import static com.hcrcms.cms.Constants.TPLDIR_MEMBER;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.subject.WebSubject;
import org.apache.shiro.web.subject.WebSubject.Builder;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hcrcms.cms.entity.main.CmsThirdAccount;
import com.hcrcms.cms.manager.main.CmsThirdAccountMng;
import com.hcrcms.common.security.encoder.PwdEncoder;
import com.hcrcms.common.web.RequestUtils;
import com.hcrcms.common.web.ResponseUtils;
import com.hcrcms.common.web.session.SessionProvider;
import com.hcrcms.core.entity.CmsSite;
import com.hcrcms.core.entity.CmsUserExt;
import com.hcrcms.core.entity.UnifiedUser;
import com.hcrcms.core.entity.WeixinAccessToken;
import com.hcrcms.core.entity.WeixinUserinfo;
import com.hcrcms.core.manager.CmsUserMng;
import com.hcrcms.core.manager.UnifiedUserMng;
import com.hcrcms.core.web.WebErrors;
import com.hcrcms.core.web.util.CmsUtils;
import com.hcrcms.core.web.util.Constant;
import com.hcrcms.core.web.util.FrontUtils;
import com.hcrcms.core.web.util.HttpUtil;


/**
 * 第三方登录Action
 * 腾讯qq、新浪微博登陆
 */
@Controller
public class ThirdLoginAct {
	public static final String TPL_BIND = "tpl.member.bind";
	public static final String TPL_AUTH = "tpl.member.auth";
	
	
	@RequestMapping(value = "/public_auth.jspx")
	public String auth(String openId,HttpServletRequest request,HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, TPL_AUTH);
	}
	
	@RequestMapping(value = "/public_auth_login.jspx")
	public void authLogin(String key,String source,HttpServletRequest request,HttpServletResponse response, ModelMap model) throws JSONException {
		if(StringUtils.isNotBlank(source)){
			if(source.equals(CmsThirdAccount.QQ_PLAT)){
				session.setAttribute(request,response,CmsThirdAccount.QQ_KEY, key);
			}else if(source.equals(CmsThirdAccount.QQ_WEBO_PLAT)){
				session.setAttribute(request,response,CmsThirdAccount.QQ_WEBO_KEY, key);
			}else if(source.equals(CmsThirdAccount.SINA_PLAT)){
				session.setAttribute(request,response,CmsThirdAccount.SINA_KEY, key);
			}else if (source.equals(CmsThirdAccount.WEIXIN_PLAT)){
				//微信还有一步通过code获取access_token
				String[] access_token = getWeixinAccessToken(key);
				if (access_token.length > 0){
					session.setAttribute(request,response,CmsThirdAccount.WEIXIN_KEY, key);
				}
			}
		}
		JSONObject json=new JSONObject();
		//库中存放的是加密后的key
		if(StringUtils.isNotBlank(key)){
			key=pwdEncoder.encodePassword(key);
		}
		CmsThirdAccount account=accountMng.findByKey(key);
		if(account!=null){
			json.put("succ", true);
			//已绑定直接登陆
			loginByKey(key, request, response, model);
		}else{
			json.put("succ", false);
		}
		ResponseUtils.renderJson(response, json.toString());
	}
	
	/**
	 * 获取微信access_token
	 * @param code
	 * @return
	 */
	public String[] getWeixinAccessToken(String code){
		String[] userinfo = new String[3];
		String appid = "123456";			//需要从数据库配置中获取
		String secret = "SECRET";			//需要从数据库配置中获取
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+Constant.WEIXIN_APPID+"&secret="+Constant.WEIXIN_SECRET+"code="+code+"grant_type=authorization_code";
		String result = "";
		try {
			result = HttpUtil.doHPPost(url,null);
		} catch (Exception e) {
			System.out.println("send weixin get access_token is error : ");
			e.printStackTrace();
			//如果获取异常了，再获取10次，如果还异常就返回出错了
			boolean flag = false;
			for (int i=0;i<10;i++){
				try {
					if (flag) break;
					System.out.println("no."+(i+1)+" times get access_token......");
					result = HttpUtil.doHPPost(url,null);
					flag = true;
				} catch (Exception e1) {
					System.out.println("no."+(i+1)+" times is error:");
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e2) {}
				}
			}
		}
		if (!"".equals(result)){
			JSONObject json=new JSONObject();
			try {
				String access_token = json.getString("access_token");
				String expires_in = json.getString("expires_in");
				String refresh_token = json.getString("refresh_token");
				String openid = json.getString("openid");
				String scope = json.getString("scope");
				String unionid = json.getString("unionid");
				WeixinAccessToken weixinAccessToken = new WeixinAccessToken();
				weixinAccessToken.setAccess_token(access_token);
				weixinAccessToken.setExpires_in(expires_in);
				weixinAccessToken.setRefresh_token(refresh_token);
				weixinAccessToken.setOpenid(openid);
				weixinAccessToken.setScope(scope);
				weixinAccessToken.setUnionid(unionid);
				weixinAccessToken.setAccessDate(new Date());
				//这里需要做件事,把刷新时间放到缓存中,后台有个线程判断微信过期时间后,会自动刷新时间
				userinfo[0] = access_token;
				Constant.weixinMap.put(access_token,weixinAccessToken);
				WeixinUserinfo wxUserinfo = getWeixinUserInfo(access_token,openid);
				if (wxUserinfo != null){
					userinfo[1] = wxUserinfo.getNickname();
					userinfo[2] = wxUserinfo.getHeadimgurl();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return userinfo;
	}
	
	/**
	 * 获取微信用户信息
	 */
	public WeixinUserinfo getWeixinUserInfo(String accessToken,String openId){
		WeixinUserinfo wxUserInfo = new WeixinUserinfo();
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+accessToken+"&openid="+openId;
		String result = "";
		try {
			result = HttpUtil.doHPPost(url,null);
		} catch (Exception e) {
			System.out.println("send weixin get userinfo is error : ");
			e.printStackTrace();
			//如果获取异常了，再获取10次，如果还异常就返回出错了
			boolean flag = false;
			for (int i=0;i<10;i++){
				try {
					if (flag) break;
					System.out.println("no."+(i+1)+" times get userinfo......");
					result = HttpUtil.doHPPost(url,null);
					flag = true;
				} catch (Exception e1) {
					System.out.println("no."+(i+1)+" times is error:");
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e2) {}
				}
			}
		}
		if (!"".equals(result)){
			JSONObject json=new JSONObject();
			try {
				String openid = json.getString("openid");
				String nickname = json.getString("nickname");
				String sex = json.getString("sex");
				String province = json.getString("province");
				String city = json.getString("city");
				String country = json.getString("country");
				String headimgurl = json.getString("headimgurl");
				String privilege = json.getString("privilege");
				String unionid = json.getString("unionid");
				wxUserInfo.setOpenid(openid);
				wxUserInfo.setNickname(nickname);
				wxUserInfo.setSex(sex);
				wxUserInfo.setProvince(province);
				wxUserInfo.setCity(city);
				wxUserInfo.setCountry(country);
				wxUserInfo.setHeadimgurl(headimgurl);
				wxUserInfo.setPrivilege(privilege);
				wxUserInfo.setUnionid(unionid);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return wxUserInfo;
	}
	
	@RequestMapping(value = "/public_bind.jspx",method = RequestMethod.GET)
	public String bind_get(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),
				TPLDIR_MEMBER, TPL_BIND);
	}
	
	@RequestMapping(value = "/public_bind.jspx",method = RequestMethod.POST)
	public String bind_post(String username,String password,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		boolean usernameExist=unifiedUserMng.usernameExist(username);
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors=WebErrors.create(request);
		String source="";
		if(!usernameExist){
			//用户名不存在
			errors.addErrorCode("error.usernameNotExist");
		}else{
			UnifiedUser u=unifiedUserMng.getByUsername(username);
			boolean passwordValid=unifiedUserMng.isPasswordValid(u.getId(), password);
			if(!passwordValid){
				errors.addErrorCode("error.passwordInvalid");
			}else{
				//获取用户来源
				String openId=(String) session.getAttribute(request, CmsThirdAccount.QQ_KEY);
				String uid=(String) session.getAttribute(request, CmsThirdAccount.SINA_KEY);
				String weboOpenId=(String) session.getAttribute(request, CmsThirdAccount.QQ_WEBO_KEY);
				String weixinCode = (String) session.getAttribute(request,CmsThirdAccount.WEIXIN_KEY);
				if(StringUtils.isNotBlank(openId)){
					source=CmsThirdAccount.QQ_PLAT;
				}else if(StringUtils.isNotBlank(uid)){
					source=CmsThirdAccount.SINA_PLAT;
				}else if(StringUtils.isNotBlank(weboOpenId)){
					source=CmsThirdAccount.QQ_WEBO_PLAT;
				}else if (StringUtils.isNotBlank(weixinCode)){
					source=CmsThirdAccount.WEIXIN_PLAT;
				}
				//提交登录并绑定账号
				loginByUsername(username, request, response, model);
			}
		}
		if(errors.hasErrors()){
			errors.toModel(model);
			model.addAttribute("success",false);
		}else{
			model.addAttribute("success",true);
		}
		model.addAttribute("source", source);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_MEMBER, TPL_BIND);
	}
	
	@RequestMapping(value = "/public_bind_username.jspx")
	public String bind_username_post(String username,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsSite site = CmsUtils.getSite(request);
		WebErrors errors=WebErrors.create(request);
		String source="";
		if(StringUtils.isBlank(username)){
			//用户名为空
			errors.addErrorCode("error.usernameRequired");
		}else{
			boolean usernameExist=unifiedUserMng.usernameExist(username);
			if(usernameExist){
				//用户名存在
				errors.addErrorCode("error.usernameExist");
			}else{
				//获取用户来源
				String openId=(String) session.getAttribute(request, CmsThirdAccount.QQ_KEY);
				String uid=(String) session.getAttribute(request, CmsThirdAccount.SINA_KEY);
				String weboOpenId=(String) session.getAttribute(request, CmsThirdAccount.QQ_WEBO_KEY);
				String weixinCode = (String) session.getAttribute(request,CmsThirdAccount.WEIXIN_KEY);
				//(获取到登录授权key后可以注册用户)
				if(StringUtils.isNotBlank(openId)||StringUtils.isNotBlank(uid)||StringUtils.isNotBlank(weboOpenId)){
					//初始设置密码同用户名
					cmsUserMng.registerMember(username, null, username, RequestUtils.getIpAddr(request), null, null, false, new CmsUserExt(), null);
				}
				if(StringUtils.isNotBlank(openId)){
					source=CmsThirdAccount.QQ_PLAT;
				}else if(StringUtils.isNotBlank(uid)){
					source=CmsThirdAccount.SINA_PLAT;
				}else if(StringUtils.isNotBlank(weboOpenId)){
					source=CmsThirdAccount.QQ_WEBO_PLAT;
				}else if (StringUtils.isNotBlank(weixinCode)){
					source=CmsThirdAccount.WEIXIN_PLAT;
				}
				//提交登录并绑定账号
				loginByUsername(username, request, response, model);
			}
		}
		if(errors.hasErrors()){
			errors.toModel(model);
			model.addAttribute("success",false);
		}else{
			model.addAttribute("success",true);
		}
		model.addAttribute("source", source);
		FrontUtils.frontData(request, model, site);
		return FrontUtils.getTplPath(request, site.getSolutionPath(),TPLDIR_MEMBER, TPL_BIND);
	}
	
	/**
	 * 用户名登陆,绑定用户名和第三方账户key
	 * @param username
	 * @param request
	 * @param response
	 * @param model
	 */
	private void loginByUsername(String username, HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		String openId=(String) session.getAttribute(request, CmsThirdAccount.QQ_KEY);
		String uid=(String) session.getAttribute(request, CmsThirdAccount.SINA_KEY);
		String weboOpenId=(String) session.getAttribute(request, CmsThirdAccount.QQ_WEBO_KEY);
		String weixinCode = (String) session.getAttribute(request,CmsThirdAccount.WEIXIN_KEY);
		if(StringUtils.isNotBlank(openId)){
			loginShiro(request, response, username);
			//绑定账户
			bind(username, openId,  CmsThirdAccount.QQ_PLAT);
		}
		if(StringUtils.isNotBlank(uid)){
			loginShiro(request, response, username);
			//绑定账户
			bind(username, uid,  CmsThirdAccount.SINA_PLAT);
		}
		if(StringUtils.isNotBlank(weboOpenId)){
			loginShiro(request, response, username);
			//绑定账户
			bind(username, weboOpenId,  CmsThirdAccount.QQ_WEBO_PLAT);
		}
		if(StringUtils.isNotBlank(weixinCode)){
			loginShiro(request, response, username);
			//绑定账户
			bind(username, weixinCode,  CmsThirdAccount.WEIXIN_PLAT);
		}
	}
	
	/**
	 * 已绑定用户key登录
	 * @param key
	 * @param request
	 * @param response
	 * @param model
	 */
	private void loginByKey(String key,HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		CmsThirdAccount account=accountMng.findByKey(key);
		if(StringUtils.isNotBlank(key)&&account!=null){
			String username=account.getUsername();
			loginShiro(request, response, username);
		}
	}
	
	
	private void loginShiro(HttpServletRequest request,HttpServletResponse response,String username){
		PrincipalCollection principals = new SimplePrincipalCollection(username, username);  
		Builder builder = new WebSubject.Builder( request,response);  
		builder.principals(principals);  
		builder.authenticated(true);  
		WebSubject subject = builder.buildWebSubject();  
		ThreadContext.bind(subject); 
	}
	
	private void bind(String username,String openId,String source){
		CmsThirdAccount account=accountMng.findByKey(openId);
		if(account==null){
			account=new CmsThirdAccount();
			account.setUsername(username);
			//第三方账号唯一码加密存储 防冒名登录
			openId=pwdEncoder.encodePassword(openId);
			account.setAccountKey(openId);
			account.setSource(source);
			accountMng.save(account);
		}
	}
	
	@Autowired
	private UnifiedUserMng unifiedUserMng;
	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private CmsThirdAccountMng accountMng;
	@Autowired
	private SessionProvider session;
	@Autowired
	private PwdEncoder pwdEncoder;
}
