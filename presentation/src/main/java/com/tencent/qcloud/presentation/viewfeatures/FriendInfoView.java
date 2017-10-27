package com.tencent.qcloud.presentation.viewfeatures;


import com.tencent.imsdk.TIMUserProfile;

import java.util.List;

/**
 * 好友信息接口
 */
public interface FriendInfoView {


    /**
     * 显示用户信息
     *
     * @param users 资料列表
     */
    void showUserInfo(List<TIMUserProfile> users);
}
