package com.tycs.jsonrpc4go.test;

public class FalconItem {

    String  endpoint;
    String  metric;
    Long    timestamp;
    Integer step;
    Double  value;
    String  counterType;
    String  tags;

    public FalconItem(String e, String m, Long ts, Integer step, double d, String type, String tags){
        this.endpoint = e;
        this.metric = m;
        this.timestamp = ts;
        this.step = step;
        this.value = d;
        this.counterType = type;
        this.tags = tags;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCounterType() {
        return counterType;
    }

    public void setCounterType(String counterType) {
        this.counterType = counterType;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
