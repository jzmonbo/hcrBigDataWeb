package com.hcrcms.core.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.hcrcms.core.entity.WeixinAccessToken;

/**
 * 保存系统启动后常用值
 * @author jingrun.zhang
 *
 */
public class Constant {

	/**
	 * Redis连接池
	 */
	public static JedisPool jedisPool = null;
	/**
	 * 上下文Redis实例
	 */
	//public static Jedis jedis = null;
	/**
	 * Redis服务器IP
	 */
	public static String ADDR = "192.168.0.100";
    /**
     * Redis的端口号
     */
	public static int PORT = 6379;
    /**
     * 访问密码
     */
	public static String AUTH = "admin";
    /**
     * 可用连接实例的最大数目，默认值为8；
     * 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
     */
	public static int MAX_ACTIVE = 1024;
    /**
     * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
     */
	public static int MAX_IDLE = 200;
    /**
     * 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
     */
	public static int MAX_WAIT = 10000;
    /**
     * 设置超时时间
     */
	public static int TIMEOUT = 10000;
    /**
     * 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
     */
	public static boolean TEST_ON_BORROW = true;
	
	//*************************************************  下面是常用值设置  ***************************************************
	/**
	 * 微信appID
	 */
	public static String WEIXIN_APPID = "";
	
	/**
	 * 微信secret
	 */
	public static String WEIXIN_SECRET = "";
	
	/**
	 * 内存中微信登陆,定期查看是否有过期,如果有需要刷新过期时间
	 */
	public static Map<String,WeixinAccessToken> weixinMap = new HashMap<String,WeixinAccessToken>();
	
	/**
	 * 操作微信内存时同步
	 */
	public static String weixinMapString = "";
	
	/**
	 * 获取基因大数据专题活动排行榜数据接口(第二个比赛)
	 */
	public static String JIYINDASHUJU_TWO_RANKING_INTERFACE = "";
	
	/**
     * 获取Jedis实例
     * @return
     */
	public synchronized static Jedis getJedis() {
        try {
            if (Constant.jedisPool != null) {
                Jedis resource = Constant.jedisPool.getResource();
                return resource;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
    }
	
	/**
     * 返还到连接池
     * 
     * @param pool 
     * @param redis
     */
    public static void returnResource(Jedis redis) {
        if (redis != null) {
        	Constant.jedisPool.returnResource(redis);
        }
    }
    
    /**
     * 把多个空格替换为一个空格
     */
    public static String replaceManySpace(String tagStr){
    	String w= "";
    	Pattern p = Pattern.compile("\\s+");
		Matcher m = p.matcher(tagStr);
		w= m.replaceAll(" ");
		return w;
    }
    
    /**
     * 图片切割工具类
     */
    public static ImageCutUtil imageCutUtil = new ImageCutUtil();
}
