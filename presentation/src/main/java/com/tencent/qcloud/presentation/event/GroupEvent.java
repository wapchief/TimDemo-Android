package com.tencent.qcloud.presentation.event;




import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupSettings;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.ext.group.TIMGroupAssistantListener;
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo;
import com.tencent.imsdk.ext.group.TIMUserConfigGroupExt;

import java.util.List;
import java.util.Observable;

/**
 * 群相关数据缓存，底层IMSDK会维护本地存储
 */
public class GroupEvent extends Observable implements TIMGroupAssistantListener {

    private final String TAG = "GroupInfo";


    private GroupEvent(){
    }

    private static GroupEvent instance = new GroupEvent();

    public static GroupEvent getInstance(){
        return instance;
    }

    public TIMUserConfig init(TIMUserConfig config){
        TIMGroupSettings settings = new TIMGroupSettings();
        config.setGroupSettings(settings);

        //开启IMSDK本地存储
        return new TIMUserConfigGroupExt(config)
                        .enableGroupStorage(true)
                        .setGroupAssistantListener(this);
    }


    @Override
    public void onMemberJoin(String s, List<TIMGroupMemberInfo> list) {

    }



    @Override
    public void onMemberQuit(String s, List<String> list) {

    }

    @Override
    public void onMemberUpdate(String s, List<TIMGroupMemberInfo> list) {

    }



    @Override
    public void onGroupAdd(TIMGroupCacheInfo timGroupCacheInfo) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.ADD, timGroupCacheInfo));
    }



    @Override
    public void onGroupDelete(String s) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.DEL, s));
    }

    @Override
    public void onGroupUpdate(TIMGroupCacheInfo timGroupCacheInfo) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.UPDATE, timGroupCacheInfo));
    }




    /**
     * 通知上层用的数据
     */
    public class NotifyCmd{
        public final NotifyType type;
        public final Object data;

        NotifyCmd(NotifyType type, Object data){
            this.type = type;
            this.data = data;
        }

    }

    public enum NotifyType{
        REFRESH,//刷新
        ADD,//添加群
        DEL,//删除群
        UPDATE,//更新群信息
    }


}
