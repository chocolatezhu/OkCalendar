package com.ppzhu.calendar.bean;

import android.net.Uri;

/**
 * @author zzl
 * 保存联系人信息
 * Created on 2016/12/7.
 */
public class ContactInfoPojo {
    //联系人id
    int contactId;
    //名字
    String name;
     //生日
    String birthday;
    //跳转联系人详情uri
    Uri lookupUri;

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Uri getLookupUri() {
        return lookupUri;
    }

    public void setLookupUri(Uri lookupUri) {
        this.lookupUri = lookupUri;
    }
}
