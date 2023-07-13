import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.System.out;

public class TestClass {

    @Test
    public void doTest() throws Exception {


        JSONArray array = new JSONArray();

        HashMap<Integer,Integer> gidMap = new HashMap<Integer,Integer>();

        if(gidMap.size() > 0){
            Set<Map.Entry<Integer, Integer>> entries = gidMap.entrySet();
            JSONObject totalJson = new JSONObject();
            totalJson.put("total",0);
            totalJson.put("use",0);
            totalJson.put("nouse",0);
            for (Map.Entry<Integer, Integer> entry : entries) {
                array.getJSONObject(entry.getValue()).put("totalinfo",totalJson);
            }
        }


        JSONObject jsonObject = new JSONObject();

        jsonObject.put("code",1);

        jsonObject.put("desc","未设置操作类型");

        long  lCurTime = System.currentTimeMillis() / 1000;

        jsonObject.put("svrtime",lCurTime);


        String requestData = "{\"taocan\":{\"rplid\":4767,\"payfree\":0,\"paylinkstate\":365,\"paylinktype\":\"days\",\"paytips\":\"杭州亚运会博物馆\",\"ctrlcmd\":\"&o=3&c=2\",\"proname\":\"上午场\",\"defstate\":\"defflex\",\"paytype\":0,\"minmoney\":0,\"outpaytype\":0,\"outotime\":0,\"outunitprice\":0,\"opentime\":32400,\"closetime\":43200,\"enabledays\":\"2\",\"peoplenumber\":500,\"maxmoney\":0,\"temptimesout\":0,\"otime\":31536000,\"unit\":\"365天\",\"otimestr\":\"365天\",\"infostr\":\"收费规则：0.00元 / 365天,不足365天按365天计算\",\"createtimestr\":\"2023-06-12 17:00:00\",\"opentimestr\":\"09:00\",\"closetimestr\":\"12:00\",\"stopintimestr\":\"13:00\",\"content\":\"上午场 09:00~12:00\",\"content_en\":\"09:00~12:00\"},\"date\":{\"id\":1,\"title\":\"06.13\",\"day\":2,\"cansel\":true},\"userlist\":[{\"cicd\":9493,\"createtime\":1686223213,\"userid\":\"511681199501090036\",\"realname\":\"周文强\",\"phone\":\"15208212060\",\"ciid\":231325,\"userid_type\":\"中国居民身份证\",\"sex\":\"男\",\"remark\":\"研学（Research）\",\"createtimestr\":\"2023-06-08 19:20:13\",\"sel\":true}],\"ciid\":231325,\"masterid\":150381,\"rsp\":\"person\"}";
        JSONObject requestJson = JSONObject.fromObject(requestData);

        int rplid = requestJson.getInt("rplid");
        int ciid = requestJson.getInt("ciid");
        int gaid = requestJson.getInt("gaid");
        String rsp = requestJson.getString("rsp");
        String strPhone = requestJson.getString("phone");
        String strCurdate = requestJson.getString("curdate");
        JSONArray userArray = new JSONArray();
        JSONObject teaminfoJson = new JSONObject();

        if("team".equals(rsp) && !requestJson.containsKey("teaminfo")){

            jsonObject.put("desc","缺少团队信息");

            out.println(jsonObject.toString());

            return;

        } else if("order".equals(rsp)){

            userArray = requestJson.getJSONArray("userlist");

        }  else if("team".equals(rsp)){

            teaminfoJson = requestJson.getJSONObject("teaminfo");

            int peoplenumber = teaminfoJson.getInt("peoplenumber");
            String remark = teaminfoJson.getString("remark");
            String teamname = teaminfoJson.getString("teamname");

            for(int index = 0;index < peoplenumber;index++){
                JSONObject userJson = new JSONObject();
                userJson.put("phone","");
                userJson.put("realname","");
                userJson.put("userid","");
                userArray.add(userJson);
            }

        }

        if(userArray.size() == 0){
            jsonObject.put("desc","购买人信息为空");
            out.println(jsonObject.toString());
            return;
        }

        Context initContext = new InitialContext();
        DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/WIFIDBPool");
        Connection sqlConn = ds.getConnection();

        sqlConn.setAutoCommit(false);

        try {

            JSONObject paramJson = new JSONObject();
            paramJson.put("curdate",strCurdate);
            paramJson.put("rplid",rplid);
            paramJson.put("ciid",ciid);
            paramJson.put("gaid",gaid);
            paramJson.put("phone",strPhone);
            paramJson.put("userlist",userArray);
            paramJson.put("rsp",rsp);

            if("team".equals(rsp)){

                paramJson.put("teaminfo",teaminfoJson);

            }

            jsonObject = doHand(sqlConn,paramJson);

        } catch (Exception e) {

            sqlConn.rollback();

            jsonObject.put("desc",e.toString());

        }

        sqlConn.close();
        out.println(jsonObject.toString());
    }

