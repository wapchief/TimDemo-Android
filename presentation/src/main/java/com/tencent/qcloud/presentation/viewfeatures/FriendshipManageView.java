package com.tencent.qcloud.presentation.viewfeatures;


import com.tencent.imsdk.ext.sns.TIMFriendStatus;

/**
 * 好友关系链管理接口
 */
public interface FriendshipManageView {

    /**
     * 添加好友结果回调
     *
     * @param status 返回状态
     */
    void onAddFriend(TIMFriendStatus status);

    /**
     * 删除好友结果回调
     *
     * @param status 返回状态
     */
    void onDelFriend(TIMFriendStatus status);


    /**
     * 修改好友分组回调
     *
     * @param status 返回状态
     * @param groupName 分组名
     */
    void onChangeGroup(TIMFriendStatus status, String groupName);
}
