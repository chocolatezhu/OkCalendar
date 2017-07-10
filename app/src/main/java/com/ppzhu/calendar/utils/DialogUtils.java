package com.ppzhu.calendar.utils;

import android.content.Context;


import com.ppzhu.calendar.R;
import com.ppzhu.calendar.view.dialog.CustomAlertDialog;
import com.ppzhu.calendar.view.dialog.ImplementCustomAlertDialogListener;

import java.util.List;

/**
 * @author zzl
 * Dialog工具类，这里统一定义各种弹框
 * Created on 2016/8/24.
 */
public class DialogUtils {
    public static CustomAlertDialog scheduleDeleteDialog(Context context, ImplementCustomAlertDialogListener listener){
        if (null == context){
            return null;
        }

        CustomAlertDialog dlg = new CustomAlertDialog(context, listener);
        dlg.setMessage(R.string.schedule_delete_dialog_message);
        dlg.setPositiveButton(R.string.schedule_delete_sure);
        dlg.setNegativeButton(R.string.schedule_delete_cancel);
        dlg.setCanceledOnTouchOutside(true);
        return dlg;
    }


    public static CustomAlertDialog scheduleMultiAlertDialog(Context context, List<String> list, ImplementCustomAlertDialogListener listener){
        if (null == context){
            return null;
        }

        CustomAlertDialog dlg = new CustomAlertDialog(context, listener);
        dlg.setNegativeButton("查看详情");
        dlg.setPositiveButton("确定");
        dlg.setItems(list.toArray(new String[list.size()]));
        dlg.setCanceledOnTouchOutside(false);
        dlg.setCancelable(true);
        dlg.setOnKeyListener();
        return dlg;
    }

    public static CustomAlertDialog scheduleSaveDialog(Context context, ImplementCustomAlertDialogListener listener) {
        if (null == context){
            return null;
        }

        CustomAlertDialog dlg = new CustomAlertDialog(context, listener);
        dlg.setNegativeButton("不保存");
        dlg.setPositiveButton("保存");
        dlg.setMessage("是否保存该日程？");
        dlg.setCanceledOnTouchOutside(true);
        dlg.setCancelable(true);
        dlg.setOnKeyListener();
        return dlg;
    }


}
