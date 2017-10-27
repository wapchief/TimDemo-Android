package com.wapchief.timdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.wapchief.timdemo.framework.BaseActivity;
import com.wapchief.timdemo.ui.entity.TabEntity;
import com.wapchief.timdemo.ui.fragment.FragmentFactory;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {


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
    @BindView(R.id.main_vp)
    ViewPager mMainVp;
    @BindView(R.id.main_tab)
    CommonTabLayout mMainTab;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private String[] mTitles = {"消息", "联系人", "设置"};
    private int[] mTabIcon = {R.mipmap.ic_launcher};
    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initTab();
        initListener();
    }

    /*tab和vp切换监听*/
    private void initListener() {
        mMainTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int i) {
                titleChange(i);
                mMainVp.setCurrentItem(i);
            }

            @Override
            public void onTabReselect(int i) {

            }
        });

        mMainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                titleChange(position);
                mMainTab.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mMainVp.setCurrentItem(0);

    }

    /*初始化Tab*/
    private void initTab() {
        //设置选中和未选中的字体颜色
        mMainTab.setTextSelectColor(getResources().getColor(R.color.colorPrimary));
        mMainTab.setTextUnselectColor(getResources().getColor(R.color.colorUnselect));
        for (int i = 0; i < mTitles.length; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], mTabIcon[0], mTabIcon[0]));
        }
        mMainTab.setTabData(mTabEntities);
        mMainVp.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));


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


    /*设置标题栏变化*/
    private void titleChange(int position){
        mTitleBarTitle.setText(mTitles[position]);
    }

    /*viewpager适配器*/
    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.createFragment(position);
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitBy2Click();
        }
        return false;
    }

    /**
     * 双击退出
     */
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            ToastUtils.showShort("再按一次退出程序");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            System.exit(0);
        }
    }
}
