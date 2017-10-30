package com.wapchief.timdemo.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.ext.message.TIMMessageDraft;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.wapchief.timdemo.R;
import com.wapchief.timdemo.framework.BaseActivity;
import com.wapchief.timdemo.presentation.presenter.ChatPresenter;
import com.wapchief.timdemo.presentation.viewfeatures.ChatView;
import com.wapchief.timdemo.ui.activity.ImagePreviewActivity;
import com.wapchief.timdemo.ui.activity.UserDataActivity;
import com.wapchief.timdemo.ui.adapter.ChatAdapter;
import com.wapchief.timdemo.ui.entity.CustomMessage;
import com.wapchief.timdemo.ui.entity.ImageMessage;
import com.wapchief.timdemo.ui.entity.Message;
import com.wapchief.timdemo.ui.entity.MessageFactory;
import com.wapchief.timdemo.ui.entity.TextMessage;
import com.wapchief.timdemo.ui.entity.VoiceMessage;
import com.wapchief.timdemo.ui.views.ChatInput;
import com.wapchief.timdemo.ui.views.TemplateTitle;
import com.wapchief.timdemo.ui.views.VoiceSendingView;
import com.wapchief.timdemo.utils.FileUtil;
import com.wapchief.timdemo.utils.MediaUtil;
import com.wapchief.timdemo.utils.RecorderUtil;

import java.io.File;
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
    private String mPeer, titleStr;
    private Handler mHandler = new Handler();
    private ChatPresenter mPresenter;
    private static String TAG = "ChatActivity";
    //选择图片回调
    private static final int IMAGE_STORE = 200;
    private static final int IMAGE_PREVIEW = 400;
    public static final int RESULT_OK = -1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private static final int FILE_CODE = 300;
    private static final int VIDEO_RECORD = 500;
    private Uri fileUri;
    private RecorderUtil mRecorderUtil = new RecorderUtil();
    @Override
    protected int setContentView() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {

        mType = (TIMConversationType) getIntent().getSerializableExtra("data");
        mPeer = getIntent().getStringExtra("peer");
        Log.e(TAG, "Type:" + mType.name() + "," + mPeer);
        mChatTitle.setTitleText(mPeer);
        //初始化会话
        mPresenter = new ChatPresenter(this, mPeer, mType);
        //加载页面默认会话消息上报
        mPresenter.readMessages();
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

                mChatTitle.setMoreImgAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, UserDataActivity.class);
                        intent.putExtra("mPeer", mPeer);
                        startActivity(intent);
                    }
                });

                mChatTitle.setTitleText(titleStr = mPeer);
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
//        Log.e(TAG, "message:" + message.getMsg());
        if (message == null) {
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
                            mHandler.postDelayed(resetTitle, 1000);
                            break;
                        default:

                            break;
                    }
                } else {
                    if (mMessageList.size() == 0) {
                        message1.setHasTime(null);
                    } else {
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
     *
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        Log.e(TAG, "messages:" + messages.toString());
        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i) {
            Message mMessage = MessageFactory.getMessage(messages.get(i));
            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted)
                continue;
            if (mMessage instanceof CustomMessage && (((CustomMessage) mMessage).getType() == CustomMessage.Type.TYPING ||
                    ((CustomMessage) mMessage).getType() == CustomMessage.Type.INVALID)) continue;
            ++newMsgNum;
            if (i != messages.size() - 1) {
                mMessage.setHasTime(messages.get(i + 1));
                mMessageList.add(0, mMessage);
            } else {
                mMessage.setHasTime(null);
                mMessageList.add(0, mMessage);
            }
        }
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(newMsgNum);
    }

    /*撤回事件*/
    @Override
    public void showRevokeMessage(TIMMessageLocator timMessageLocator) {

    }

    @Override
    public void clearAllMessage() {
        mMessageList.clear();
    }

    /**
     * 发送成功
     *
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        Log.e(TAG, "onSendMessageSuccess:" + message.getMsg());
        showMessage(message);
    }

    /**
     * 消息发送失败
     *
     * @param code    返回码
     * @param desc    返回描述
     * @param message 发送的消息
     */
    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        long id = message.getMsgUniqueId();
        for (Message msg : mMessageList) {
            if (msg.getMessage().getMsgUniqueId() == id) {
                switch (code) {
                    case 80001:
                        //发送内容包含敏感词
                        msg.setDesc(getString(R.string.chat_content_bad));
                        mAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }
        Log.e(TAG, "发送失败：" + desc);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void sendImage() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_STORE);
    }

    /**
     * 发送照片消息
     */
    @Override
    public void sendPhoto() {
        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent_photo.resolveActivity(getPackageManager()) != null) {
            File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
            if (tempFile != null) {
                fileUri = Uri.fromFile(tempFile);
            }
            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void sendText() {
        Log.e(TAG, "发送文本消息：" + mInputPanel.getText());
        Message message = new TextMessage(mInputPanel.getText());
        mPresenter.sendMessage(message.getMessage());
        mInputPanel.setText("");
    }

    @Override
    public void sendFile() {

    }

    /*开始录音*/
    @Override
    public void startSendVoice() {
        mVoiceSending.setVisibility(View.VISIBLE);
        mVoiceSending.showRecording();
        mRecorderUtil.startRecording();
    }

    /*结束录音,并发送*/
    @Override
    public void endSendVoice() {
        mVoiceSending.release();
        mVoiceSending.setVisibility(View.GONE);
        mRecorderUtil.stopRecording();
        if (mRecorderUtil.getTimeInterval() < 1) {
            ToastUtils.showShort("时间过短");
        } else if (mRecorderUtil.getTimeInterval() > 60) {
            ToastUtils.showShort("录音时间过长");
        }else {
            Message message = new VoiceMessage(mRecorderUtil.getTimeInterval(), mRecorderUtil.getFilePath());
            mPresenter.sendMessage(message.getMessage());
        }
    }

    @Override
    public void sendVideo(String fileName) {

    }

    @Override
    public void cancelSendVoice() {

    }

    /**
     * 正在发送
     */
    @Override
    public void sending() {
        if (mType == TIMConversationType.C2C) {
            Message message = new CustomMessage(CustomMessage.Type.TYPING);
            mPresenter.sendOnlineMessage(message.getMessage());
        }
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

    private void showImagePreview(String path) {
        if (path == null) {
            return;
        }
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //相机回调
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                showImagePreview(fileUri.getPath());
            }
            //选择图片回调
        } else if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getFilePath(ChatActivity.this, data.getData()));
            }
            //图片预览回调
        } else if (requestCode == IMAGE_PREVIEW) {
            if (resultCode == RESULT_OK) {
                boolean isOri = data.getBooleanExtra("isOri", false);
                String path = data.getStringExtra("path");
                File file = new File(path);
                if (file.exists()) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    if (file.length() == 0 && options.outWidth == 0) {
                        Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                    } else {
                        if (file.length() > 1024 * 1024 * 10) {
                            Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
                        } else {
                            Message message = new ImageMessage(path, isOri);
                            mPresenter.sendMessage(message.getMessage());
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
