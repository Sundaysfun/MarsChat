package com.ms.controller;

import com.ms.service.WebSocketServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class ChatController {

    @GetMapping("/chatPage")
    public String chatPage(HttpSession session) {
        System.out.println(session.getId() + "   进入聊天室了");
        return "chatPage";
    }

    @GetMapping("/getOnlineCount")
    public int getOnlineCount() {
        return WebSocketServer.getOnlineCount();
    }

}
