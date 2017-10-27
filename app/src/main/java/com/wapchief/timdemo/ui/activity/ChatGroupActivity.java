package com.wapchief.timdemo.ui.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wapchief.timdemo.R;
import com.wapchief.timdemo.framework.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wapchief on 2017/10/26.
 */

public class ChatGroupActivity extends BaseActivity {
    @BindView(R.id.title_bar_backTv)
    TextView mTitleBarBackTv;
    @BindView(R.id.title_bar_back)
    LinearLayout mTitleBarBack;
    @BindView(R.id.title_bar_title)
    TextView mTitleBarTitle;
    @BindView(R.id.title_options_tv)
    TextView mTitleOptionsTv;
    @BindView(R.id.title_options_img)
    ImageView mTitleOptionsImg;
    @BindView(R.id.title)
    RelativeLayout mTitle;

    @Override
    protected int setContentView() {
        return R.layout.activity_groupchat;
    }

    @Override
    protected void initView() {
        initTitle();
    }

    private void initTitle() {
        mTitleBarTitle.setText("群聊");
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
}
