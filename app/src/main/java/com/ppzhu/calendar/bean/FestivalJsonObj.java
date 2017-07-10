package com.ppzhu.calendar.bean;


import java.util.List;

public class FestivalJsonObj {
    //	{"datas":[{"createUser":"xg","day":20150101,"festival":"元旦节","id":1,"month":201501,"year":2015},
//		{"createUser":"xg","day":20151001,"festival":"国庆节","id":2,"month":201510,"year":2015},
//		{"createUser":"xg","day":20151002,"festival":"国庆节","id":3,"month":201510,"year":2015},
//		{"createUser":"xg","day":20151003,"festival":"国庆节","id":4,"month":201510,"year":2015},
//		{"createUser":"xg","day":20151004,"festival":"国庆节","id":5,"month":201510,"year":2015}],
//		"message":"success","status":101002}
    public String message;
    public int status;
    public List<Festival> datas;

    public List<Festival> getDatas() {
        return datas;
    }

    public void setDatas(List<Festival> datas) {
        this.datas = datas;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Festival> getFestival(){
        return datas;
    }

//    public List<Festival> getFestivalJsonObj() {
//        return HttpUtil.GsonToFestival(datas);
//    }

    @Override
    public String toString() {
        return " [ message  = " + message +
                "  data  =  " + datas +
                " status  =  " + status +
                "]";
    }
}
