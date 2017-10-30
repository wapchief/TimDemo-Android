package com.wapchief.timdemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.wapchief.timdemo.MainActivity;
import com.wapchief.timdemo.R;
import com.wapchief.timdemo.framework.BaseActivity;
import com.wapchief.timdemo.framework.BaseApplication;
import com.wapchief.timdemo.framework.sp.SharedPrefHelper;
import com.wapchief.timdemo.service.TLSService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSLoginHelper;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSPwdRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by wapchief on 2017/10/20.
 */

public class LoginActivity extends BaseActivity{

    @BindView(R.id.login_user)
    EditText mLoginUser;
    @BindView(R.id.login_pwd)
    EditText mLoginPwd;
    @BindView(R.id.login_bt)
    Button mLoginBt;
    private static String TAG = "LoginActivity";
    public static int SDKAPPID = 1400045738;
    public static int accType = 18410;
    public static String appVer = "1.0";
    private SharedPrefHelper mPrefHelper=SharedPrefHelper.getInstance();
    //登录
    private TLSLoginHelper mLoginHelper;
    //注册
    private TLSAccountHelper mAccountHelper;
    @Override
    protected int setContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        initTLSSdk();
        /*注册初始化*/
        mAccountHelper = TLSAccountHelper.getInstance()
                .init(getApplicationContext(), SDKAPPID, accType, appVer);
//        mLoginHelper = TLSLoginHelper.getInstance().init(getApplicationContext(), SDKAPPID, accType, appVer);
        initLogin();
        initTIMLogin(mPrefHelper.getUserName(),mPrefHelper.getToken());
//        initAccount();
//        Log.e(TAG, "Version:\n" + mLoginHelper.getSDKVersion()+"\n"+mAccountHelper.getSDKVersion());
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
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });
    }

    private void initAccount() {
        mAccountHelper.TLSPwdRegAskCode("86-13598089610", mRegListener);
    }

    /*登录*/
    private void initLogin() {
        // 获取最近的一个已登录用户
        TLSUserInfo userInfo = mLoginHelper.getLastUserInfo();
        //
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
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.login_bt)
    public void onViewClicked() {
        showProgressDialog("正在登录....");
        mLoginHelper.TLSPwdLogin(mLoginUser.getText().toString(),
                mLoginPwd.getText().toString().getBytes(),
                mPwdLoginListener);
    }


    /*监听回调*/
    private TLSPwdLoginListener mPwdLoginListener=new TLSPwdLoginListener() {
        @Override
        public void OnPwdLoginSuccess(TLSUserInfo tlsUserInfo) {
            dismissProgressDialog();
            ToastUtils.showShort("登录成功");
            mPrefHelper.setToken(mLoginHelper.getUserSig(tlsUserInfo.identifier));
            mPrefHelper.setUserName(mLoginUser.getText().toString());
//            TIMManager.getInstance().login(tlsUserInfo.identifier,tlsUserInfo);
            initTIMLogin(tlsUserInfo.identifier,mLoginHelper.getUserSig(tlsUserInfo.identifier));
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        @Override
        public void OnPwdLoginReaskImgcodeSuccess(byte[] bytes) {
            Log.e(TAG,"需要验证码");
            dismissProgressDialog();
        }


        @Override
        public void OnPwdLoginNeedImgcode(byte[] bytes, TLSErrInfo tlsErrInfo) {
            dismissProgressDialog();
            Log.e(TAG,"需要验证码");
        }

        @Override
        public void OnPwdLoginFail(TLSErrInfo tlsErrInfo) {
            dismissProgressDialog();
            ToastUtils.showShort(tlsErrInfo.Msg);
        }

        @Override
        public void OnPwdLoginTimeout(TLSErrInfo tlsErrInfo) {
            dismissProgressDialog();
            Log.e(TAG, tlsErrInfo.Msg+"\n"+tlsErrInfo.ErrCode);
            ToastUtils.showShort(tlsErrInfo.Msg);
        }
    };

    /*注册*/
    private TLSPwdRegListener mRegListener=new TLSPwdRegListener() {
        @Override
        public void OnPwdRegAskCodeSuccess(int i, int i1) {
            Log.e(TAG, "OnPwdRegAskCodeSuccess:"+i);
        }

        @Override
        public void OnPwdRegReaskCodeSuccess(int i, int i1) {
            Log.e(TAG, "OnPwdRegReaskCodeSuccess:"+i);

        }

        @Override
        public void OnPwdRegVerifyCodeSuccess() {
            Log.e(TAG, "OnPwdRegVerifyCodeSuccess");

        }

        @Override
        public void OnPwdRegCommitSuccess(TLSUserInfo tlsUserInfo) {
            Log.e(TAG, "OnPwdRegCommitSuccess");

        }

        @Override
        public void OnPwdRegFail(TLSErrInfo tlsErrInfo) {
            Log.e(TAG, "OnPwdRegFail:"+tlsErrInfo.ErrCode);

        }

        @Override
        public void OnPwdRegTimeout(TLSErrInfo tlsErrInfo) {
            Log.e(TAG, "OnPwdRegTimeout:"+tlsErrInfo.ErrCode);

        }
    };


    /**
     * 初始化TLS
     * 注意为避免登录超时，需要将useSSO设置为true
     */
    public void initTLSSdk() {
        mLoginHelper = TLSLoginHelper.getInstance().init(BaseApplication.baseApplication, SDKAPPID, accType, appVer);
        mLoginHelper.setTimeOut(3000);
        mLoginHelper.setLocalId(2052);
        mLoginHelper.setTestHost("",true);
    }


}
