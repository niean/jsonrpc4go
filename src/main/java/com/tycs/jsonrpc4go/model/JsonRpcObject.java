package com.tycs.jsonrpc4go.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JsonRpcObject implements Serializable {

    private static final long serialVersionUID = 2263979655023414003L;

    List<Object>              params;
    String                    method;
    Object                    id;

    public JsonRpcObject(String method, Object params, Object id){
        super();
        this.method = method;
        this.params = new ArrayList<Object>();
        this.params.add(params);
        this.id = id;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }
}
