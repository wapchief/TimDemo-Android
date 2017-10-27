package com.wapchief.timdemo.business;

import android.content.Context;

import com.wapchief.timdemo.service.TLSService;
import com.wapchief.timdemo.ui.activity.SplashActivity;

/**
 * 初始化tls登录模块
 */
public class TlsBusiness {


    private TlsBusiness(){}

    public static void init(Context context){
        TLSConfiguration.setSdkAppid(SplashActivity.SDKAPPID);
        TLSConfiguration.setAccountType(SplashActivity.accType);
        TLSConfiguration.setTimeout(8000);
        TLSConfiguration.setQqAppIdAndAppKey("222222", "CXtj4p63eTEB2gSu");
        TLSConfiguration.setWxAppIdAndAppSecret("wx65f71c2ea2b122da", "1d30d40f8db6d3ad0ee6492e62ad5d57");
        TLSService.getInstance().initTlsSdk(context);
    }

    public static void logout(String id){
        TLSService.getInstance().clearUserInfo(id);

    }
}
