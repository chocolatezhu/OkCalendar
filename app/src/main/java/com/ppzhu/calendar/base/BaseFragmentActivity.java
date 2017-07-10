package com.ppzhu.calendar.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.test.suitebuilder.annotation.Suppress;

/**
 * @author zzl
 *         Created on 16-5-16.
 */
public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