    public String getContent(InputStream is, String charset) {
        String pageString = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuffer sb = null;
        try {
            isr = new InputStreamReader(is, charset);
            br = new BufferedReader(isr);
            sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            pageString = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {

                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            sb = null;
        }
        return pageString;
    }

    public boolean isNotEmptyBatch(String... strs) {
        for (String str : strs) {
            if (str == null || "".equals(str)) {
                return false;
            }
        }
        return true;
    }

    public JSONObject doHand(Connection sqlConn, JSONObject paramJson) throws Exception{

        JSONObject resultJson = new JSONObject();
        long lCurtime = System.currentTimeMillis() / 1000;
        resultJson.put("code",1);
        resultJson.put("desc","未做任何操作");
        resultJson.put("svrtime",lCurtime);

        JSONArray userlistArray =  paramJson.getJSONArray("userlist");

        JSONObject taocanJson = doGetTaoCanInfoJson(sqlConn,paramJson.getInt("gaid"),paramJson.getInt("rplid"));

        if(!taocanJson.containsKey("rplid")){
            resultJson.put("desc","错误的套餐信息");
            return resultJson;
        }

        taocanJson.put("payfree",taocanJson.getInt("payfree") * userlistArray.size());

        JSONObject raceInfoJson = doGetRaceInfoJson(sqlConn,paramJson.getInt("rplid"),paramJson.getString("curdate"));

        if(!raceInfoJson.containsKey("raceid")){
            raceInfoJson = doAddRaceInfoJson(sqlConn,taocanJson,paramJson.getInt("rplid"),paramJson.getString("curdate"));
        }

        if(!raceInfoJson.containsKey("raceid")){
            resultJson.put("desc","订单创建失败");
            return resultJson;
        }

        if(raceInfoJson.getInt("peoplenumber") >= taocanJson.getInt("peoplenumber")){
            resultJson.put("desc","当前时间段预约已满，请选择其他时间段");
            return resultJson;
        }

        if(raceInfoJson.getInt("peoplenumber") + userlistArray.size() > taocanJson.getInt("peoplenumber")){
            resultJson.put("desc","当前时间段仅剩余" + (taocanJson.getInt("peoplenumber") - raceInfoJson.getInt("peoplenumber") )+ "张门票，请选择其他时间段");
            return resultJson;
        }

        JSONObject cardInfo = doGetUserInfoJson(sqlConn,paramJson.getInt("ciid"));

        if(!cardInfo.containsKey("ciid")){
            resultJson.put("desc","错误的用户信息");
            return resultJson;
        }

        paramJson.put("openid",cardInfo.getString("openid"));

        if("team".equals(paramJson.getString("rsp"))){
            int tid = doCreateTeamInfoJson(sqlConn,paramJson.getJSONObject("teaminfo"),cardInfo);
            if (tid == 0){
                resultJson.put("desc","团队信息创建失败");
                return resultJson;
            }
            paramJson.put("tid",tid);
        }

        JSONObject createOrderJson = doCreateOrderInfoJson(sqlConn,paramJson,taocanJson,cardInfo);

        if(!createOrderJson.containsKey("eid")){
            resultJson.put("desc","创建订单失败");
            return resultJson;
        }

        createOrderJson.put("starttime",raceInfoJson.getLong("starttime"));
        createOrderJson.put("endtime",raceInfoJson.getLong("endtime"));

        String addRaceUserResult = doAddRaceUserInfoJson(sqlConn,paramJson,raceInfoJson,createOrderJson,cardInfo);

        if(addRaceUserResult.length() > 0){
            resultJson.put("desc", addRaceUserResult);
            return resultJson;
        }

        //提交已创建的订单
        sqlConn.commit();

        boolean payResult = false;


        if(cardInfo.getLong("endtime") > lCurtime){
            //使用会员卡付费
            payResult = doPayOrderBYEndTime(createOrderJson,cardInfo,sqlConn);
        } else if(cardInfo.getInt("canusetimes") >=  userlistArray.size()){
            //使用次卡付费
            payResult = doPayOrderBYCardTimes(createOrderJson,cardInfo,sqlConn);
        } else if(cardInfo.getInt("money") >= taocanJson.getInt("payfree")){
            //使用余额支付
            payResult = doPayOrderBYMoney(createOrderJson,cardInfo,sqlConn);
        }

        if(payResult){

            resultJson.put("code",0);
            resultJson.put("desc","预约成功");
            resultJson.put("eid",createOrderJson.getInt("eid"));
            resultJson.put("transactionid",createOrderJson.getInt("transactionid"));

            sqlConn.commit();


        } else {

            resultJson.put("desc","支付失败，余额不足");

        }

        return resultJson;
    }

    private int doCreateTeamInfoJson(Connection sqlConn, JSONObject teaminfo,JSONObject cardInfoJson) throws Exception{
        int tid = 0;

        String teamName = teaminfo.getString("teamname");
        String phone = teaminfo.getString("phone");
        String realname = teaminfo.getString("realname");
        String remark = teaminfo.getString("remark");
        String title = teaminfo.getString("title");
        int rplid = teaminfo.getInt("rplid");
        int peoplenumber = teaminfo.getInt("peoplenumber");
        long lCurtime = System.currentTimeMillis() / 1000;

        PreparedStatement ps = sqlConn.prepareStatement("insert into ot_card_team_info (createtime,showname,title,rplid,ciid,concat_name,peoplenumber,phone,remark) values (?,?,?,?,?,?,?,?,?)");
        ps.setLong(1,lCurtime);
        ps.setString(2,teamName);
        ps.setString(3,title);
        ps.setInt(4,rplid);
        ps.setInt(5,cardInfoJson.getInt("ciid"));
        ps.setString(6,realname);
        ps.setInt(7,peoplenumber);
        ps.setString(8,phone);
        ps.setString(9,remark);
        ps.executeUpdate();


        ps = sqlConn.prepareStatement("select tid from ot_card_team_info where ciid = ? and createtime = ?");
        ps.setInt(1,cardInfoJson.getInt("ciid"));
        ps.setLong(2,lCurtime);
        ResultSet resultSet = ps.executeQuery();

        while (resultSet.next()){
            tid = resultSet.getInt(1);
        }

        return tid;
    }

    private String doAddRaceUserInfoJson(Connection sqlConn, JSONObject paramJson, JSONObject raceInfoJson,JSONObject orderJson,JSONObject cardInfo) throws Exception {

        long lCurTime = System.currentTimeMillis() / 1000;


        PreparedStatement ps = sqlConn.prepareStatement("select count(*) from ot_race_user_info where  raceid = ? and ciid = ?");
        ps.setInt(1,raceInfoJson.getInt("raceid"));
        ps.setInt(2,cardInfo.getInt("ciid"));
        ResultSet resultSet = ps.executeQuery();

        int count = 0;

        while (resultSet.next()){
            count = resultSet.getInt(1);
        }

        //单时间段 人数限制

        if(count >= 10){
            return "当前账号该时间段已预约超10人，请预约其他时间段";
        }

        int tid = paramJson.containsKey("tid")?paramJson.getInt("tid"):0;
        String rsp = paramJson.getString("rsp");

        JSONArray userlistArray = paramJson.getJSONArray("userlist");

        if("team".equals(rsp)){

            PreparedStatement psBatch = sqlConn.prepareStatement("insert into ot_race_user_info (createtime,phone,name,company,job,userid,sex,email,openid,raceid,imglist,state,ciid,eid,tid)" +
                    " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            for(int index = 0;index < userlistArray.size();index++){

                JSONObject curUserJson =  userlistArray.getJSONObject(index);

                psBatch.setLong(1, lCurTime);
                psBatch.setString(2,curUserJson.getString("phone"));
                psBatch.setString(3, curUserJson.getString("realname"));
                psBatch.setString(4,  curUserJson.getString("realname"));
                psBatch.setString(5, "用户");
                psBatch.setString(6, curUserJson.getString("userid"));
                psBatch.setInt(7, 0);
                psBatch.setString(8, "");
                psBatch.setString(9, cardInfo.getString("openid"));
                psBatch.setInt(10,raceInfoJson.getInt("raceid"));
                psBatch.setString(11, "");
                psBatch.setInt(12, 2);
                psBatch.setInt(13, cardInfo.getInt("ciid"));
                psBatch.setInt(14, orderJson.getInt("eid"));
                psBatch.setInt(15,tid);
                psBatch.addBatch();
            }

            psBatch.executeBatch();

            ps = sqlConn.prepareStatement("update ot_race_info set peoplenumber = peoplenumber + ? where raceid = ?");
            ps.setInt(1,userlistArray.size());
            ps.setInt(2,raceInfoJson.getInt("raceid"));
            ps.executeUpdate();

        } else {
            StringBuffer cicdBuffer = new StringBuffer();
            for(int index = 0;index < userlistArray.size();index++){
                cicdBuffer.append("," + userlistArray.getJSONObject(index).getInt("cicd"));
            }

            if(cicdBuffer.length() == 0){
                return "参观者信息不足1人";
            }

            cicdBuffer.deleteCharAt(0);


            PreparedStatement psBatch = sqlConn.prepareStatement("insert into ot_race_user_info (createtime,phone,name,company,job,userid,sex,email,openid,raceid,imglist,state,ciid,eid,tid)" +
                    " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            PreparedStatement psQuery = sqlConn.prepareStatement("select userid,realname,phone,sex,userid_type from ot_card_info_concat where  cicd in (" + cicdBuffer.toString() + ")");
            ResultSet resultSet1 = psQuery.executeQuery();

            StringBuffer useridBuffer = new StringBuffer();

            while (resultSet1.next()){

                String userid = resultSet1.getString(1);
                String realname = resultSet1.getString(2);
                String phone = resultSet1.getString(3);
                String sex = resultSet1.getString(4);
                String userid_type = resultSet1.getString(5);

                psBatch.setLong(1, lCurTime);
                psBatch.setString(2, phone);
                psBatch.setString(3,   realname);
                psBatch.setString(4,  realname);
                psBatch.setString(5, "用户");
                psBatch.setString(6, userid);
                psBatch.setInt(7, 0);
                psBatch.setString(8, "");
                psBatch.setString(9, cardInfo.getString("openid"));
                psBatch.setInt(10,raceInfoJson.getInt("raceid"));
                psBatch.setString(11, "");
                psBatch.setInt(12, 2);
                psBatch.setInt(13, cardInfo.getInt("ciid"));
                psBatch.setInt(14, orderJson.getInt("eid"));
                psBatch.setInt(15,tid);
                psBatch.addBatch();

                useridBuffer.append(",'" + userid + "'");

            }


            if(useridBuffer.length() == 0){
                return "参观者信息不足1人";
            }

            useridBuffer.deleteCharAt(0);

            ps = sqlConn.prepareStatement("select rausid,name from ot_race_user_info where userid in (" + useridBuffer.toString() + ") and raceid = ?");
            ps.setInt(1,raceInfoJson.getInt("raceid"));
            resultSet = ps.executeQuery();
            int rausid = 0;

            StringBuffer nameBuffer = new StringBuffer();

            while (resultSet.next()){
                rausid = resultSet.getInt(1);
                nameBuffer.append("、" + resultSet.getString(2));
            }

            if(rausid > 0){
                nameBuffer.deleteCharAt(0);
                return "用户 " + nameBuffer.toString() + " 已预约过当前时间段，请勿重复预约";
            }

            psBatch.executeBatch();

            ps = sqlConn.prepareStatement("update ot_race_info set peoplenumber = peoplenumber + ? where raceid = ?");
            ps.setInt(1,userlistArray.size());
            ps.setInt(2,raceInfoJson.getInt("raceid"));
            ps.executeUpdate();
        }



        return "";
    }

    private boolean doPayOrderBYMoney(JSONObject orderJson, JSONObject cardInfo, Connection sqlConn) throws Exception {

        long lCurTime = System.currentTimeMillis() / 1000;
        String strTransactionId =  System.currentTimeMillis() + "" + orderJson.getInt("eid");

        if(orderJson.getInt("payaccount") > cardInfo.getInt("money")){
            return false;
        }

        long starttime = lCurTime;
        long endtime = lCurTime + orderJson.getInt("otime");

        if(orderJson.containsKey("starttime")){
            starttime = orderJson.getLong("starttime");
        }

        if(orderJson.containsKey("endtime")){
            endtime = orderJson.getLong("endtime");
        }

        PreparedStatement ps = sqlConn.prepareStatement("update ot_card_info set money = money - ? where ciid = ?");
        ps.setInt(1, orderJson.getInt("payaccount"));
        ps.setInt(2, cardInfo.getInt("ciid"));
        ps.executeUpdate();

        ps = sqlConn.prepareStatement("insert into wx_pay_table(username, paytype, transactionid, outtrade, outrefund, createtime, refundtime, paystatus, amount, paymac, apply, rplid, otime,uid,isdivide,shopid) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?,?,1,?)");


        ps.setString(1, cardInfo.getString("openid"));
        ps.setString(2, "c-wxshop");
        ps.setString(3, strTransactionId);
        ps.setString(4, strTransactionId);
        ps.setString(5, "");
        ps.setLong(6, lCurTime);
        ps.setLong(7, 0);
        ps.setInt(8, 1);
        ps.setInt(9, orderJson.getInt("payaccount"));
        ps.setString(10,orderJson.getString("devmac"));
        ps.setInt(11, orderJson.getInt("rplid"));
        ps.setInt(12, orderJson.getInt("otime"));
        ps.setInt(13, orderJson.getInt("uid"));
        ps.setInt(14, orderJson.getInt("gaid"));
        ps.executeUpdate();


        ps = sqlConn.prepareStatement("update wx_session_table set Starttime=?,Endtime = ?,Paytime = ?,mflag = 1,Curstate = 1,Statedesc=?,transactionid = ? where eid = ?");
        ps.setLong(1, starttime);
        ps.setLong(2, endtime);
        ps.setLong(3, lCurTime);
        ps.setString(4, "订单进行中");
        ps.setString(5, strTransactionId);
        ps.setLong(6, orderJson.getInt("eid"));


        ps.executeUpdate();

        orderJson.put("transactionid",strTransactionId);


        return true;
    }

    private boolean doPayOrderBYCardTimes(JSONObject orderJson, JSONObject cardInfo, Connection sqlConn) throws Exception {
        long lCurTime = System.currentTimeMillis() / 1000;
        String strTransactionId =  System.currentTimeMillis() + "" + orderJson.getInt("eid");
        int paytimes = 1;

        long starttime = lCurTime;
        long endtime = lCurTime + orderJson.getInt("otime");

        if(orderJson.containsKey("starttime")){
            starttime = orderJson.getLong("starttime");
        }

        if(orderJson.containsKey("endtime")){
            endtime = orderJson.getLong("endtime");
        }


        PreparedStatement ps = sqlConn.prepareStatement("select ctid,ciid,createtime,canusetimes,endtime from ot_card_times where ciid = ? and endtime > ? and canusetimes >= 1");
        ps.setInt(1,cardInfo.getInt("ciid"));
        ps.setLong(2,lCurTime);
        ResultSet resultSet = ps.executeQuery();

        int ctid = 0;

        while (resultSet.next()){
            ctid = resultSet.getInt(1);
        }

        if(ctid == 0){
            return false;
        }

        ps = sqlConn.prepareStatement("update ot_card_times set canusetimes = canusetimes - ? where ctid = ?");
        ps.setInt(1, paytimes);
        ps.setInt(2, ctid);
        ps.executeUpdate();


        ps = sqlConn.prepareStatement("insert into wx_pay_table(username, paytype, transactionid, outtrade, outrefund, createtime, refundtime, paystatus, amount, paymac, apply, rplid, otime,uid,isdivide,pleaccount,shopid) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?,?,1,?,?)");


        ps.setString(1, cardInfo.getString("openid"));
        ps.setString(2, "t-wxshop");
        ps.setString(3, strTransactionId);
        ps.setString(4, strTransactionId);
        ps.setString(5, "");
        ps.setLong(6, lCurTime);
        ps.setLong(7, 0);
        ps.setInt(8, 1);
        ps.setInt(9, 0);
        ps.setString(10,orderJson.getString("devmac"));
        ps.setInt(11, orderJson.getInt("rplid"));
        ps.setInt(12, orderJson.getInt("otime"));
        ps.setInt(13, orderJson.getInt("uid"));
        ps.setInt(14,paytimes);
        ps.setInt(15, orderJson.getInt("gaid"));
        ps.executeUpdate();


        ps = sqlConn.prepareStatement("update wx_session_table set Starttime=?,endtime = ?,Paytime = ?,mflag = 1,Curstate = 1,Statedesc=?,transactionid = ?,payaccount=0 where eid = ?");
        ps.setLong(1, starttime);
        ps.setLong(2,  endtime);
        ps.setLong(3, lCurTime);

        ps.setString(4, "订单进行中");
        ps.setString(5, strTransactionId);
        ps.setLong(6, orderJson.getInt("eid"));
        ps.executeUpdate();

        orderJson.put("transactionid",strTransactionId);

        return true;
    }

    private boolean doPayOrderBYEndTime(JSONObject orderJson, JSONObject cardInfo, Connection sqlConn) throws Exception {

        long lCurTime = System.currentTimeMillis() / 1000;

        String strTransactionId =  System.currentTimeMillis() + "" + orderJson.getInt("eid");

        long starttime = lCurTime;
        long endtime = lCurTime + orderJson.getInt("otime");

        if(orderJson.containsKey("starttime")){
            starttime = orderJson.getLong("starttime");
        }

        if(orderJson.containsKey("endtime")){
            endtime = orderJson.getLong("endtime");
        }


        PreparedStatement ps = sqlConn.prepareStatement("insert into wx_pay_table(username, paytype, transactionid, outtrade, outrefund, createtime, refundtime, paystatus, amount, paymac, apply, rplid, otime,uid,isdivide,shopid) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?,?,1,?)");

        ps.setString(1, cardInfo.getString("openid"));
        ps.setString(2, "v-wxshop");
        ps.setString(3, strTransactionId);
        ps.setString(4, strTransactionId);
        ps.setString(5, "");
        ps.setLong(6, lCurTime);
        ps.setLong(7, 0);
        ps.setInt(8, 1);
        ps.setInt(9, 0);
        ps.setString(10,orderJson.getString("devmac"));
        ps.setInt(11, orderJson.getInt("rplid"));
        ps.setInt(12, orderJson.getInt("otime"));
        ps.setInt(13, orderJson.getInt("uid"));
        ps.setInt(14, orderJson.getInt("gaid"));
        ps.executeUpdate();


        ps = sqlConn.prepareStatement("update wx_session_table set Paytime = ?,mflag = 1,Curstate = 1,Statedesc=?,transactionid = ?,payaccount=0,starttime=?,endtime=? where eid = ?");
        ps.setLong(1, lCurTime);
        ps.setString(2, "订单进行中");
        ps.setString(3, strTransactionId);
        ps.setLong(4, starttime);
        ps.setLong(5, endtime);
        ps.setInt(6, orderJson.getInt("eid"));

        ps.executeUpdate();

        orderJson.put("transactionid",strTransactionId);

        return true;
    }

    public JSONObject doGetUserInfoJson(Connection sqlConn, int ciid) throws Exception{

        JSONObject resultJson = new JSONObject();

        long lCurTime = System.currentTimeMillis() / 1000;

        PreparedStatement ps = sqlConn.prepareStatement("select masteruid,ciid,openid,showname,money,integral,endtime from ot_card_info where ciid = ? ");
        ps.setInt(1,ciid);

        ResultSet resultSet = ps.executeQuery();

        while (resultSet.next()){
            resultJson.put("masteruid",resultSet.getInt(1));
            resultJson.put("ciid",resultSet.getInt(2));
            resultJson.put("openid",resultSet.getString(3));
            resultJson.put("showname",resultSet.getString(4));
            resultJson.put("money",resultSet.getInt(5));
            resultJson.put("integral",resultSet.getInt(6));
            resultJson.put("endtime",resultSet.getLong(7));
        }


        ps = sqlConn.prepareStatement("select sum(canusetimes) from ot_card_times where ciid = ? and endtime > ?");
        ps.setInt(1,ciid);
        ps.setLong(2,lCurTime);
        resultSet = ps.executeQuery();

        while (resultSet.next()){
            resultJson.put("canusetimes",resultSet.getInt(1));
        }


        return resultJson;
    }

    public JSONObject doCreateOrderInfoJson(Connection sqlConn, JSONObject paramJson, JSONObject taocanJson,JSONObject userJson) throws Exception{
        JSONObject resultJson = new JSONObject();
        long lCurtime = System.currentTimeMillis() / 1000;

        JSONArray userlistArray = paramJson.getJSONArray("userlist");

        String strInsert = "insert into wx_session_table " +
                " (Username, uid, createtime, Curstate, Paytype, payaccount, Devmac,objmac, Statedesc, mflag, objtype, rplid, rsp,otime,devshowname,outpaytype,outunitprice,outotime,peoplenumber,transactionid,ciid,unitPrice,maxmoney,starttime,endtime,remark,shopid)" +
                " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = sqlConn.prepareStatement(strInsert);

        int otime = 0;
        int unit = 1;
        String paylinktype = taocanJson.getString("paylinktype");
        int paylinkstate =taocanJson.getInt("paylinkstate");

        if("seconds".equalsIgnoreCase(paylinktype)){
            unit = 1;
        } else if ("minutes".equalsIgnoreCase(paylinktype)){
            unit = 60;
        } else if ("hours".equalsIgnoreCase(paylinktype)){
            unit = 3600;
        } else if ("days".equalsIgnoreCase(paylinktype)){
            unit = 86400;
        } else if ("times".equalsIgnoreCase(paylinktype)){
            unit = 1;
        }

        otime = paylinkstate * unit;

        String DevShowname = taocanJson.getString("paytips") + "-" + taocanJson.getString("proname");

        String transactionid = userJson.getInt("masteruid") + "" + System.currentTimeMillis();

        //先收费
        ps.setString(1, paramJson.getString("openid"));
        ps.setInt(2,  userJson.getInt("masteruid"));
        ps.setLong(3,lCurtime);
        ps.setInt(4, 0);
        ps.setInt(5, 0);
        ps.setInt(6, 0);
        ps.setString(7,  taocanJson.getString("devname"));
        ps.setString(8, taocanJson.getString("devname"));
        ps.setString(9, "等待用户支付中");
        ps.setLong(10, 0);
        ps.setInt(11,  0);
        ps.setInt(12,taocanJson.getInt("rplid"));
        ps.setString(13, paramJson.containsKey("rsp")?paramJson.getString("rsp"):"person");
        ps.setInt(14, otime);
        ps.setString(15, DevShowname);
        ps.setInt(16, 0);
        ps.setInt(17, 0);
        ps.setInt(18,0);
        ps.setInt(19, 10000 * userlistArray.size());
        ps.setString(20, transactionid);
        ps.setInt(21, userJson.getInt("ciid"));
        ps.setInt(22, taocanJson.getInt("payfree"));
        ps.setInt(23, taocanJson.getInt("maxmoney"));
        ps.setInt(24,0);
        ps.setInt(25,0);
        ps.setString(26,paramJson.toString());
        ps.setInt(27,paramJson.getInt("gaid"));
        int addResult = ps.executeUpdate();

        if(addResult > 0){

            ps = sqlConn.prepareStatement("select eid,transactionid,payaccount,otime,devmac,rplid,uid from wx_session_table where devmac = ? and  transactionid = ?");
            ps.setString(1, taocanJson.getString("devname"));
            ps.setString(2, transactionid);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()){
                resultJson.put("eid",resultSet.getInt(1));
                resultJson.put("transactionid",resultSet.getString(2));
                resultJson.put("payfree",resultSet.getInt(3));
                resultJson.put("payaccount",resultSet.getInt(3));
                resultJson.put("otime",resultSet.getInt(4));
                resultJson.put("devmac",resultSet.getString(5));
                resultJson.put("rplid",resultSet.getInt(6));
                resultJson.put("uid",resultSet.getInt(7));
            }
        }

        resultJson.put("gaid",paramJson.getInt("gaid"));

        return resultJson;
    }

    public JSONObject doAddRaceInfoJson(Connection sqlConn, JSONObject taocanJson, int rplid, String curdate) throws Exception{
        JSONObject resultJson = new JSONObject();
        long lCurtime = System.currentTimeMillis() / 1000;

        java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(curdate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);

        String devmac = "rplid_" + rplid;
        long curdateStarttime = calendar.getTimeInMillis() / 1000;

        PreparedStatement ps = sqlConn.prepareStatement("insert into ot_race_info (createtime,title,introduce,agreement,hint,peoplenumber,devmac,masteruid,racetime,starttime,endtime,maxnumber)" +
                " values (?,?,?,?,?,?,?,?,?,?,?,?)");
        ps.setLong(1,lCurtime);
        ps.setString(2,taocanJson.getString("gatename"));
        ps.setString(3,taocanJson.getString("paytips") + "-" + taocanJson.getString("proname") + "-" + curdate);
        ps.setString(4,"");
        ps.setString(5,curdate);
        ps.setInt(6,0);
        ps.setString(7,devmac);
        ps.setInt(8,taocanJson.getInt("uid"));
        ps.setLong(9,0);
        ps.setLong(10,curdateStarttime + (taocanJson.getLong("runopentime") > 0?taocanJson.getLong("runopentime"):taocanJson.getLong("opentime")));
        ps.setLong(11,curdateStarttime + (taocanJson.getLong("runclosetime") > 0?taocanJson.getLong("runclosetime"):taocanJson.getLong("closetime")));
        ps.setInt(12,taocanJson.getInt("peoplenumber"));

        int addResult = ps.executeUpdate();

        resultJson.put("addResult",addResult);

        if(addResult > 0){

            ps = sqlConn.prepareStatement("select raceid,starttime,endtime from ot_race_info where devmac = ? and masteruid = ? and hint = ? and createtime = ?");
            ps.setString(1,devmac);
            ps.setInt(2,taocanJson.getInt("uid"));
            ps.setString(3,curdate);
            ps.setLong(4,lCurtime);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()){
                resultJson.put("raceid",resultSet.getInt(1));
                resultJson.put("starttime",resultSet.getLong(2));
                resultJson.put("endtime",resultSet.getLong(3));
                resultJson.put("peoplenumber",0);
            }
        }

        return resultJson;
    }

