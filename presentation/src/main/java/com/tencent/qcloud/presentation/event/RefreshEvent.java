package com.tencent.qcloud.presentation.event;


import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMUserConfig;

import java.util.List;
import java.util.Observable;

/**
 * IMSDK提供的刷新和被动更新的通知,登录前注册
 */
public class RefreshEvent extends Observable implements TIMRefreshListener {


    private volatile static RefreshEvent instance;

    private RefreshEvent(){

    }

    public static RefreshEvent getInstance(){
        if (instance == null) {
            synchronized (RefreshEvent.class) {
                if (instance == null) {
                    instance = new RefreshEvent();
                }
            }
        }
        return instance;
    }

    public void init(TIMUserConfig config) {
        config.setRefreshListener(this);
    }

    /**
     * 数据刷新通知，如未读技术、会话列表等
     */
    @Override
    public void onRefresh() {
        setChanged();
        notifyObservers();
    }


    /**
     * 部分会话刷新，多终端数据同步
     */
    @Override
    public void onRefreshConversation(List<TIMConversation> list) {
        setChanged();
        notifyObservers();

    }
}
