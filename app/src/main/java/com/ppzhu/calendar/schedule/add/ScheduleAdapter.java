package com.ppzhu.calendar.schedule.add;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ppzhu.calendar.R;
import com.ppzhu.calendar.bean.Schedule;
import com.ppzhu.calendar.constants.ScheduleConst;

import java.util.Calendar;
import java.util.List;

/**
 * @author zzl
 * 日程列表适配器
 * Created on 2016/8/12.
 */
public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_NORMAL = 0x1000;
    private static final int TYPE_HEAD = 0x1001;
    private static final int TYPE_FOOT = 0x1002;

    private Context context;
    private RecyclerView mRecyclerView;
    private View mHeadView;
    private View mFootView;

    private List<Schedule> list;

    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private OnRecyclerViewHeadClickListener onRecyclerViewHeadClickListener;
    private OnRecyclerViewFootClickListener onRecyclerViewFootClickListener;

    public ScheduleAdapter(Context context, List<Schedule> list) {
        this.context = context;
        this.list = list;
    }

    public void refresh(List<Schedule> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEAD) {
            return new ScheduleViewHeadHolder(mHeadView);
        } else if (viewType == TYPE_FOOT) {
            return new ScheduleViewFooterHolder(mFootView);
        } else {
            return new ScheduleViewHolder(LayoutInflater.from(context).inflate(R.layout.schedule_list_item, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof  ScheduleViewHolder) {
            //head头部
            if (haveHeaderView()) {
                position--;
            }

            final int schedulePosition = position;
            ScheduleViewHolder viewHolder = (ScheduleViewHolder) holder;
            viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onRecyclerViewItemClickListener) {
                        onRecyclerViewItemClickListener.onItemClick(v, schedulePosition);
                    }
                }
            });

            if (haveHeaderView()) {
                if (1 == schedulePosition) {
                    viewHolder.llTitleView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.llTitleView.setVisibility(View.GONE);
                }
            } else {
                if (0 == schedulePosition) {
                    viewHolder.llTitleView.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.llTitleView.setVisibility(View.GONE);
                }
            }

            if (ScheduleConst.SCHEDULE_IS_ALL_DAY == list.get(schedulePosition).getAllDay()){
                viewHolder.tvAllDay.setVisibility(View.VISIBLE);
                viewHolder.tvAllDay.setText("全天");

                viewHolder.tvStart.setVisibility(View.GONE);
                viewHolder.tvEnd.setVisibility(View.GONE);
            } else {
                viewHolder.tvAllDay.setVisibility(View.GONE);
                Calendar caStart = formatDates(list.get(schedulePosition).getAlertTime());
                Calendar caEnd = formatDates(list.get(schedulePosition).getEndTimeMill());
                viewHolder.tvStart.setVisibility(View.VISIBLE);
                viewHolder.tvStart.setText(format(caStart.get(Calendar.HOUR_OF_DAY)) + ":" + format(caStart.get(Calendar.MINUTE)));
                viewHolder.tvEnd.setVisibility(View.VISIBLE);
                viewHolder.tvEnd.setText(format(caEnd.get(Calendar.HOUR_OF_DAY)) + ":" + format(caEnd.get(Calendar.MINUTE)));
            }

            if (TextUtils.isEmpty(list.get(schedulePosition).getLookUpUri())) {
                viewHolder.ivCakeIcon.setVisibility(View.GONE);
            } else {
                viewHolder.ivCakeIcon.setVisibility(View.VISIBLE);
            }

            viewHolder.tvTitle.setText(list.get(schedulePosition).getTitle());
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        int count = list.size();
        if (null != mHeadView) {
            count ++;
        }
        if (null != mFootView){
            count ++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEAD;
        } else if (isFooterView(position)) {
            return TYPE_FOOT;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        try {
            if (null == mRecyclerView) {
                mRecyclerView = recyclerView;
            }
            ifGridLayoutManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ScheduleViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootView;
        LinearLayout llTitleView;
        TextView tvStart, tvEnd, tvAllDay, tvTitle;
        ImageView ivCakeIcon;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            rootView = (LinearLayout) itemView.findViewById(R.id.item_root_view);
            llTitleView = (LinearLayout) itemView.findViewById(R.id.item_title_layout);
            tvStart = (TextView) itemView.findViewById(R.id.tv_start_item);
            tvEnd = (TextView) itemView.findViewById(R.id.tv_end_item);
            tvAllDay = (TextView) itemView.findViewById(R.id.tv_all_day);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title_item);
            ivCakeIcon = (ImageView) itemView.findViewById(R.id.iv_cake_icon);
        }
    }

    private class ScheduleViewHeadHolder extends RecyclerView.ViewHolder {
        public ScheduleViewHeadHolder(View itemView) {
            super(itemView);
        }
    }

    private class ScheduleViewFooterHolder extends RecyclerView.ViewHolder {
        public ScheduleViewFooterHolder(View itemView) {
            super(itemView);
        }
    }

    public void setHeadViewVisibility(int visibility) {
        if (null != mHeadView) {
            mHeadView.setVisibility(visibility);
        }
    }

    public void setFootViewVisibility(int visibility) {
        if (null != mFootView) {
            mFootView.setVisibility(visibility);
        }
    }

    public void addHeaderView(View headerView) {
        if (haveHeaderView()) {
            throw new IllegalStateException("hearview has already exists!");
        } else {
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(params);
            mHeadView = headerView;
            ifGridLayoutManager();
            notifyItemInserted(0);
        }
    }

    public void addFooterView(View footerView) {
        if (haveFooterView()) {
            throw new IllegalStateException("footerView has already exists!");
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            footerView.setLayoutParams(params);
            mFootView = footerView;
            ifGridLayoutManager();
            notifyItemInserted(getItemCount() - 1);
        }
    }

    private void ifGridLayoutManager() {
        if (mRecyclerView == null) return;
        final RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager.SpanSizeLookup originalSpanSizeLookup =
                    ((GridLayoutManager) layoutManager).getSpanSizeLookup();
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeaderView(position) || isFooterView(position)) ?
                            ((GridLayoutManager) layoutManager).getSpanCount() : 1;
                }
            });
        }
    }

    private boolean isHeaderView(int position) {
        return haveHeaderView() && position == 0;
    }

    private boolean isFooterView(int position) {
        return haveFooterView() && position == getItemCount() - 1;
    }

    private boolean haveHeaderView() {
        return mHeadView != null;
    }

    public boolean haveFooterView() {
        return mFootView != null;
    }

    private Calendar formatDates(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal;
    }

    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        onRecyclerViewItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnHeadClickListener(OnRecyclerViewHeadClickListener listener) {
        onRecyclerViewHeadClickListener = listener;
    }

    public interface OnRecyclerViewHeadClickListener {
        void onHeadClick(View view);
    }

    public void setOnFootClickListener(OnRecyclerViewFootClickListener listener) {
        onRecyclerViewFootClickListener = listener;
    }
    public interface OnRecyclerViewFootClickListener {
        void onFootClick(View view);
    }
}