    public JSONObject doGetRaceInfoJson(Connection sqlConn, int rplid, String curdate) throws Exception{

        JSONObject resultJson = new JSONObject();

        PreparedStatement ps = sqlConn.prepareStatement("select raceid,peoplenumber,maxnumber,starttime,endtime,hint from ot_race_info where hint = ? and devmac = ?");
        ps.setString(1,curdate);
        ps.setString(2,"rplid_" + rplid);

        ResultSet resultSet = ps.executeQuery();

        while(resultSet.next()){
            resultJson.put("raceid", resultSet.getInt(1));
            resultJson.put("peoplenumber", resultSet.getInt(2));
            resultJson.put("maxnumber",resultSet.getInt(3));
            resultJson.put("starttime",resultSet.getLong(4));
            resultJson.put("endtime",resultSet.getLong(5));
            resultJson.put("hint",resultSet.getString(6));
        }

        return resultJson;
    }

    public JSONObject doGetTaoCanInfoJson(Connection sqlConn,int gaid,int rplid) throws Exception{

        JSONObject resultJson = new JSONObject();

        PreparedStatement ps = sqlConn.prepareStatement("select peoplenumber,paytips,proname,paylinktype,paylinkstate,runopentime,runclosetime,opentime,closetime,devname,payfree,maxmoney from ot_user_dev_paylink where devname = ? and rplid = ?");
        ps.setString(1,"gate_" + gaid);
        ps.setInt(2,rplid);

        ResultSet resultSet = ps.executeQuery();

        while(resultSet.next()){
            resultJson.put("peoplenumber", resultSet.getInt(1));
            resultJson.put("paytips", resultSet.getString(2));
            resultJson.put("proname",resultSet.getString(3));
            resultJson.put("paylinktype",resultSet.getString(4));
            resultJson.put("paylinkstate",resultSet.getInt(5));
            resultJson.put("runopentime",resultSet.getLong(6));
            resultJson.put("runclosetime",resultSet.getLong(7));
            resultJson.put("opentime",resultSet.getLong(8));
            resultJson.put("closetime",resultSet.getLong(9));
            resultJson.put("devname",resultSet.getString(10));
            resultJson.put("payfree",resultSet.getInt(11));
            resultJson.put("maxmoney",resultSet.getInt(12));
            resultJson.put("gaid",gaid);
            resultJson.put("rplid",rplid);
        }

        ps = sqlConn.prepareStatement("select gatename,uid from ot_gate_info where gaid = ?");
        ps.setInt(1,gaid);
        resultSet = ps.executeQuery();

        while(resultSet.next()){

            resultJson.put("gatename",resultSet.getString(1));
            resultJson.put("uid",resultSet.getInt(2));

        }

        return resultJson;
    }
}
