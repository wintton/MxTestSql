package com.mx;

import com.mx.limit.LimitRequestUtil;
import com.mx.test.sql.Connection;
import com.mx.test.sql.PreparedStatement;
import com.mx.test.sql.ResultSet;
import net.sf.json.JSONObject;

import java.util.Calendar;

public class Main {

    public volatile static int runCount = 0;
    public volatile static int cancelCount = 0;

    public static void main(String[] args) {



//        Connection sqlConn = new Connection();
//
//        //配置查询
//        sqlConn.addQueryResult("select sum(endtime - starttime) from wx_session_table where devmac = '1F3E5589A21D' and  createtime > 1672502400 and curstate = 2","21353");
//
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.MONTH,0);
//        calendar.set(Calendar.DATE,1);
//        calendar.set(Calendar.HOUR_OF_DAY,0);
//        calendar.set(Calendar.MINUTE,0);
//        calendar.set(Calendar.SECOND,0);
//        long thisYearStartTime = calendar.getTimeInMillis() / 1000;
//
//        String strDevid = "1F3E5589A21D";
//
//        PreparedStatement ps = sqlConn.prepareStatement("select sum(endtime - starttime) from wx_session_table where devmac = ? and  createtime > ? and curstate = 2");
//        ps.setString(1,strDevid);
//        ps.setLong(2,thisYearStartTime);
//
//        ResultSet resultSet = ps.executeQuery();
//
//        int useTimeInt = 0;
//
//        while (resultSet.next()){
//            useTimeInt = resultSet.getInt(1);
//        }
//
//       String useTimeStr = (useTimeInt / 3600) + "时" + (useTimeInt % 3600 / 60) + "分钟" + (useTimeInt % 60) + "秒";
//        System.out.println(useTimeStr);
//
//        JSONObject paramJson = new JSONObject();
//        paramJson.put("masteruid","strMasterUid");
//        paramJson.put("id",10);
//        paramJson.put("black_user",1);

        LimitRequestUtil limitRequestUtil = new LimitRequestUtil(100,LimitRequestUtil.NO_WAIT);

        int workCount = 1000;
        runCount = 0;
        cancelCount = 0;

        for(int index = 0;index < workCount;index++){

            int finalIndex = index;

            new Thread(new Runnable() {
                int curIndex = finalIndex;
                @Override
                public void run() {
                    if(limitRequestUtil.addCount()){
                        runCount++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }
                        limitRequestUtil.reduceCount();
                    } else {
                        cancelCount++;
                    }
                    System.out.println("执行：" + runCount + " 取消：" + cancelCount);
                }
            }).start();
        }


    }





}