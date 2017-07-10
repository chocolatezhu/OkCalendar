package com.ppzhu.calendar.base;

import android.content.Context;
import android.content.Intent;
import android.test.suitebuilder.annotation.Suppress;

/**
 * @author zzl
 * Created 16-5-16.
 */
public class BaseController {
    protected Context context;

    public BaseController( Context context ) {
        this.context = context;
    }

    @Suppress
    public void onActivityCreate() {
    }

    @Suppress
    public void onActivityResume() {
    }

    @Suppress
    public void onActivityPause() {
    }

    @Suppress
    public void onActivityDestroy() {
    }

    @Suppress
    public void onBackPressed() {
    }

    @Suppress
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
    }

    @Suppress
    public void onclickHomeKey() {
    }
}
