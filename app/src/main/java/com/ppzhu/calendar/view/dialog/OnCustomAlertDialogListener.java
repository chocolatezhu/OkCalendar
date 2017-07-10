package com.ppzhu.calendar.view.dialog;

import android.view.KeyEvent;

/**
 * @author zzl
 * Created on 2016/8/24.
 */
public interface OnCustomAlertDialogListener {
    void onPositiveButton();
    void onNeutralButton();
    void onNegativeButton();
    void onItemClick(int position);
    void onSingleChoiceItemClick(int position);
    void onMultiChoiceItemClick(int position, boolean isChoice);
    void onCancel();
    void onDismiss();
    boolean onKey(int keyCode, KeyEvent keyEvent);
}
