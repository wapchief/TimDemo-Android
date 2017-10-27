package com.tencent.qcloud.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;


/**
 * 列表选项控件
 */
public class ListPickerDialog extends DialogFragment {


    String[] list;
    String tag = "listPicker";
    DialogInterface.OnClickListener listener;

    public void show(String[] list, FragmentManager fm, DialogInterface.OnClickListener listener){
        this.list = list;
        this.listener = listener;
        show(fm, tag);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(list, listener);
        return builder.create();
    }

}
