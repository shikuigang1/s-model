package com.skg.smodel.core.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2017/6/29.
 */
public class WxUtil {
    //此处的appid与wx.config 参数appId一致   微信公众账号提供给开发者的信息，以下同理
    public static String APPID = "wx710e61f1c200c617";

    //同上
    public static String SECRET = "1445481792d5a8a73b3fe5c147879753";
    public static String ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    public static String TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

    public static Map getAccess_token(){

        String url = String.format(ACCESS_TOKEN,APPID,SECRET);
        try {
            String result = HttpGetRequest.doGet(url);
            JSONObject rqJsonObject = JSONObject.parseObject(result);
            Map map = JSONObject.toJavaObject(rqJsonObject,Map.class);
            return map;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    private static Map getTicket(String token){
        String url = String.format(TICKET,token);
        try {
            String result = HttpGetRequest.doGet(url);
            JSONObject rqJsonObject = JSONObject.parseObject(result);
            Map map = JSONObject.toJavaObject(rqJsonObject, Map.class);
            return map;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取js sdk 认证信息
     * @author
     * @date 创建时间 2016年7月28日 上午11:25:01
     * @param url
     * @return
     */
    public static Map<String, String> getSign(String url){

        //处理token失效的问题
        try {
            if(WxParams.tokenTime==null){
                WxParams.tokenTime="0";
            }
            if(WxParams.tokenExpires==null){
                WxParams.tokenExpires="7200";
            }
            long tokenTimeLong = Long.parseLong(WxParams.tokenTime);
            long tokenExpiresLong = Long.parseLong(WxParams.tokenExpires);

            //时间差
            long differ = (System.currentTimeMillis() - tokenTimeLong) /1000;
            if (WxParams.token == null ||  differ > tokenExpiresLong ) {
                //token为null，或者超时，重新获取
                Map map = getAccess_token();
                if (map.get("errcode") == null) {
                    WxParams.token = map.get("access_token").toString();
                    WxParams.tokenTime = System.currentTimeMillis()+"";
                    WxParams.tokenExpires =  map.get("expires_in")+"";
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Map map = getAccess_token();
            if (map.get("errcode") == null) {
                WxParams.token = map.get("access_token").toString();
                WxParams.tokenTime = System.currentTimeMillis()+"";
                WxParams.tokenExpires =  map.get("expires_in")+"";
            }
        }

        //处理ticket失效的问题
        try {
            if(WxParams.ticketTime==null){
                WxParams.ticketTime="0";
            }
            if(WxParams.ticketExpires==null){
                WxParams.ticketExpires="7200";
            }
            long ticketTimeLong = Long.parseLong(WxParams.ticketTime);
            long ticketExpiresLong = Long.parseLong(WxParams.ticketExpires);

            //时间差
            long differ = (System.currentTimeMillis() - ticketTimeLong) /1000;
            if (WxParams.ticket == null ||  differ > ticketExpiresLong ){
                //ticket为null，或者超时，重新获取
                Map ticket = getTicket(WxParams.token);
                if ((int)ticket.get("errcode") == 0) {
                    WxParams.ticket =ticket.get("ticket").toString();
                    WxParams.ticketTime = System.currentTimeMillis()+"";
                    WxParams.ticketExpires = ticket.get("expires_in")+"";
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Map ticket = getTicket(WxParams.token);
            if ((int)ticket.get("errcode") == 0) {
                WxParams.ticket =ticket.get("ticket").toString();
                WxParams.ticketTime = System.currentTimeMillis()+"";
                WxParams.ticketExpires = ticket.get("expires_in")+"";
            }
        }

        Map<String, String> ret = Sign.sign(WxParams.ticket, url);
        ret.put("appId",APPID);
        return ret;
    }

    /**
     * @version 1.0
     * 创建时间：2016年4月13日 下午3:53:57
     */
    public static class WxParams {
        public static String token;
        public static String tokenTime;
        public static String tokenExpires;

        public static String ticket;
        public static String ticketTime;
        public static String ticketExpires;
    }

}
