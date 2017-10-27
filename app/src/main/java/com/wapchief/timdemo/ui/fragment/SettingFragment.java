package com.wapchief.timdemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tencent.imsdk.TIMFriendshipManager;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.wapchief.timdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);
        initView();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initView() {
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

}
