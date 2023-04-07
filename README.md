大家好哇，我又来了，还是我梦辛工作室的灵，最近在调试 老项目的时候，老出现数据异常，查了好半天都查不到，本来想把数据库同步到本地后来调试，又觉得麻烦，就想着要不自己写一个测试类，然后自定义一些sql语句的匹配，返回定义好的数据这样来调试代码，所以我就花了一些时间来写了这个MxTestSql类，有帮助的小伙伴帮忙点个赞，先看下实现效果：

```java
执行：update user set name = '1' from user
执行结果：1
执行查询：select name,phone from user
查询结果:
小明,110
执行查询：select name,phone from user2
查询结果:
 06d9706b92904dadb5022916edbdf667 , 183199  
 0ec005bb835c457a94ac232bc65bc2ca , 182911 

Process finished with exit code 0
```

```java
package com.mx;

import com.mx.test.sql.Connection;
import com.mx.test.sql.PreparedStatement;
import com.mx.test.sql.ResultSet;

public class Main {

    public static void main(String[] args) {

        Connection connection = new Connection();

        //配置查询
        connection.addQueryResult("select name,phone from user","小明,110");

        //支持分析直接从面板 拷贝出来的数据
        connection.parseQueryResult("select name,phone from user2",
                "| 06d9706b92904dadb5022916edbdf667 | 183199  \n" +
                        "| 0ec005bb835c457a94ac232bc65bc2ca | 182911 \n"
        );
        //配置更新结果
        connection.putUpdateResult("update user set name = '1' from user",1);


        PreparedStatement ps = connection.prepareStatement("update user set name = ? from user");

        ps.setString(1,"1");

        int result = ps.executeUpdate();

        ps = connection.prepareStatement("select name,phone from user");

        ResultSet resultSet = ps.executeQuery();

        while (resultSet.next()){

        }

        ps = connection.prepareStatement("select name,phone from user2");

        resultSet = ps.executeQuery();

        while (resultSet.next()){

        }
    }


}
```
是不是这样就很丝滑，在本地调试也简单了许多，不用导入各种数据，直接模拟线上情况，在本地跑就行了，替换也简单，导入的sql类更换下就可以了，下面是全部代码：
Connection：

```java
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

```
PreparedStatement：

```java
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

}


```
ResultSet：

```java
package com.mx.test.sql;

import java.util.ArrayList;
import java.util.List;

public class ResultSet {
    List<String> datas = null;
    int current = -1;

    public ResultSet(){
        this.datas = new ArrayList<>();
        current = -1;
    }

    public ResultSet(List<String> datas){
        this.datas = datas;
        current = -1;
    }

    public boolean doCheckNext(){
        if(datas == null || datas.size() == 0){
            return  false;
        }
        return  current < datas.size() - 1;
    }

    public boolean next(){
        if (doCheckNext()){
            current++;
            return true;
        }
        return false;
    }

    public String getString(int index){
        String value = getValue(index).trim();
        return value;
    }

    public int getInt(int index){
        String value = getValue(index).trim();
        if("null".equals(value)){
            return 0;
        }

        return value != null?Integer.parseInt(value):0;
    }

    public long getLong(int index){
        String value = getValue(index).trim();
        if("null".equals(value)){
            return 0;
        }
        return value != null?Long.parseLong(value):0;
    }

    public float getFloat(int index){
        String value = getValue(index).trim();
        if("null".equals(value)){
            return 0;
        }
        return value != null?Float.parseFloat(value):0;
    }

    public double getDouble(int index){
        String value = getValue(index).trim();
        if("null".equals(value)){
            return 0;
        }
        return value != null?Double.parseDouble(value):0;
    }

    public String getValue(int index){
        index--;
        String currentStr = datas.get(current);
        if(currentStr == null){
            return "";
        }
        String[] currentStrDatas = currentStr.split(",");
        return index < currentStrDatas.length?currentStrDatas[index]:"";
    }
}
```
Statement：

```java
package com.mx.test.sql;

public class Statement {

    private Connection sqlConn;

    public Statement(Connection sqlConn){
        this.sqlConn = sqlConn;
    }

    public int result = 0;

    public void setResult(int result) {
        this.result = result;
    }

    public ResultSet executeQuery(String sql){
        return doSqlQuery(sql);
    }

    public int executeUpdate(String sql){
        return doSqlUpdate(sql);
    }

    public int doSqlUpdate(String sql){
        return  sqlConn.doSqlUpdate(sql);
    }

    public ResultSet doSqlQuery(String sql){
        return this.sqlConn.doSqlQuery(sql);
    }
}

``` 