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
import com.ppzhu.calendar.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomRepeatActivity extends FragmentActivity {
    private CustomRepeatAdapter mRepeatAdapter;

    private int num = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat);
        getWindow().setBackgroundDrawable(null);
        initView();
        getIntentInfo();
    }

    private void getIntentInfo() {
        Intent it = getIntent();
        List<Integer> selectList = it.getIntegerArrayListExtra(ConstData.INTENT_SCHEDULE_REPEAT_FREE_SET_ITEM);
        if (null == selectList || selectList.isEmpty()) {
            return;
        }

        for (int i = 0; i < selectList.size(); i++) {
            mRepeatAdapter.getIsSelected().put(selectList.get(i), true);
        }
//        mRepeatAdapter.notifyDataSetChanged();
    }

    private void initView() {
        ListView mListView = (ListView) findViewById(R.id.schedule_list);
        TextView mTvTitle = (TextView) findViewById(R.id.tv_title_bar);
        ImageView mIvTitle = (ImageView) findViewById(R.id.iv_title_bar);
        mTvTitle.setText(getResources().getString(R.string.schedule_repeat_custom_title));
        mRepeatAdapter = new CustomRepeatAdapter(this, ConstData.CUSTOMREPEATSTR);
        mListView.setAdapter(mRepeatAdapter);
        // mRepeatAdapter.setSelectedIndex(num);
        //mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                CustomRepeatAdapter.ViewHolder holder = (CustomRepeatAdapter.ViewHolder) arg1.getTag();
                // 改变CheckBox的状态
                holder.radio.setChecked(!holder.radio.isChecked());
                // 将CheckBox的选中状况记录下来
                mRepeatAdapter.getIsSelected().put(arg2, holder.radio.isChecked());
                mRepeatAdapter.notifyDataSetChanged();
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBack() {
        Map<Integer, Boolean> isSelected = mRepeatAdapter.getIsSelected();
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < isSelected.size(); i++) {
            if (isSelected.get(i)) {
                list.add(i);
            }
        }
        Intent intent = getIntent();
        intent.putIntegerArrayListExtra("repeat", list);
        //intent.putExtra("repeat", str);
        // 设置该SelectActivity的结果码，并设置结束之后退回的Activity
        setResult(RepeatActivity.customRepeatCode, intent);
        // 结束SelectCityActivity。
        finish();
    }

}
