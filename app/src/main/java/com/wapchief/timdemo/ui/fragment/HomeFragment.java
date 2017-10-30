package com.wapchief.timdemo.ui.fragment;

import android.content.Context;
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
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageListener;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.tencent.imsdk.ext.message.TIMManagerExt;
import com.wapchief.timdemo.R;
import com.wapchief.timdemo.presentation.presenter.ChatPresenter;
import com.wapchief.timdemo.ui.adapter.ConversionListAdapter;
import com.wapchief.timdemo.ui.entity.TIMConverstionBean;
import com.wapchief.timdemo.utils.TimeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by wapchief on 2017/10/19.
 */

public class HomeFragment extends Fragment implements TIMMessageListener {

    @BindView(R.id.conver_rv)
    RecyclerView mConverRv;
    Unbinder unbinder;
    ConversionListAdapter mAdapter;
    List<TIMConverstionBean> mConverstionBean = new ArrayList<>();
    @BindView(R.id.home_refresh)
    SmartRefreshLayout mHomeRefresh;
    private ChatPresenter mPresenter;
    private String TAG = "HomeFragment";
    Context mContext;

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
        mAdapter = new ConversionListAdapter(getActivity(), R.layout.item_main_message, mConverstionBean, 0);
        mConverRv.setAdapter(mAdapter);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        mConverstionBean.clear();
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
        TIMConverstionBean mBean;
        for (int i = 0; i < list.size(); i++) {
            mBean = new TIMConverstionBean();
//            mBean.title =list.get(i).getPeer().
            mBean.conType = list.get(i).getType();
            mBean.type = 0;
            mBean.sum = getMsgNum(list.get(i).getType(), list.get(i).getPeer());
            mBean.time = getLastTime(list.get(i).getType(), list.get(i).getPeer());
            switch (list.get(i).getType()) {
                case C2C:
                    mBean.userName = getUserName(list.get(i).getType(), list.get(i).getPeer());
                    mBean.content = "" + msgString(list.get(i).getType(), list.get(i).getPeer());
                    break;
                case Group:
                    mBean.userName = getUserName(list.get(i).getType(), list.get(i).getPeer());
                    mBean.content = "[群聊]";
                    break;
                case System:
                    mBean.userName = "系统通知";
                    mBean.content = msgString(list.get(i).getType(), list.get(i).getPeer());
                    break;
                default:
                    mBean.userName = "未知类型";
                    mBean.content = "[未知类型]";
                    break;
            }
//            mPresenter = new ChatPresenter(getActivity(), list.get(i).getPeer(), list.get(i).getType());
            mConverstionBean.add(mBean);
        }
        Log.e(TAG, "mBean:" + mConverstionBean);
        mAdapter.notifyDataSetChanged();
    }


    /*解析会话*/
    private String msgString(TIMConversationType type, String peer) {
        //得到会话
        TIMConversation conversation = TIMManager.getInstance().getConversation(type, peer);
        //同步获取会话的最后几条消息
        TIMConversationExt ext = new TIMConversationExt(conversation);
        //拿到最后一条
        TIMMessage message = ext.getLastMsgs(1).get(0);
        //解析
        TIMElem elem = message.getElement(0);
        //获取消息类型
        Log.e(TAG, "elem:" + message.getElement(0) + "\n" + message.getSender());
        TIMElemType elemType = elem.getType();
        switch (elemType) {
            case Text:
                return ((TIMTextElem) message.getElement(0)).getText();
            case Image:
                return "[图片]";
            case Video:
                return "[视频]";
            case Sound:
                return "[语音]";
            default:
                return "[系统消息]";

        }
    }

    /*获取对方资料*/
    private String getUserName(TIMConversationType type, String peer) {
        //得到会话
        TIMConversation conversation = TIMManager.getInstance().getConversation(type, peer);
        //同步获取会话的最后几条消息
        TIMConversationExt ext = new TIMConversationExt(conversation);
        //拿到最后一条
        TIMMessage message = ext.getLastMsgs(1).get(0);

        return message.getSender();
    }

    /*获取未读消息数*/
    private long getMsgNum(TIMConversationType type, String peer) {
        //得到会话
        TIMConversation conversation = TIMManager.getInstance().getConversation(type, peer);
        //同步获取会话的最后几条消息
        TIMConversationExt ext = new TIMConversationExt(conversation);
        return ext.getUnreadMessageNum();
    }

    /*获取最后一条消息的时间*/
    private String getLastTime(TIMConversationType type, String peer) {
        //得到会话
        TIMConversation conversation = TIMManager.getInstance().getConversation(type, peer);
        //同步获取会话的最后几条消息
        TIMConversationExt ext = new TIMConversationExt(conversation);
        //拿到最后一条
        TIMMessage message = ext.getLastMsgs(1).get(0);
        DateFormat format = new SimpleDateFormat("MM-dd HH:mm");
//        Log.e(TAG, "time:" + message.timestamp()+"\n"+
//                com.wapchief.timdemo.utils.TimeUtils.TimeStamp2Date(message.timestamp()+"","MM-dd HH:mm"));

        return TimeUtils.TimeStamp2Date(message.timestamp() + "", "MM-dd HH:mm");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        Log.e(TAG,"新消息："+ list.size());
        return false;
    }


}
