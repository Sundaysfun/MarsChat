package com.ms.service;


import com.alibaba.fastjson.JSONObject;
import com.ms.model.Message;
import com.ms.utils.MarsLanguageTrans;
import com.ms.utils.MessageEncoder;
import com.ms.utils.RandomName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint(value = "/websocket", encoders = {MessageEncoder.class})
@Component
public class WebSocketServer {
    static Log log = LogFactory.getLog(WebSocketServer.class);
    //记录当前在线人数
    private static int onlineCount = 0;
    //concurrent包的线程安全set，用来存放每个会话对应的webSocketServer对象
    private static CopyOnWriteArrayList<WebSocketServer> webSocketServers = new CopyOnWriteArrayList<>();
    //与某个客户端的会话连接，需要它来给客户端发送数据
    private Session session;
    //接收sid
    private String sid = "";
    //存储随机昵称
    private String name;
    //当前对象是不是灭霸响指拥有者？
    private boolean isThanos;

    /**
     * 连接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.name = RandomName.getRandomName();
        this.session = session;
        webSocketServers.add(this);
        addOnlineCount();
        this.sid = session.getId();
        log.info("有新窗口加入会话：" + sid + ",昵称为" + name + ",当前在线人数为：" + onlineCount);
        sendMessage(new Message("chat", "连接服务器成功... ..."));
        sendInfo(new Message("chat", name + " 加入聊天室"), null);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketServers.remove(this);
        sendInfo(new Message("chat", name + " 离开了聊天室"), null);
        subOnlineCount();
        log.info("有窗口关闭，当前在线人数为：" + onlineCount);
    }

    /**
     * 收到客户端发送的信息后调用的方法
     */
    @OnMessage
    public void onMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        switch ((String) jsonObject.get("type")) {
            case "chat":
                String value = (String) jsonObject.get("value");
                if ("贵族模式".equals((String) jsonObject.get("model"))) {
                    try {
                        value = MarsLanguageTrans.trans(value);
                    } catch (Exception e) {
                        log.error("转换火星文时出现了异常");
                    }
                }
                log.info("收到来自窗口：" + sid + "的信息" + value);
                sendInfo(new Message("chat", name + ":" + value), null);
                break;
            case "snap":
                if (isThanos) {
                    //即将被消灭的人数
                    int killNum = webSocketServers.size() / 2;
                    //灭霸本人是否被消灭？
                    boolean willKillSelf = false;
                    System.out.println("响指消灭人数" + killNum);
                    Random random = new Random();
                    StringBuilder messageValue = new StringBuilder(this.name + " 发动了灭霸的响指，消灭了");

                    for (int i = 0; i < killNum; i++) {
                        //随机幸运贵族下标
                        int willBeKilledIndex = random.nextInt(webSocketServers.size());
                        messageValue.append(webSocketServers.get(willBeKilledIndex).name + ",");
                        //判断灭霸本人是不是被自己消灭
                        WebSocketServer willBeKilledItem = webSocketServers.get(willBeKilledIndex);
                        if (this.sid == willBeKilledItem.sid) {
                            willKillSelf = true;
                        }
                        sendInfo(new Message("notice", "你被 " + this.name + " 的响指消灭了，刷新页面重新加入聊天室"), willBeKilledItem.sid);
                        webSocketServers.remove(willBeKilledItem);
                    }
                    if (willKillSelf) {
                        messageValue.append("灭霸把自己也消灭了哈哈哈！！！");
                    } else {
                        messageValue.append("灭霸竟然在这场灾难中幸免于难！！！");
                    }
                    System.out.println(webSocketServers.size());
                    sendInfo(new Message("chat", messageValue.toString()), null);
                } else {
                    sendMessage(new Message("chat", "您暂时不是灭霸响指拥有者"));
                }
                break;
        }
    }

    /**
     * 发生错误时调用的方法
     */
    @OnError
    public void onError(Throwable throwable) {
        log.info("发生错误");
        throwable.printStackTrace();
    }


    /**
     * 服务器主动发送信息
     */
    public void sendMessage(Message message) {
        try {
            this.session.getBasicRemote().sendObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群发自定义消息
     */

    public static void sendInfo(Message message, String sid) {
        log.info("发送消息到窗口：" + (sid == null ? "所有在线窗口" : sid) + ",推送内容：" + message.getValue());
        for (WebSocketServer item : webSocketServers) {
            try {
                if (sid == null) {
                    item.sendMessage(message);
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(message);
                    break;
                }
            } catch (Exception e) {
                log.error("群发信息时发生 websocket IO 异常");
                e.printStackTrace();
                continue;
            }
        }

    }

    /**
     * 随机选取一个人成为灭霸响指拥有者
     */
    public static void chooseThanos() {
        int webSocketServersSize = webSocketServers.size();
        if (webSocketServersSize > 0) {
            int random = new Random().nextInt(webSocketServers.size());
            for (WebSocketServer item : webSocketServers) {
                item.isThanos = false;
            }
            WebSocketServer webSocketServer = webSocketServers.get(random);
            webSocketServer.isThanos = true;
            sendInfo(new Message("chat", webSocketServer.name + "成为新的灭霸响指拥有者"), null);
        }
    }

    /**
     * 返回当前在线人数
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 在线人数+1
     */
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
        sendInfo(new Message("onlineCount", WebSocketServer.onlineCount + ""), null);
    }

    /**
     * 在线人数-1
     */
    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
        sendInfo(new Message("onlineCount", WebSocketServer.onlineCount + ""), null);
    }

}
