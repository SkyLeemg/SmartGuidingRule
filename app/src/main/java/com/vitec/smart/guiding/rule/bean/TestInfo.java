package com.vitec.smart.guiding.rule.bean;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

/**
 * Created by skyel on 2018/10/11.
 */
@SmartTable(name = "测量数据表")
public class TestInfo {

    @SmartColumn(id = 1, name = "垂直度")
    private String column;
    @SmartColumn(id = 2,name = "水平度")
    private String vertical;
    @SmartColumn(id = 3,name = "误差值")
    private String w;


    public TestInfo() {

    }

    public TestInfo(String column,String vertical,String w) {
        this.column = column;
        this.vertical = vertical;
        this.w = w;

    }
    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getVertical() {
        return vertical;
    }

    public void setVertical(String vertical) {
        this.vertical = vertical;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }
}

