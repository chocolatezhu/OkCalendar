package com.ppzhu.calendar.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.test.suitebuilder.annotation.Suppress;


/**
 * @author zzl
 * Created on 16-5-16.
 */
public abstract class BaseFragment extends Fragment {
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public void onActivityCreated( @Nullable Bundle savedInstanceState ) {
        super.onActivityCreated( savedInstanceState );
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        super.onActivityResult( requestCode, resultCode, data );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //fragment可见
    @Suppress
    public void onFragmentVisible(){

    }
    //fragment不可见
    @Suppress
    public void onFragmentInvisible(){

    }

}
