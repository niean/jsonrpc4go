package com.tycs.jsonrpc4go.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.tycs.jsonrpc4go.rpccli.RpcClient;

/**
 * Test Case for Json Rpc Client
 */
public class Main {

    final static Logger logger = Logger.getLogger("Main");

    public static void main(String[] args) throws Exception {
        // get config
        Properties pro = new Properties();
        pro.load(Main.class.getResourceAsStream("/jsonrpc4go.properties"));
        String hostname = pro.getProperty("json.rpc.svr.hostname");
        Integer port = Integer.parseInt(pro.getProperty("json.rpc.svr.port"));

        // create client instance
        final RpcClient rcs = new RpcClient(hostname, port);

        // connect to server
        while (!rcs.connect()) {
            Thread.sleep(1000);
        }

        // shutdown after 10s
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {

            public void run() {
                if ("Open".equals(rcs.status())) {
                    rcs.disConnect();
                } else {
                    rcs.connect();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);

        // send data list
        Integer step = 1;
        for (;;) {
            Thread.sleep(step * 1000);
            long ts = System.currentTimeMillis() / 1000;
            FalconItem fio1 = new FalconItem("test.jrc", "test.jrc1", ts, step, 1.0, "GAUGE", "");
            FalconItem fio2 = new FalconItem("test.jrc", "test.jrc2", ts, step, 1.0, "GAUGE", "t0=tag0");
            List<Object> list = new ArrayList<Object>();
            list.add(fio1);
            list.add(fio2);
            if (!rcs.sendToSvrSync("Transfer.Update", list, 1000)) {
                // logger.error("send item failed: " + list);
            }
        }
    }
}
