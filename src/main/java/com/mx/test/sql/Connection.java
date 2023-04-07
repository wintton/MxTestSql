package com.mx.test.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Connection {

    private HashMap<String,List<String>> queryDataResult = new HashMap<>();
    private HashMap<String,Integer> updateDataResult = new HashMap<>();

    public void addQueryResult(String sql,String result){
        if(queryDataResult.get(sql) == null){
            List<String> resultAdd = new ArrayList<>();
            resultAdd.add(result);
            queryDataResult.put(sql,resultAdd);
        } else {
            queryDataResult.get(sql).add(result);
        }
    }

    public void parseQueryResult(String sql,String result){
        String[] resultSplit = result.split("\n");
        List<String> resultAdd = new ArrayList<>();
        for (String split:resultSplit){
            String addResult = "";
            if(split.startsWith("|")){
                addResult = split.replaceFirst("\\|","");
            }
            resultAdd.add(addResult.replaceAll("\\|",","));
        }
        putQueryResult(sql,resultAdd);
    }

    public void putQueryResult(String sql,List<String> resultAdd){
        queryDataResult.put(sql,resultAdd);
    }

    public void putUpdateResult(String sql,int resultAdd){
        updateDataResult.put(sql,resultAdd);
    }

    public PreparedStatement prepareStatement(String sql){
        return new PreparedStatement(sql,this);
    }

    public Statement createStatement(){
        return new Statement(this);
    }

    public ResultSet doSqlQuery(String sql) {

        System.out.println("执行查询：" + sql);

        List<String> result = queryDataResult.get(sql);

        System.out.println("查询结果:");

        if(result == null){
            System.out.println("数据为空");
            return  new ResultSet(null);
        }

        result.forEach(item -> {
            System.out.println(item);
        });

        return new ResultSet(result);
    }

    public int doSqlUpdate(String sql) {
        System.out.println("执行：" + sql);
        Integer result = updateDataResult.get(sql);
        System.out.println("执行结果：" + result);
        return result;
    }


}
