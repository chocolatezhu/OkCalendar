package com.ppzhu.calendar.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Administrator on 2017/1/18.
 */
public class AlmanacPojo implements Parcelable {

    private String date;
    private String yi;
    private String ji;
    private String fw;
    private String sc;
    private String nongli;
    private String taishen;
    private String chongsha;
    private String xingxiu;
    private String wuxing;
    private String pengzu;
    private String idx;

    private long selectTimeMill;
    private int count;

    public AlmanacPojo() {
    }

    public static final Creator<AlmanacPojo> CREATOR = new Creator<AlmanacPojo>() {
        @Override
        public AlmanacPojo createFromParcel(Parcel in) {
            return new AlmanacPojo(in);
        }

        @Override
        public AlmanacPojo[] newArray(int size) {
            return new AlmanacPojo[size];
        }
    };

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getSelectTimeMill() {
        return selectTimeMill;
    }

    public void setSelectTimeMill(long selectTimeMill) {
        this.selectTimeMill = selectTimeMill;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getYi() {
        return yi;
    }

    public void setYi(String yi) {
        this.yi = yi;
    }

    public String getJi() {
        return ji;
    }

    public void setJi(String ji) {
        this.ji = ji;
    }

    public String getFw() {
        return fw;
    }

    public void setFw(String fw) {
        this.fw = fw;
    }

    public String getSc() {
        return sc;
    }

    public void setSc(String sc) {
        this.sc = sc;
    }

    public String getNongli() {
        return nongli;
    }

    public void setNongli(String nongli) {
        this.nongli = nongli;
    }

    public String getTaishen() {
        return taishen;
    }

    public void setTaishen(String taishen) {
        this.taishen = taishen;
    }

    public String getChongsha() {
        return chongsha;
    }

    public void setChongsha(String chongsha) {
        this.chongsha = chongsha;
    }

    public String getXingxiu() {
        return xingxiu;
    }

    public void setXingxiu(String xingxiu) {
        this.xingxiu = xingxiu;
    }

    public String getWuxing() {
        return wuxing;
    }

    public void setWuxing(String wuxing) {
        this.wuxing = wuxing;
    }

    public String getPengzu() {
        return pengzu;
    }

    public void setPengzu(String pengzu) {
        this.pengzu = pengzu;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    protected AlmanacPojo(Parcel in) {
        date = in.readString();
        yi = in.readString();
        ji = in.readString();
        fw = in.readString();
        sc = in.readString();
        nongli = in.readString();
        taishen = in.readString();
        chongsha = in.readString();
        xingxiu = in.readString();
        wuxing = in.readString();
        pengzu = in.readString();
        idx = in.readString();
        selectTimeMill = in.readLong();
        count = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(yi);
        dest.writeString(ji);
        dest.writeString(fw);
        dest.writeString(sc);
        dest.writeString(nongli);
        dest.writeString(taishen);
        dest.writeString(chongsha);
        dest.writeString(xingxiu);
        dest.writeString(wuxing);
        dest.writeString(pengzu);
        dest.writeString(idx);
        dest.writeLong(selectTimeMill);
        dest.writeInt(count);
    }
}
