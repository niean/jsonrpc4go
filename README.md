# jsonrpc4go
json-rpc-client for golang-json-rpc-server, written in java, managed by mvn

## introduction
**jsonrpc4go** implements json rpc protocal in golang's style(ignores segment 'jsonrpc'), and thus can be used as client of golang-rpc-server.


## usage
this's an example of sending items to [falcon.transfer](https://github.com/open-falcon/transfer) via json-rpc. enjoy it.

```java
package com.tycs.jsonrpc4go.test;
import com.tycs.jsonrpc4go.rpccli.RpcClient;

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

```

## demo
install and run the demo in ```com.tycs.jsonrpc4go.test.Usage```

```bash
# download src
git clone https://github.com/niean/jsonrpc4go.git
cd jsonrpc4go

# change config, hostname & port of server
vim src/main/resource/jsonrpc4go.properties
...

# mvn package
mvn clean package # get jar pkg in target

# run
cd target
java -cp jsonrpc4go-0.0.1-SNAPSHOT-jar-with-dependencies.jar com.tycs.jsonrpc4go.test.Usage

```

## dependencies
this's a mvn project and depends on some mvn repos shown as following:

```bash
org.jboss.netty
log4j
```
## reference
