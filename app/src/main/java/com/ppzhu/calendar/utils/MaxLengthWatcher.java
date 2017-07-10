package com.ppzhu.calendar.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.ppzhu.calendar.R;


/**
 * @author zzl
 * 监听EditText的输入内容，用于限制EditText的最大长度
 * Created on 2016/10/8.
 */
public class MaxLengthWatcher implements TextWatcher {
    private int maxLen = 0;
    private Context context;

    private int mEnd;
    private int mStart;

    public MaxLengthWatcher(Context context, int maxLen) {
        this.maxLen = maxLen;
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int exceed = s.length() - maxLen;
        if (exceed > 0) {
            mStart = start + count - exceed;
            mEnd = start + count;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > maxLen) {
            s.delete(mStart, mEnd);
            //提示超过最大字符限制
            if (null != context) {
                T.getInstance(context).s(context.getResources().getString(R.string.input_maxlength));
            }
        }
    }
}
