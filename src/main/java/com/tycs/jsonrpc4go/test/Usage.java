package com.tycs.jsonrpc4go.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.tycs.jsonrpc4go.rpccli.RpcClient;

/**
 * Example of Json Rpc for Golang(Falcon)
 */
public class Usage {

    final static Logger logger = Logger.getLogger("Usage");

    public static void main(String[] args) throws Exception {
        // get config
        Properties pro = new Properties();
        pro.load(Usage.class.getResourceAsStream("/jsonrpc4go.properties"));
        String hostname = pro.getProperty("json.rpc.svr.hostname");
        Integer port = Integer.parseInt(pro.getProperty("json.rpc.svr.port"));

        // create client instance
        final RpcClient rcs = new RpcClient(hostname, port);

        // connect to server
        while (!rcs.connect()) {
            Thread.sleep(1000);
        }

        // send data list
        Integer step = 1;
        for (int i = 0; i < 60; i++) {
            Thread.sleep(step * 1000);
            long ts = System.currentTimeMillis() / 1000;
            FalconItem fio1 = new FalconItem("test.jrc", "test.jrc1", ts, step, 1.0, "GAUGE", "");
            List<Object> list = new ArrayList<Object>();
            list.add(fio1);
            if (!rcs.sendToSvrSync("Transfer.Update", list, 1000)) {
                logger.error("json rpc client send failed: " + list);
            }
        }

        // stop client
        rcs.stopRpcCli();
    }
}
