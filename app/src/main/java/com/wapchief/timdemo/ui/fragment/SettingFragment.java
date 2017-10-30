package com.wapchief.timdemo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.wapchief.timdemo.R;
import com.wapchief.timdemo.framework.sp.SharedPrefHelper;
import com.wapchief.timdemo.ui.activity.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by wapchief on 2017/10/26.
 */

public class SettingFragment extends Fragment {

    @BindView(R.id.setting_name)
    TextView mSettingName;
    @BindView(R.id.idtext)
    TextView mIdtext;
    Unbinder unbinder;
    private static String TAG = "SettingFragment";
    @BindView(R.id.setting_logout)
    Button mSettingLogout;
    private SharedPrefHelper mPrefHelper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);
        initView();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initView() {
        mPrefHelper = SharedPrefHelper.getInstance();
        /*获取个人资料*/
        TIMFriendshipManager.getInstance().getSelfProfile(
                new TIMValueCallBack<TIMUserProfile>() {
                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "获取资料失败：" + s);
                    }

                    @Override
                    public void onSuccess(TIMUserProfile timUserProfile) {
                        mSettingName.setText(timUserProfile.getNickName());
                        mIdtext.setText(timUserProfile.getIdentifier());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.setting_name, R.id.setting_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_name:
                break;
            case R.id.setting_logout:
                mSettingLogout.setText("正在退出....");
                mSettingLogout.setFocusable(false);
                mSettingLogout.setEnabled(false);
                TIMManager.getInstance().logout(new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        mSettingLogout.setText("异常退出");
                        mSettingLogout.setFocusable(true);
                        mSettingLogout.setEnabled(true);
                    }

                    @Override
                    public void onSuccess() {
                        mPrefHelper.setToken("");
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        mSettingLogout.setText("退出登录");
                        mSettingLogout.setFocusable(true);
                        mSettingLogout.setEnabled(true);
                        getActivity().finish();
                    }

                });
                break;
        }
    }
}
