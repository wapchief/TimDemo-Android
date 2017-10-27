package com.tencent.qcloud.presentation.viewfeatures;


import com.tencent.imsdk.ext.group.TIMGroupPendencyItem;

import java.util.List;

/**
 * 群管理消息接口
 */
public interface GroupManageMessageView {

    /**
     * 获取群管理最后一条系统消息的回调
     *
     * @param message 最后一条消息
     * @param unreadCount 未读数
     */
    void onGetGroupManageLastMessage(TIMGroupPendencyItem message, long unreadCount);

    /**
     * 获取群管理系统消息的回调
     *
     * @param message 分页的消息列表
     */
    void onGetGroupManageMessage(List<TIMGroupPendencyItem> message);
}
