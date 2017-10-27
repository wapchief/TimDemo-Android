package com.tencent.qcloud.presentation.viewfeatures;


import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.ext.sns.TIMFriendGroup;

import java.util.List;

/**
 * Created by admin on 16/3/1.
 */
public interface MyFriendGroupInfo extends MvpView {

    void showMyGroupList(List<TIMFriendGroup> timFriendGroups);

    void showGroupMember(String groupname,List<TIMUserProfile> timUserProfiles);
}
