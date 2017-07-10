package com.ppzhu.calendar.schedule.add.customrepeat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

import java.util.ArrayList;

public class RepeatActivity extends FragmentActivity {
    private CustomAdapter mRepeatAdapter;
    //选中第几行
    private int selectNum = 0;
    //自定义重复显示的字符串
    private String mCustomRepeatStr;
    //是否自定义重复
    private boolean isCustomRepeatSet = false;
    //保存选中的重复，自定义重复时有可能有多个
    private ArrayList<Integer> list = new ArrayList<>();
    private ArrayList<Integer> mCustomIntegerList = new ArrayList<>();
    public static final int customRepeatCode = 0x6;

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
        Bundle bundle = it.getExtras();
        isCustomRepeatSet = bundle.getBoolean(ConstData.INTENT_SCHEDULE_REPEAT_IS_CUSTOM_REPEAT, isCustomRepeatSet);
        list = bundle.getIntegerArrayList(ConstData.INTENT_SCHEDULE_REPEAT_EDIT_KEY);
        if (null == list || list.isEmpty()){
            return;
        }

        if (!isCustomRepeatSet) {
            //单个重复只有一个，就是第一个
            selectNum = list.get(0);
        } else {
            mCustomIntegerList = list;
            if (list.size() == ConstData.CUSTOMREPEATSTR.length) {
                //这里设置为每天重复
                selectNum = 1;
            } else {
                //自定义重复选项是最后一个
                selectNum = ConstData.REPEATSTR.length - 1;
                mCustomRepeatStr = getCustomRepeatStr(mCustomIntegerList);
            }
        }
    }

    private void initView() {
        ListView mListView = (ListView) findViewById(R.id.schedule_list);
        TextView mTvTitle = (TextView) findViewById(R.id.tv_title_bar);
        ImageView mIvTitle = (ImageView) findViewById(R.id.iv_title_bar);
        mTvTitle.setText(getResources().getString(R.string.schedule_repeat_title));
        mRepeatAdapter = new CustomAdapter(this, ConstData.REPEATSTR);

        mRepeatAdapter.setSelectedIndex(selectNum);
        mRepeatAdapter.setCustomRepeatStr(mCustomRepeatStr);

        mListView.setAdapter(mRepeatAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (getResources().getString(R.string.schedule_repeat_custom_title).equals(ConstData.REPEATSTR[position])) {
                    isCustomRepeatSet = true;
                    Intent intentRepeat = new Intent();
                    intentRepeat.putExtra(ConstData.INTENT_SCHEDULE_REPEAT_FREE_SET_ITEM , mCustomIntegerList);
                    intentRepeat.setClass(RepeatActivity.this, CustomRepeatActivity.class);
                    startActivityForResult(intentRepeat, customRepeatCode);
                } else {
                    selectNum = position;
                    isCustomRepeatSet = false;
                    mRepeatAdapter.setSelectedIndex(selectNum);
                    mRepeatAdapter.notifyDataSetChanged();
                    goBack();
                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case customRepeatCode:
                refreshCustomList(data);
                break;
            default:
                break;
        }
    }

    /**
     * 刷新列表
     * */
    private void refreshCustomList(Intent data) {
        Bundle b = data.getExtras();
        list = b.getIntegerArrayList("repeat");
        mCustomIntegerList = list;
        if (null == list || list.isEmpty()){
            selectNum = 0;
            isCustomRepeatSet = false;
            mRepeatAdapter.setCustomRepeatStr("");
            mRepeatAdapter.setSelectedIndex(selectNum);
            mRepeatAdapter.notifyDataSetChanged();
        } else {
            if (isCustomRepeatSet) {
                mCustomRepeatStr = getCustomRepeatStr(list);
                if (list.size() == ConstData.CUSTOMREPEATSTR.length){
                    //这里设置为每天重复
                    selectNum = 1;
                    mRepeatAdapter.setCustomRepeatStr("");
                    mRepeatAdapter.setSelectedIndex(selectNum);
                    mRepeatAdapter.notifyDataSetChanged();
                } else {
                    //自定义重复选项是最后一个
                    selectNum = ConstData.REPEATSTR.length - 1;
                    mRepeatAdapter.setCustomRepeatStr(mCustomRepeatStr);
                    mRepeatAdapter.setSelectedIndex(selectNum);
                    mRepeatAdapter.notifyDataSetChanged();
                }
            } else {
                mRepeatAdapter.setCustomRepeatStr("");
                mRepeatAdapter.setSelectedIndex(selectNum);
                mRepeatAdapter.notifyDataSetChanged();
            }
        }
    }

    private String getCustomRepeatStr(ArrayList<Integer> repeatList){
        if (null == repeatList || repeatList.isEmpty()){
            return "";
        }
        if (repeatList.size() == 7){
            return ConstData.REPEATSTR[1];
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < repeatList.size(); i++) {
            builder.append(ConstData.CUSTOMREPEATSTR[repeatList.get(i)]);
            if (i != repeatList.size() - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBack() {
        ArrayList<Integer> lists = new ArrayList<>();
        Intent intent = new Intent();
        if (isCustomRepeatSet) {
            if (list != null && list.size() > 0) {
                lists = list;
            } else {
                lists.add(selectNum);
            }
        } else {
            lists.add(selectNum);
        }

        intent.putIntegerArrayListExtra("repeat", lists);
        intent.putExtra(ConstData.INTENT_SCHEDULE_REPEAT_IS_CUSTOM_REPEAT, isCustomRepeatSet);
        // 设置该SelectActivity的结果码，并设置结束之后退回的Activity
        setResult(ConstData.repeatCode, intent);
        // 结束SelectCityActivity。
        finish();
    }
}
