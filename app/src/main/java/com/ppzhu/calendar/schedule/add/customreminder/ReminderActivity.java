package com.ppzhu.calendar.schedule.add.customreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ppzhu.calendar.R;
import com.ppzhu.calendar.constants.ConstData;
import com.ppzhu.calendar.schedule.add.CustomAdapter;
import com.ppzhu.calendar.utils.StatusBarUtil;


public class ReminderActivity extends Activity {
    private CustomAdapter mRepeatAdapter;

    //默认值设置为3
    private int num = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat);
        getWindow().setBackgroundDrawable(null);
        getIntentInfo();
        initView();
    }

    private void getIntentInfo() {
        Intent it = getIntent();
        num = it.getIntExtra(ConstData.INTENT_SCEDULE_REMIND_EDIT_KEY, 3);
    }

    private void initView(){
        ListView mListView = (ListView) findViewById(R.id.schedule_list);
        TextView mTvTitle = (TextView) findViewById(R.id.tv_title_bar);
        mTvTitle.setText(getResources().getString(R.string.schedule_remind_title));
        ImageView mIvTitle = (ImageView) findViewById(R.id.iv_title_bar);
        mRepeatAdapter = new CustomAdapter(this, ConstData.REMINDERSTR);
        mListView.setAdapter(mRepeatAdapter);
        mRepeatAdapter.setSelectedIndex(num);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                num = position;
                mRepeatAdapter.setSelectedIndex(position);
                mRepeatAdapter.notifyDataSetChanged();
                goBack();
            }
        });
        mIvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.menu_tab_red));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
          goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBack(){
       // String strs = str[num];
        Intent intent = getIntent();
        intent.putExtra("reminder", num);
        // 设置该SelectActivity的结果码，并设置结束之后退回的Activity
        setResult(ConstData.reminderCode, intent);
        // 结束SelectCityActivity。
        finish();
    }
}
