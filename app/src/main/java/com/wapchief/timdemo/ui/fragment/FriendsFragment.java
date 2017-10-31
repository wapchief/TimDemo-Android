package com.wapchief.timdemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.StringUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.sns.TIMFriendshipManagerExt;
import com.wapchief.timdemo.R;
import com.wapchief.timdemo.ui.adapter.ConversionListAdapter;
import com.wapchief.timdemo.ui.entity.TIMConverstionBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by wapchief on 2017/10/26.
 */

public class FriendsFragment extends Fragment {
    @BindView(R.id.friends_rv)
    RecyclerView mFriendsRv;
    Unbinder unbinder;
    @BindView(R.id.friends_refresh)
    SmartRefreshLayout mFriendsRefresh;
    private ConversionListAdapter mAdapter;
    List<TIMConverstionBean> mBeans = new ArrayList<>();
    private static String TAG = "FriendsFragment";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_friends, null);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        initAdapter();
        initData();
        initRefresh();
    }

    private void initRefresh() {
        mFriendsRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mBeans.clear();
                initData();
                refreshlayout.finishRefresh();
            }
        });
        mFriendsRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadmore(2000);
            }
        });
    }

    private void initAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mFriendsRv.setLayoutManager(layoutManager);
        mAdapter = new ConversionListAdapter(getActivity(), R.layout.item_main_message, mBeans,1);
        mFriendsRv.setAdapter(mAdapter);
    }

    /*获取好友列表*/
    private void initData() {
        TIMFriendshipManagerExt.getInstance().getFriendList(new TIMValueCallBack<List<TIMUserProfile>>() {
            @Override
            public void onError(int i, String s) {
                Log.e(getActivity().getLocalClassName(), "获取好友列表失败：" + s);
            }

            @Override
            public void onSuccess(List<TIMUserProfile> timUserProfiles) {
                TIMConverstionBean mBean;
                for (int i = 0; i < timUserProfiles.size(); i++) {
                    mBean = new TIMConverstionBean();
                    //解析性别
                    String gender = "[未知]";
                    switch (timUserProfiles.get(i).getGender()) {
                        case Male:
                            gender = "[男]";
                            break;
                        case Female:
                            gender = "[女]";
                            break;
                        case Unknow:

                            break;
                    }
                    //先获取备注，然后昵称，最后ID
                    if (!StringUtils.isEmpty(timUserProfiles.get(i).getRemark())) {
                        mBean.userName = gender + timUserProfiles.get(i).getRemark();
                    } else if (!StringUtils.isEmpty(timUserProfiles.get(i).getNickName())) {
                        mBean.userName = gender + timUserProfiles.get(i).getNickName();
                    } else {
                        mBean.userName = gender + timUserProfiles.get(i).getIdentifier();
                    }
                    mBean.title = timUserProfiles.get(i).getNickName();
                    mBean.img = timUserProfiles.get(i).getFaceUrl();
                    //签名
                    if (StringUtils.isEmpty(timUserProfiles.get(i).getSelfSignature())) {
                        mBean.content = "这个人太懒了，还没有签名~";
                    } else {
                        mBean.content = timUserProfiles.get(i).getSelfSignature();
                    }
                    mBean.type = 1;
                    mBeans.add(mBean);
//                    mBean.content = timUserProfiles.get(i).getGender().getValue();
                }
                Log.e(TAG, "mBeans:"+mBeans);
                mAdapter.notifyDataSetChanged();

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
