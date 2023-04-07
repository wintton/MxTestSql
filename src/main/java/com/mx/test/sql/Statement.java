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
