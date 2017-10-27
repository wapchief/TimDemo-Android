package com.wapchief.timdemo.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.ext.message.TIMMessageDraft;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.wapchief.timdemo.R;
import com.wapchief.timdemo.framework.BaseActivity;
import com.wapchief.timdemo.presentation.presenter.ChatPresenter;
import com.wapchief.timdemo.presentation.viewfeatures.ChatView;
import com.wapchief.timdemo.ui.adapter.ChatAdapter;
import com.wapchief.timdemo.ui.entity.CustomMessage;
import com.wapchief.timdemo.ui.entity.Message;
import com.wapchief.timdemo.ui.entity.MessageFactory;
import com.wapchief.timdemo.ui.entity.TextMessage;
import com.wapchief.timdemo.ui.views.ChatInput;
import com.wapchief.timdemo.ui.views.TemplateTitle;
import com.wapchief.timdemo.ui.views.VoiceSendingView;
import com.wapchief.timdemo.utils.MediaUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wapchief on 2017/10/19.
 */

public class ChatActivity extends BaseActivity implements ChatView {
    @BindView(R.id.chat_title)
    TemplateTitle mChatTitle;
    @BindView(R.id.input_panel)
    ChatInput mInputPanel;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.voice_sending)
    VoiceSendingView mVoiceSending;
    @BindView(R.id.root)
    RelativeLayout mRoot;
    private List<Message> mMessageList = new ArrayList<>();
    private ChatAdapter mAdapter;
    private TIMConversationType mType;
    private Uri mUri;
    private String identify, titleStr;
    private Handler mHandler = new Handler();
    private ChatPresenter mPresenter;
    private static String TAG = "ChatActivity";

    @Override
    protected int setContentView() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        mType = (TIMConversationType) getIntent().getSerializableExtra("data");
        Log.e(TAG,"Type:"+ mType.name());
        mPresenter = new ChatPresenter(this, identify, mType);
        mInputPanel.setChatView(this);
        mAdapter = new ChatAdapter(this, R.layout.item_message, mMessageList);
        mListView.setAdapter(mAdapter);
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mInputPanel.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int firstItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    mPresenter.getMessage(mMessageList.size() > 0 ? mMessageList.get(0).getMessage() : null);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;
            }
        });

        registerForContextMenu(mListView);

        switch (mType) {
            case C2C:
                mChatTitle.setMoreImg(R.drawable.btn_person);
//                if (FriendshipInfo.getInstance().isFriend(identify)){
//                    mChatTitle.setMoreImgAction(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
//                            intent.putExtra("identify", identify);
//                            startActivity(intent);
//                        }
//                    });
//                    FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
//                    mChatTitle.setTitleText(titleStr = profile == null ? identify : profile.getName());
//                }else{
//                    mChatTitle.setMoreImgAction(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent person = new Intent(ChatActivity.this,AddFriendActivity.class);
//                            person.putExtra("id",identify);
//                            person.putExtra("name",identify);
//                            startActivity(person);
//                        }
//                    });
//                    mChatTitle.setTitleText(titleStr = identify);
//                }
                mChatTitle.setTitleText(titleStr = identify);
                break;
            case Group:

                break;
        }
        mPresenter.start();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        /*保存草稿*/
        if (mInputPanel.getText().length() > 0) {
            TextMessage message = new TextMessage(mInputPanel.getText());
            mPresenter.saveDraft(message.getMessage());
        } else {
            mPresenter.saveDraft(null);
        }
        mPresenter.readMessages();
        MediaUtil.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    /**
     * 显示消息
     *
     * @param message
     */
    @Override
    public void showMessage(TIMMessage message) {
        Log.e(TAG, "message:" + message.getMsg());
        if (mAdapter == null) {
            mAdapter.notifyDataSetChanged();
        } else {
            Message message1 = MessageFactory.getMessage(message);
            if (message1 != null) {
                if (message1 instanceof CustomMessage) {
                    CustomMessage.Type messageType = ((CustomMessage) message1).getType();
                    switch (messageType) {
                        case TYPING:
                            mChatTitle.setTitleText("对方正在输入...");
                            mHandler.removeCallbacks(resetTitle);
                            break;
                        default:

                            break;
                    }
                }else {
                    if (mMessageList.size() == 0) {
                        message1.setHasTime(null);
                    }else {
                        message1.setHasTime(mMessageList.get(mMessageList.size() - 1).getMessage());
                    }
                    mMessageList.add(message1);
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(mAdapter.getCount() - 1);
                }
            }
        }
    }

    /*标题设置为对象*/
    private Runnable resetTitle = new Runnable() {
        @Override
        public void run() {
            mChatTitle.setTitleText(titleStr);

        }
    };

    /**
     * 显示消息列表
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        Log.e(TAG, "messages:" + messages.toString());
        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i){
            Message mMessage = MessageFactory.getMessage(messages.get(i));
            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted) continue;
            if (mMessage instanceof CustomMessage && (((CustomMessage) mMessage).getType() == CustomMessage.Type.TYPING ||
                    ((CustomMessage) mMessage).getType() == CustomMessage.Type.INVALID)) continue;
            ++newMsgNum;
            if (i != messages.size() - 1){
                mMessage.setHasTime(messages.get(i+1));
                mMessageList.add(0, mMessage);
            }else{
                mMessage.setHasTime(null);
                mMessageList.add(0, mMessage);
            }
        }
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(newMsgNum);
    }

    @Override
    public void showRevokeMessage(TIMMessageLocator timMessageLocator) {

    }

    @Override
    public void clearAllMessage() {

    }

    @Override
    public void onSendMessageSuccess(TIMMessage message) {

    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {

    }

    @Override
    public void sendImage() {

    }

    @Override
    public void sendPhoto() {

    }

    @Override
    public void sendText() {

    }

    @Override
    public void sendFile() {

    }

    @Override
    public void startSendVoice() {

    }

    @Override
    public void endSendVoice() {

    }

    @Override
    public void sendVideo(String fileName) {

    }

    @Override
    public void cancelSendVoice() {

    }

    @Override
    public void sending() {

    }

    @Override
    public void showDraft(TIMMessageDraft draft) {

    }

    @Override
    public void videoAction() {

    }

    @Override
    public void showToast(String msg) {

    }
}
