package com.wapchief.timdemo.presenter;

import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.ext.message.TIMConversationExt;
import com.wapchief.timdemo.utils.TimeUtils;

/**
 * Created by wapchief on 2017/10/30.
 * 会话列表控制器
 */

public class CovListPresenter {

    /*获取对方资料*/
    private String getUserName(TIMConversationType type, String peer){
        //得到会话
        TIMConversation conversation= TIMManager.getInstance().getConversation(type, peer);
        //同步获取会话的最后几条消息
        TIMConversationExt ext = new TIMConversationExt(conversation);
        //拿到最后一条
        TIMMessage message = ext.getLastMsgs(1).get(0);

        return message.getSender();
    }


    /*获取未读消息数*/
    private long getMsgNum(TIMConversationType type,String peer){
        //得到会话
        TIMConversation conversation=TIMManager.getInstance().getConversation(type, peer);
        //同步获取会话的最后几条消息
        TIMConversationExt ext = new TIMConversationExt(conversation);
        return ext.getUnreadMessageNum();
    }

    /*解析会话*/
    private String msgString(TIMConversationType type,String peer){
        //得到会话
        TIMConversation conversation=TIMManager.getInstance().getConversation(type, peer);
        //同步获取会话的最后几条消息
        TIMConversationExt ext = new TIMConversationExt(conversation);
        //拿到最后一条
        TIMMessage message = ext.getLastMsgs(1).get(0);
        //解析
        TIMElem elem = message.getElement(0);
        //获取消息类型
        TIMElemType elemType=elem.getType();
        switch (elemType) {
            case Text:
                return  ((TIMTextElem) message.getElement(0)).getText();
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

    /*获取最后一条消息的时间*/
    private String getLastTime(TIMConversationType type,String peer){
        //得到会话
        TIMConversation conversation=TIMManager.getInstance().getConversation(type, peer);
        //同步获取会话的最后几条消息
        TIMConversationExt ext = new TIMConversationExt(conversation);
        //拿到最后一条
        TIMMessage message = ext.getLastMsgs(1).get(0);
        return TimeUtils.ms2date("MM-dd HH:mm",message.timestamp());
    }
}
