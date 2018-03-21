package com.wapchief.timdemo.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
    protected void convert(final ViewHolder holder, final TIMConverstionBean o, int position) {
        holder.setText(R.id.item_main_username, o.userName);
        holder.setText(R.id.item_main_content, o.content);
        holder.setText(R.id.item_main_time, o.time);
        String imgUrl="http://oyo2uh6cg.bkt.clouddn.com/head_other.png?e=1509441987&token=Krhu0H-zzbB3CAyvYjavLd-YpOFj1o8bnQehctw2:uTuFupwPo64YuPMnFOemLBxa3PM";
        if (!StringUtils.isEmpty(o.img)){
            imgUrl = o.img;
        }
        /*会话列表*/
        Glide.with(mContext)
                .resumeRequests();
//        Log.e("adapter", imgUrl+"\n"+o.img);
        if (type==0) {
//            Log.e("adapter2", imgUrl+"\n"+o.img);
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

            switch (o.conType){
                case C2C:

                        Glide.with(mContext)
                                .load(imgUrl)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                        holder.setImageBitmap(R.id.item_main_img, bitmap);

                                    }
                                });

                    break;
                case System:
                    holder.setImageResource(R.id.item_main_img, R.drawable.ic_news);
                    break;
                case Group:
                    holder.setImageResource(R.id.item_main_img, R.drawable.head_group);
                    break;
                case Invalid:
                    holder.setImageResource(R.id.item_main_img, R.drawable.head_discussion_group);
                    break;
                default:
                    break;

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
//            Log.e("adapter3", imgUrl+"\n"+o.img);
            if (!StringUtils.isEmpty(o.img)) {
//                holder.setImageBitmap(R.id.item_main_img, ImageUtils.returnBitMap(o.img));
                Glide.with(mContext)
                        .load(o.img)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                                holder.setImageBitmap(R.id.item_main_img, bitmap);

                            }
                        });

            }else {
                holder.setImageResource(R.id.item_main_img, R.drawable.head_other);

            }
        }

    }
}
