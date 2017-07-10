package com.ppzhu.calendar.schedule.add;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ppzhu.calendar.R;


/**
 * Created by Administrator on 2016/8/8.
 */
public class CustomAdapter extends BaseAdapter {

    private Context mContext;

    private String[] mRepeatStr;
    private String mCustomRepeatStr;
    private int mSelectedIndex = -1;


    public CustomAdapter(Context context, String[] str) {
        this.mContext = context;
        this.mRepeatStr = str;
    }

    @Override
    public int getCount() {
        return mRepeatStr.length;
    }

    @Override
    public Object getItem(int i) {
        return mRepeatStr[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.repeat_item, null);
            viewHolder.title = (TextView) view.findViewById(R.id.tv_repeat_item);
            viewHolder.radio = (RadioButton) view.findViewById(R.id.rb_repeat_item);
            viewHolder.itemArrow = (ImageView) view.findViewById(R.id.iv_repeat_item);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (!TextUtils.isEmpty(mCustomRepeatStr) && position == mRepeatStr.length - 1){
            viewHolder.radio.setText(mCustomRepeatStr);
            viewHolder.radio.setVisibility(View.VISIBLE);
            viewHolder.radio.setChecked(true);
        } else {
            viewHolder.radio.setText("");
            if (mSelectedIndex == position) {
                viewHolder.radio.setVisibility(View.VISIBLE);
                viewHolder.radio.setChecked(true);
            } else {
                viewHolder.radio.setVisibility(View.GONE);
                viewHolder.radio.setChecked(false);
            }
        }
        viewHolder.title.setText(mRepeatStr[position]);

        return view;
    }

    public void setSelectedIndex(int index) {
        mSelectedIndex = index;
    }

    public void setCustomRepeatStr(String customRepeatStr) {
        mCustomRepeatStr = customRepeatStr;
    }

    public final class ViewHolder {
        public RadioButton radio;
        public TextView title;
        public ImageView itemArrow;
    }
}
