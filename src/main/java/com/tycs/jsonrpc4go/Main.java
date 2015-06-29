package com.tycs.jsonrpc4go;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tycs.jsonrpc4go.model.FalconItemObject;
import com.tycs.jsonrpc4go.service.RpcClientService;

/**
 * Hello world!
 */
public class Main {

    public static void main(String[] args) {
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-rpc-all.xml");
        RpcClientService rcs = (RpcClientService) context.getBean("rpcClientService");
        rcs.startRpcCli();
        try {
            for (int i = 0; i < 100; ++i) {
                Thread.sleep(1000);
                FalconItemObject fio = new FalconItemObject("test.xiaolong", "test.xiaolong",
                                                            System.currentTimeMillis() / 1000, 60, 1.0, "GAUGE", "");
                rcs.sendToSvrSync("Transfer.Update", fio, 1000);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
