package com.wapchief.timdemo.ui.entity;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;

import java.io.Serializable;

/**
 * Created by wapchief on 2017/10/26.
 */

public class TIMConverstionBean implements Serializable{
    //登录状态
    public boolean login;
    //在线状态
    public boolean online;
    public int type;
    public String img;
    public String msgID;
    public String title;
    public String content;
    public String time;
    public String userName;
    public Boolean isFriends;
    public TIMConversation mConversation;
    public TIMConversationType conType;
    public int MsgType;
    public long sum;

    @Override
    public String toString() {
        return "TIMConverstionBean{" +
                "login=" + login +
                ", online=" + online +
                ", type=" + type +
                ", img='" + img + '\'' +
                ", msgID='" + msgID + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", userName='" + userName + '\'' +
                ", isFriends=" + isFriends +
                ", mConversation=" + mConversation +
                ", conType=" + conType +
                ", MsgType=" + MsgType +
                ", sum=" + sum +
                '}';
    }
}
