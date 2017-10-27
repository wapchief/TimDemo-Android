package com.wapchief.timdemo.ui.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMLogListener;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
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
import com.tencent.qalsdk.QALSDKManager;
import com.tencent.qalsdk.QALUserStatusListener;
import com.wapchief.timdemo.MainActivity;
import com.wapchief.timdemo.R;
import com.wapchief.timdemo.business.InitBusiness;
import com.wapchief.timdemo.business.TlsBusiness;
import com.wapchief.timdemo.event.FriendshipEvent;
import com.wapchief.timdemo.event.MessageEvent;
import com.wapchief.timdemo.event.RefreshEvent;
import com.wapchief.timdemo.framework.sp.SharedPrefHelper;
import com.wapchief.timdemo.presentation.event.GroupEvent;
import com.wapchief.timdemo.presenter.SplashPresenter;
import com.wapchief.timdemo.service.TLSService;
import com.wapchief.timdemo.ui.views.SplashView;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.ArrayList;
import java.util.List;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSLoginHelper;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by wapchief on 2017/10/20.
 */

public class SplashActivity extends FragmentActivity implements SplashView {
    private static String TAG = "SplashActivity";
    private static String Key = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAENXG1fn7rdrK5EcqFjW6IhB2Ah/v37cwn\n" +
            "iEpVu3Ath6DBP5cgUoGLFewQDiyinyPzKrbC9Db5gF/dj5fAHP3yrw==";
    private SharedPrefHelper mPrefHelper;
    private TLSLoginHelper mLoginHelper;
    public static int SDKAPPID = 1400045738;
    public static int accType = 18410;
    public static String appVer = "1.0";
    //==========
    SplashPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clearNotification();
        setContentView(R.layout.activity_splash);
        mPrefHelper = SharedPrefHelper.getInstance();
        Log.e(TAG, mPrefHelper.getUserName() + "\n" + mPrefHelper.getToken());
        mLoginHelper = TLSLoginHelper.getInstance().init(getApplicationContext(), SDKAPPID, accType, appVer);
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() == 0) {
//                initTIM();
                init();
            } else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        0);
            }
        } else {
//            initTIM();
            init();
        }
    }




    /*初始化通讯*/
    private void initTIM() {
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        int logLevel = preferences.getInt("logLevel", TIMLogLevel.DEBUG.ordinal());
        //基本配置
        TIMSdkConfig config = new TIMSdkConfig(1400045738)
                .enableCrashReport(false)
                .enableLogPrint(true)
                .setLogListener(mLogListener)
                .setLogLevel(TIMLogLevel.values()[logLevel])
                .setLogPath(Environment.getExternalStorageDirectory() + "/tim/log/");
        //一、初始化
        TIMManager.getInstance().init(getApplicationContext(), config);
        //二、用户配置
        initTLSSdk();

//        TLSLoginHelper.getInstance().init(this, SDKAPPID, accType, appVer);
        //四、初始化TLS

        //登录
        initLogin();
        Log.e(TAG, "QALSdkVersion:" + QALSDKManager.getInstance().getSdkVersion());
        QALSDKManager.getInstance().setUserStatusListener(new QALUserStatusListener() {
            @Override
            public void onForceOffline(String s) {
                Log.e(TAG, "onForceOffline:" + s);

            }

            @Override
            public void onRegisterSucc(String s) {
                Log.e(TAG, "onRegisterSucc:" + s);

            }

            @Override
            public void onRegisterFail(String s, int i, String s1) {
                Log.e(TAG, "onRegisterFail:" + s);

            }
        });
//        Log.e(TAG, TIMManager.getInstance().getVersion());
    }

    private void initLogin() {
        mLoginHelper = TLSLoginHelper.getInstance().init(getApplicationContext(), SDKAPPID, accType, appVer);
        mLoginHelper.setTimeOut(3000);
        mLoginHelper.setLocalId(2052);
        mLoginHelper.setTestHost("", true);
        TIMManager.getInstance().login(mPrefHelper.getUserName(), mPrefHelper.getToken(), mTIMCallBack);
        Log.e(TAG, "loginUser:" + TIMManager.getInstance().getLoginUser());
    }

    /**
     * 登录前的用户绑定配置
     */
    private void initConfig() {

    }

    /*日志*/
    private TIMLogListener mLogListener = new TIMLogListener() {
        @Override
        public void log(int i, String s, String s1) {

        }
    };

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


    /*初始化tls*/
    public void initTLSSdk() {


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
        //6、设置消息监听
        TIMManager.getInstance().addMessageListener(mMessageListener);

        //        mLoginHelper = TLSLoginHelper.getInstance().init(getApplicationContext(), SDKAPPID, accType, appVer);
//        mLoginHelper.setTimeOut(3000);
//        mLoginHelper.setLocalId(2052);
//        mLoginHelper.setTestHost("",false);
        //6、登录


    }

    private TIMMessageListener mMessageListener = new TIMMessageListener() {
        @Override
        public boolean onNewMessages(List<TIMMessage> list) {
            Log.e(TAG, "listMsg:" + list.size());
            return false;
        }
    };



    //====================================测试============================================

    private void init() {
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int loglvl = pref.getInt("loglvl", TIMLogLevel.DEBUG.ordinal());
        //初始化IMSDK
        InitBusiness.start(getApplicationContext(), loglvl);
        //初始化TLS
        TlsBusiness.init(getApplicationContext());
        String id = TLSService.getInstance().getLastUserIdentifier();
        mPrefHelper.setUserName(id);
        mPrefHelper.setToken(TLSService.getInstance().getUserSig(id));
        presenter = new SplashPresenter(this);
        presenter.start();
    }


    /*跳转首页*/
    @Override
    public void navToHome() {
//登录之前要初始化群和好友关系链缓存
        TIMUserConfig userConfig = new TIMUserConfig();
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                Log.e(TAG, "receive force offline message");
            }

            @Override
            public void onUserSigExpired() {
                //票据过期，需要重新登录
                Log.e(TAG, "票据过期");

            }
        })
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                        Log.i(TAG, "onConnected");
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                        Log.i(TAG, "onDisconnected");
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                        Log.i(TAG, "onWifiNeedAuth");
                    }
                });

        //设置刷新监听
        RefreshEvent.getInstance().init(userConfig);
        userConfig = FriendshipEvent.getInstance().init(userConfig);
        userConfig = GroupEvent.getInstance().init(userConfig);
        userConfig = MessageEvent.getInstance().init(userConfig);
        TIMManager.getInstance().setUserConfig(userConfig);
