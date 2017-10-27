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

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.wapchief.timdemo.R;
import com.wapchief.timdemo.ui.adapter.ConversionListAdapter;
import com.wapchief.timdemo.ui.entity.TIMConverstionBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by wapchief on 2017/10/19.
 */

public class HomeFragment extends Fragment {

    @BindView(R.id.conver_rv)
    RecyclerView mConverRv;
    Unbinder unbinder;
    ConversionListAdapter mAdapter;
    List<TIMConverstionBean> mConverstionBean = new ArrayList<>();
    @BindView(R.id.home_refresh)
    SmartRefreshLayout mHomeRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_home, null);

        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
//        mMessageExt = new TIMMessageExt(new TIMMessage());

        initAdapter();
        initRefresh();
    }

    private void initAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mConverRv.setLayoutManager(layoutManager);
        mAdapter = new ConversionListAdapter(getActivity(), R.layout.item_main_message, mConverstionBean);
        mConverRv.setAdapter(mAdapter);
        initData();
    }

    private void initRefresh() {
        mHomeRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mConverstionBean.clear();
                initData();
                refreshlayout.finishRefresh();
            }
        });
        mHomeRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
//                initData();
                refreshlayout.finishLoadmore(2000);
            }
        });
    }


    private void initData() {
        mConverstionBean.clear();
        //获取会话列表
        List<TIMConversation> list = TIMManagerExt.getInstance().getConversationList();
        Log.e("HomeFragment", list.size() + "");
        TIMConverstionBean mBean;
        for (int i = 0; i < list.size(); i++) {
            mBean = new TIMConverstionBean();
            mBean.userName = list.get(i).getPeer();
            switch (list.get(i).getType()) {
                case C2C:
                    mBean.content = "[单聊]";

                    break;
                case Group:
                    mBean.content = "[群聊]";


                    break;
                default:
                    mBean.content = "[未知类型]";
                    break;
            }
            mBean.conType = list.get(i).getType();
            mBean.title = "首页好友" + i;
            mBean.type = 0;
//            mBean.img = timUserProfiles.get(i).getFaceUrl();
            mConverstionBean.add(mBean);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
