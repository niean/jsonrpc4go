package com.tycs.jsonrpc4go;

public class RpcClientCfg {

    public String  svrHostname;
    public Integer svrPort;

    public String getSvrHostname() {
        return svrHostname;
    }

    public void setSvrHostname(String svrHostname) {
        this.svrHostname = svrHostname;
    }

    public Integer getSvrPort() {
        return svrPort;
    }

    public void setSvrPort(Integer svrPort) {
        this.svrPort = svrPort;
    }
}
