package com.wapchief.timdemo.framework;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMOfflinePushListener;
import com.tencent.imsdk.TIMOfflinePushNotification;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSNSChangeInfo;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.imsdk.ext.group.TIMGroupAssistantListener;
import com.tencent.imsdk.ext.group.TIMGroupCacheInfo;
import com.tencent.imsdk.ext.group.TIMUserConfigGroupExt;
import com.tencent.imsdk.ext.message.TIMUserConfigMsgExt;
import com.tencent.imsdk.ext.sns.TIMFriendshipProxyListener;
import com.tencent.imsdk.ext.sns.TIMUserConfigSnsExt;
import com.tencent.qalsdk.sdk.MsfSdkUtils;
import com.wapchief.timdemo.R;
import com.wapchief.timdemo.framework.sp.SharedPrefHelper;
import com.wapchief.timdemo.ui.activity.LoginActivity;

import org.xutils.x;

import java.util.List;


/**
 * Created by wapchief on 2017/4/13 0013 上午 11:23.
 * 描述：自定义Application
 */
public class BaseApplication extends Application {


    public static BaseApplication baseApplication;
    private Context mContext;
    private SharedPrefHelper sharedPrefHelper;
    public static int SDKAPPID = 1400045738;
    public static int accType = 18410;
    public static String appVer = "1.0";
    private static String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
        Utils.init(this);
        x.Ext.init(this);

//        initTIM();
//        this.registerActivityLifecycleCallbacks();
        if (MsfSdkUtils.isMainProcess(this)) {
            TIMManager.getInstance().setOfflinePushListener(new TIMOfflinePushListener() {
                @Override
                public void handleNotification(TIMOfflinePushNotification notification) {
                    if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
                        //消息被设置为需要提醒
                        notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher);
                    }
                }
            });
        }


    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    /*初始化通讯*/
    private void initTIM() {
        //基本配置
        TIMSdkConfig config = new TIMSdkConfig(SDKAPPID)
                .enableCrashReport(false)
                .enableLogPrint(true)
                .setLogLevel(TIMLogLevel.DEBUG)
                .setLogPath(Environment.getExternalStorageDirectory() + "/tim/log/");
        //一、初始化
        TIMManager.getInstance().init(getApplicationContext(), config);
        //二、用户配置
        initConfig();
        //三、设置消息监听
        TIMManager.getInstance().addMessageListener(new TIMMessageListener() {
            @Override
            public boolean onNewMessages(List<TIMMessage> list) {
                return false;
            }
        });
//        TLSLoginHelper.getInstance().init(this, SDKAPPID, accType, appVer);

        Log.e(TAG, TIMManager.getInstance().getVersion());
    }

    /**
     * 登录前的用户绑定配置
     */
    private void initConfig() {
        TIMUserConfig userConfig = new TIMUserConfig();
        //1、基本配置
        userConfig.setUserStatusListener(mUserStatusListener)
                .setConnectionListener(mConnListener)
                .setGroupEventListener(mGroupEventListener)
                .setRefreshListener(mRefreshListener);
        //2、消息扩展配置。
        userConfig = new TIMUserConfigMsgExt(userConfig)
                //开启消息存储
                .enableStorage(true)
                //开启消息已读回执
                .enableReadReceipt(true);
        //3、资料关系链用户配置
        userConfig = new TIMUserConfigSnsExt(userConfig)
                //开启资料关系链本地存储
                .enableFriendshipStorage(true)
                .setFriendshipProxyListener(mProxyListener);
        //4、群组管理扩展配置
        userConfig = new TIMUserConfigGroupExt(userConfig)
                //开启群组资料本地存储
                .enableGroupStorage(true)
                .setGroupAssistantListener(mGroupAssistantListener);
        //5、绑定通讯管理器
        TIMManager.getInstance().setUserConfig(userConfig);
    }


    /**
     * 用户状态变更监听
     */
    private TIMUserStatusListener mUserStatusListener = new TIMUserStatusListener() {
        @Override
        public void onForceOffline() {
            MyShowDialog("该账号已在其它设备登录！", LoginActivity.class);
        }

        @Override
        public void onUserSigExpired() {
            MyShowDialog("Token失效，需要重新登录！", LoginActivity.class);

        }
    };

    /*提示弹窗*/
    private void MyShowDialog(String msg, final Class<?> activity) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setMessage(msg);
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.setPositiveButton("重新登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), activity);
                startActivity(intent);
            }
        });

        dialog.show();

    }

    /**
     * 连接状态监听
     */
    private TIMConnListener mConnListener = new TIMConnListener() {
        @Override
        public void onConnected() {
            Log.e(TAG, "TIMConnListener");

        }

        @Override
        public void onDisconnected(int i, String s) {
            ToastUtils.showShort(s);
            Log.e(TAG, "onDisconnected：" + s);

        }

        @Override
        public void onWifiNeedAuth(String s) {
            ToastUtils.showShort(s);
            Log.e(TAG, "onWifiNeedAuth：" + s);


        }
    };

    /**
     * 群组事件变更监听
     */
    private TIMGroupEventListener mGroupEventListener = new TIMGroupEventListener() {
        @Override
        public void onGroupTipsEvent(TIMGroupTipsElem timGroupTipsElem) {

        }
    };

    /**
     * 会话刷新监听
     */
    private TIMRefreshListener mRefreshListener = new TIMRefreshListener() {
        @Override
        public void onRefresh() {
            Log.e(TAG, "onRefresh");

        }

        @Override
        public void onRefreshConversation(List<TIMConversation> list) {
            Log.e(TAG, "onRefreshConversation：" + list.size());

        }
    };

    /**
     * 关系链变更监听器
     */
    private TIMFriendshipProxyListener mProxyListener = new TIMFriendshipProxyListener() {
        @Override
        public void OnAddFriends(List<TIMUserProfile> list) {

        }

        @Override
        public void OnDelFriends(List<String> list) {

        }

        @Override
        public void OnFriendProfileUpdate(List<TIMUserProfile> list) {

        }

        @Override
        public void OnAddFriendReqs(List<TIMSNSChangeInfo> list) {

        }
    };

    /**
     * 群组资料变更事件监听
     */
    private TIMGroupAssistantListener mGroupAssistantListener = new TIMGroupAssistantListener() {
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

        }

        @Override
        public void onGroupDelete(String s) {

        }

        @Override
        public void onGroupUpdate(TIMGroupCacheInfo timGroupCacheInfo) {

        }
    };
}
