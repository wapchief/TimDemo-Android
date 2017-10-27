package com.tencent.qcloud.presentation.presenter;


import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupDetailInfo;
import com.tencent.imsdk.ext.group.TIMGroupManagerExt;
import com.tencent.qcloud.presentation.viewfeatures.GroupInfoView;

import java.util.List;


/**
 * 群信息逻辑
 */
public class GroupInfoPresenter implements TIMValueCallBack<List<TIMGroupDetailInfo>> {

    private GroupInfoView view;
    private boolean isInGroup;
    private List<String> groupIds;

    public GroupInfoPresenter(GroupInfoView view,List<String> groupIds,boolean isInGroup){
        this.view = view;
        this.isInGroup = isInGroup;
        this.groupIds = groupIds;
    }


    public void getGroupDetailInfo(){
        if (isInGroup) {
            TIMGroupManagerExt.getInstance().getGroupDetailInfo(groupIds, this);
        }else{
            TIMGroupManagerExt.getInstance().getGroupPublicInfo(groupIds, this);
        }
    }



    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onSuccess(List<TIMGroupDetailInfo> timGroupDetailInfos) {
        view.showGroupInfo(timGroupDetailInfos);
    }
}
