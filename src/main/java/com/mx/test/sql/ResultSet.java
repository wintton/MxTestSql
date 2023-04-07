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