//        initLogin2();
        initTIMLogin(mPrefHelper.getUserName(),mPrefHelper.getToken());

    }

    /*回登录页*/
    @Override
    public void navToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        SplashActivity.this.finish();
    }

    /*判断登录状态*/
    @Override
    public boolean isUserLogin() {
        return initLogin2();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult code:" + requestCode);
        if (100 == requestCode) {
            Log.d(TAG, "login error no " + TLSService.getInstance().getLastErrno());
            if (0 == TLSService.getInstance().getLastErrno()) {
                String id = TLSService.getInstance().getLastUserIdentifier();
                mPrefHelper.setUserName(id);
                mPrefHelper.setToken(TLSService.getInstance().getUserSig(id));
                navToHome();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Log.e(TAG, "权限未开启");
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 判断小米推送是否已经初始化
     */
    private boolean shouldMiInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 清楚所有通知栏通知
     */
    private void clearNotification(){
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        MiPushClient.clearNotification(getApplicationContext());
    }

    private TIMCallBack mTIMCallBack=new TIMCallBack() {
        @Override
        public void onError(int i, String s) {
            Log.e(TAG, "失败：" + s);
            switch (i) {
                case 6208:
                    //离线状态下被其他终端踢下线
                    Log.e(TAG, "离线状态下被其他终端踢下线");
                    navToHome();

                    break;
                case 6200:
                    Log.e(TAG, "6200");
                    navToLogin();
                    break;
                default:
                    Log.e(TAG, "onError");
                    navToLogin();
                    break;
            }
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }

        @Override
        public void onSuccess() {
            Log.e(TAG, "成功");
//        TIMOfflinePushToken pushToken=new TIMOfflinePushToken(169,)

            //初始化程序后台后消息推送
//            PushUtil.getInstance();
            //初始化消息监听

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    /*判断登录状态，是否需要更新sin*/
    private boolean initLogin2() {
        // 获取最近的一个已登录用户
        TLSUserInfo userInfo = mLoginHelper.getLastUserInfo();
        mLoginHelper.needLogin(mPrefHelper.getUserName());
        //判断是否已登录
        boolean hasLogin = userInfo != null && !mLoginHelper.needLogin(userInfo.identifier);
        //更新sign
        if (hasLogin){
            mLoginHelper.TLSRefreshUserSig(userInfo.identifier, new TLSService.RefreshUserSigListener() {
                @Override
                public void onUserSigNotExist() {
                    Log.e(TAG, "onUserSigNotExist");

                }

                @Override
                public void OnRefreshUserSigSuccess(TLSUserInfo tlsUserInfo) {
                    //更新成功
                    String usersig = mLoginHelper.getUserSig(tlsUserInfo.identifier);
                    mPrefHelper.setToken(usersig);
                    mPrefHelper.setUserName(tlsUserInfo.identifier);
                    Log.e(TAG,"usersig:" + usersig);
                }

                @Override
                public void OnRefreshUserSigFail(TLSErrInfo tlsErrInfo) {
                    Log.e(TAG, "tlsErrInfo:" + tlsErrInfo.Msg);
                }

                @Override
                public void OnRefreshUserSigTimeout(TLSErrInfo tlsErrInfo) {
                    Log.e(TAG, "tlsErrInfo2:" + tlsErrInfo.Msg);

                }
            });
        }
        return hasLogin;
    }
    private void initTIMLogin(String id,String sig) {
        TIMManager.getInstance().login(id,sig, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "登录失败:"+s);

            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "登录成功");
                MessageEvent.getInstance();
                String deviceMan = android.os.Build.MANUFACTURER;
                if (deviceMan.equals("Xiaomi") && shouldMiInit()){
                    MiPushClient.registerPush(getApplicationContext(), "2882303761517631002", "5411748055335");
                }
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        });
    }
}
