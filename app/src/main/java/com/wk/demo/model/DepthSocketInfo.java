package com.wk.demo.model;

import com.google.gson.JsonArray;

import java.util.List;

//ticker，socket返回数据
public class DepthSocketInfo {
    private String channel = "";
    private String level = "";
    private String instrumentId = "";
    private String symbol = "";
    private List<JsonArray> b;
    private List<JsonArray> a;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<JsonArray> getB() {
        return b;
    }

    public void setB(List<JsonArray> b) {
        this.b = b;
    }

    public List<JsonArray> getA() {
        return a;
    }

    public void setA(List<JsonArray> a) {
        this.a = a;
    }

}
