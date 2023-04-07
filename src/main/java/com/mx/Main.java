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