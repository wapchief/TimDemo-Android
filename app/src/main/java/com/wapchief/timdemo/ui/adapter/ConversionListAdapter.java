package com.wapchief.timdemo.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.tencent.imsdk.TIMConversationType;
import com.wapchief.timdemo.R;
import com.wapchief.timdemo.ui.ChatActivity;
import com.wapchief.timdemo.ui.activity.ChatGroupActivity;
import com.wapchief.timdemo.ui.entity.TIMConverstionBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 会话列表
 * Created by wapchief on 2017/10/26.
 */

public class ConversionListAdapter extends CommonAdapter<TIMConverstionBean>{

    Context mContext;
    int type = 0;
    long sum = 0;
    private List<TIMConverstionBean> datas;
    public ConversionListAdapter(Context context, int layoutId, List<TIMConverstionBean> datas,int type) {
        super(context, layoutId, datas);
        this.mContext = context;
        this.type = type;
        this.datas = datas;
    }

    @Override
    protected void convert(ViewHolder holder, final TIMConverstionBean o, int position) {
        holder.setText(R.id.item_main_username, o.userName);
        holder.setText(R.id.item_main_content, o.content);
        holder.setText(R.id.item_main_time, o.time);
        /*会话列表*/
        if (type==0) {
            /*未读消息*/
            sum = o.sum;
            if (sum>0) {
                holder.setVisible(R.id.item_main_sum, true);
                holder.setText(R.id.item_main_sum, sum + "条未读");
            }else {
                holder.setVisible(R.id.item_main_sum, false);
            }
            /*最后一条消息时间*/
            holder.setVisible(R.id.item_main_time, true);
            holder.setText(R.id.item_main_time, o.time);

            /*头像*/
            if (!StringUtils.isEmpty(o.img)) {
                holder.setImageBitmap(R.id.item_main_img, ImageUtils.getBitmap(o.img));
            } else {
                if (o.conType == TIMConversationType.C2C) {

                    holder.setImageResource(R.id.item_main_img, R.drawable.head_other);
                } else {
                    holder.setImageResource(R.id.item_main_img, R.drawable.ic_news);
                }
            }
            holder.setOnClickListener(R.id.item_main, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (o.type == 0) {
                        switch (o.conType) {
                            case C2C:
                                Intent intent = new Intent(mContext, ChatActivity.class);
                                intent.putExtra("data", o.conType);
                                intent.putExtra("peer", o.userName);
                                mContext.startActivity(intent);
                                break;
                            case Group:
                                Intent intent1 = new Intent(mContext, ChatGroupActivity.class);
                                mContext.startActivity(intent1);
                                break;
                            default:
                                ToastUtils.showShort("未知的会话类型");
                                break;
                        }


                    }
                }
            });
            return;
        }
        /*好友列表*/
        if (type == 1) {
            if (!StringUtils.isEmpty(o.img)) {
                holder.setImageBitmap(R.id.item_main_img, ImageUtils.getBitmap(o.img));
            }
        }
    }
}
