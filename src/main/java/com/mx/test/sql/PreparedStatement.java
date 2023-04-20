package com.mx.test.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class PreparedStatement {

    private Connection sqlConn;

    List<String> result = null;
    Map<Integer,String> datas = null;
    String curSql = "";

    public PreparedStatement(String sql, Connection sqlConn){
        curSql = sql;
        datas = new HashMap<>();
        result = new ArrayList<>();
        this.sqlConn = sqlConn;
    }

    public void setInt(int index,int value){
        datas.put(index,value + "");
    }

    public void setString(int index,String value){
        datas.put(index, "'" + value + "'");
    }

    public void setLong(int index,long value){
        datas.put(index,value + "");
    }

    public void setFloat(int index,float value){
        datas.put(index,value + "");
    }

    public void setDouble(int index,double value){
        datas.put(index,value + "");
    }

    public void addBatch(){
        result.add(getCurSetSql());
    }

    public void executeBatch(){
        System.out.println("开始批量执行：");
        result.forEach(item -> {
            this.sqlConn.doSqlUpdate(item);
        });
        System.out.println("结束批量执行：");
    }

    public int executeUpdate(){
        return this.sqlConn.doSqlUpdate(getCurSetSql());
    }

    public ResultSet executeQuery(){
        return doSqlQuery(getCurSetSql());
    }

    private String getCurSetSql(){
        String[] concatStr = curSql.split("\\?");

        int len = concatStr.length;

        StringBuffer resultBuffer = new StringBuffer();

        for (int i = 0;i < len ;i++){
            resultBuffer.append(concatStr[i]);
            resultBuffer.append(datas.getOrDefault(i + 1,"null"));
        }
        String result = resultBuffer.toString();
        if(result.endsWith("null")){
            return result.substring(0,result.length() - 4);
        }
        return  resultBuffer.toString();
    }

    public ResultSet doSqlQuery(String sql){
        return this.sqlConn.doSqlQuery(sql);
    }

    public void setObject(int index, Object o) {
        if(o instanceof Integer){
            setInt(index,(int) o);
        } else if(o instanceof Float){
            setFloat(index,(float) o);
        }  else if(o instanceof Double){
            setDouble(index,(double) o);
        } else if(o instanceof String){
            setString(index, o.toString());
        }
    }
}

