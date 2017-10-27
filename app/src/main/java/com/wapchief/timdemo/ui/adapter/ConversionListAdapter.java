package com.wapchief.timdemo.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
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
    public ConversionListAdapter(Context context, int layoutId, List<TIMConverstionBean> datas) {
        super(context, layoutId, datas);
        this.mContext = context;
    }

    @Override
    protected void convert(ViewHolder holder, final TIMConverstionBean o, int position) {
        if (!StringUtils.isEmpty(o.img)) {
            holder.setImageBitmap(R.id.item_main_img, ImageUtils.getBitmap(o.img));
        }
        holder.setText(R.id.item_main_username, o.userName);
        holder.setText(R.id.item_main_content, o.content);
        holder.setText(R.id.item_main_time, o.time);
        holder.setOnClickListener(R.id.item_main, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (o.type==0){
                    switch (o.conType){
                        case C2C:
                            Intent intent = new Intent(mContext, ChatActivity.class);
                            intent.putExtra("data", o.conType);
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
    }
}
