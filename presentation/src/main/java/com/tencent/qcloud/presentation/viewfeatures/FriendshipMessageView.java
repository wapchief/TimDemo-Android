package com.tencent.qcloud.presentation.viewfeatures;


import com.tencent.imsdk.ext.sns.TIMFriendFutureItem;

import java.util.List;

/**
 * 好友关系链管理消息接口
 */
public interface FriendshipMessageView {



    /**
     * 获取好友关系链管理最后一条系统消息的回调
     *
     * @param message 最后一条消息
     * @param unreadCount 未读数
     */
    void onGetFriendshipLastMessage(TIMFriendFutureItem message, long unreadCount);

    /**
     * 获取好友关系链管理最后一条系统消息的回调
     *
     * @param message 消息列表
     */
    void onGetFriendshipMessage(List<TIMFriendFutureItem> message);


}
