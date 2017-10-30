package com.wapchief.timdemo.utils;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 录音工具
 */
public class RecorderUtil {

    private static final String TAG = "RecorderUtil";

    private String mFileName = null;
    private MediaRecorder mRecorder = null;
    private long startTime;
    private long timeInterval;
    private boolean isRecording;

    public RecorderUtil(){
        mFileName = FileUtil.getCacheFilePath("tempAudio");
    }

    /**
     * 开始录音
     */
    public void startRecording() {
        if (mFileName == null) return;
        if (isRecording){
            mRecorder.release();
            mRecorder = null;
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        startTime = System.currentTimeMillis();
        try {
            mRecorder.prepare();
            mRecorder.start();
            isRecording = true;
        } catch (Exception e){
            Log.e(TAG, "prepare() failed");
        }

    }


    /**
     * 停止录音
     */
    public void stopRecording() {
        if (mFileName == null) return;
        timeInterval = System.currentTimeMillis() - startTime;
        try{
            if (timeInterval>1000){
                mRecorder.stop();
            }
            mRecorder.release();
            mRecorder = null;
            isRecording =false;
        }catch (Exception e){
            Log.e(TAG, "release() failed");
        }

    }


    /**
     * 获取录音文件
     */
    public byte[] getDate() {
        if (mFileName == null) return null;
        try{
            return readFile(new File(mFileName));
        }catch (IOException e){
            Log.e(TAG, "read file error" + e);
            return null;
        }
    }

    /**
     * 获取录音文件地址
     */
    public String getFilePath(){
        return mFileName;
    }


    /**
     * 获取录音时长,单位秒
     */
    public long getTimeInterval() {
        return timeInterval/1000;
    }


    /**
     * 将文件转化为byte[]
     *
     * @param file 输入文件
     */
    private static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }



}
