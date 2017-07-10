package com.ppzhu.calendar.decorator;

import android.content.Context;
import android.os.AsyncTask;

import org.joda.time.DateTime;

/**
 * @author zzl
 * 日程圆点控制类
 * Created on 2017/2/9.
 */
public class TaskHintController {
    private Context mContext;
    private int mSelectYear, mSelectMonth;
    private DateTime mStartDate;
    private TaskHintListener mTaskHintListener;
    private TaskHintCircleSearchByMonthAsyncTask taskHintCircleSearchByMonthAsyncTask;
    private TaskHintCircleSearchByWeekAsyncTask taskHintCircleSearchByWeekAsyncTask;

    public TaskHintController(Context context, TaskHintListener taskHintListener) {
        mContext = context;
        mTaskHintListener = taskHintListener;
    }

    public void startTaskHintCircleSearchByMonth(int selectYear, int selectMonth) {
        mSelectYear = selectYear;
        mSelectMonth = selectMonth;
        cancelTaskHintCircleSearchByMonthAsyncTask();
        taskHintCircleSearchByMonthAsyncTask = new TaskHintCircleSearchByMonthAsyncTask();
        taskHintCircleSearchByMonthAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void startTaskHintCircleSearchByWeek(DateTime startDate) {
        mStartDate = startDate;
        cancelTaskHintCircleSearchByWeekAsyncTask();
        taskHintCircleSearchByWeekAsyncTask = new TaskHintCircleSearchByWeekAsyncTask();
        taskHintCircleSearchByWeekAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class TaskHintCircleSearchByMonthAsyncTask extends AsyncTask<Void, Void, boolean[]> {
        @Override
        protected void onPostExecute(boolean[] booleans) {
            if (null != mTaskHintListener) {
                mTaskHintListener.onGetTaskHintCircle(booleans, TaskHintConst.TASK_HINT_MONTH);
            }
        }

        @Override
        protected boolean[] doInBackground(Void... params) {
            return getTaskHintCircleByMonth(mSelectYear, mSelectMonth);
        }
    }

    private boolean[] getTaskHintCircleByMonth(int selectYear, int selectMonth) {
        TaskHint taskHint = TaskHint.getInstance(mContext);
        return taskHint.geTaskHintByMonth(selectYear, selectMonth);
    }

    public class TaskHintCircleSearchByWeekAsyncTask extends AsyncTask<Void, Void, boolean[]> {
        @Override
        protected void onPostExecute(boolean[] booleans) {
            if (null != mTaskHintListener) {
                mTaskHintListener.onGetTaskHintCircle(booleans, TaskHintConst.TASK_HINT_WEEK);
            }
        }

        @Override
        protected boolean[] doInBackground(Void... params) {
            return getTaskHintCircleByWeek(mStartDate);
        }
    }

    private boolean[] getTaskHintCircleByWeek(DateTime startDate) {
        TaskHint taskHint = TaskHint.getInstance(mContext);
        return taskHint.getTaskHintByWeek(startDate);
    }

    private void cancelTaskHintCircleSearchByWeekAsyncTask() {
        if (null != taskHintCircleSearchByWeekAsyncTask) {
            taskHintCircleSearchByWeekAsyncTask.cancel(true);
            taskHintCircleSearchByWeekAsyncTask = null;
        }
    }

    private void cancelTaskHintCircleSearchByMonthAsyncTask() {
        if (null != taskHintCircleSearchByMonthAsyncTask) {
            taskHintCircleSearchByMonthAsyncTask.cancel(true);
            taskHintCircleSearchByMonthAsyncTask = null;
        }
    }

    public void close() {
        cancelTaskHintCircleSearchByMonthAsyncTask();
        cancelTaskHintCircleSearchByWeekAsyncTask();
    }
}
