package com.ppzhu.calendar.schedule.add.customrepeat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


import com.ppzhu.calendar.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/8.
 */
public class CustomRepeatAdapter extends BaseAdapter{

    private Context context;

    private String[] str;

    // 用来控制CheckBox的选中状况
    private Map<Integer,Boolean> isSelected  = new HashMap<>();


    public CustomRepeatAdapter(Context context, String[] str){
           this.context = context;
           this.str = str;
           initDate();
    }

    // 初始化isSelected的数据
    private void initDate(){
        for(int i=0; i<str.length;i++) {
            getIsSelected().put(i,false);
        }
    }

    @Override
    public int getCount() {
        return str.length;
    }

    @Override
    public Object getItem(int i) {
        return str[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null)
        {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.custom_item, null);
            viewHolder.title = (TextView)view.findViewById(R.id.tv_repeat_item);
            viewHolder.radio = (CheckBox) view.findViewById(R.id.rb_repeat_item);
            view.setTag(viewHolder);
        }else
        {
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.title.setText(str[position]);
        viewHolder.radio.setChecked(getIsSelected().get(position));
        return view;
    }

    public Map<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Map<Integer,Boolean> isSelected) {
        this.isSelected = isSelected;
    }

    public final class ViewHolder{
        public CheckBox radio;
        public TextView title;
    }
}
