package com.tencent.qcloud.presentation.viewfeatures;

/**
 * 消息回调接口
 */
public interface MessageView {


    void onStatusChange(Status newStatus);


    enum Status{
        NORMAL,
        SENDING,
        ERROR,
    }
}
