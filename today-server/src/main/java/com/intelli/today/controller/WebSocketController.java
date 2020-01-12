package com.intelli.today.controller;

import com.intelli.today.domain.Client2ServerMessage;
import com.intelli.today.domain.Server2ClientMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {
    @Autowired
    //使用 SimpMessagingTemplate 向浏览器发送信息
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/hello") // @MessageMapping 和 @RequestMapping 功能类似，浏览器向服务器发起请求时，映射到该地址。
    @SendTo("/nasus/getResponse") //如果服务器接受到了消息，就会对订阅了 @SendTo 括号中的地址的浏览器发送消息。
    public Server2ClientMessage say(Client2ServerMessage message) throws Exception {
        Thread.sleep(3000);
        return new Server2ClientMessage("Hello," + message.getName() + "!");
    }

    @MessageMapping("/chat")
    public void handleChat(Principal principal, String msg) {
        // 在 SpringMVC 中，可以直接在参数中获得 principal，principal 中包含当前用户信息
        if (principal.getName().equals("nasus")) {
            // 硬编码，如果发送人是 nasus 则接收人是 chenzy 反之也成立。
            // 通过 messageingTemplate.convertAndSendToUser 方法向用户发送信息，参数一是接收消息用户，参数二是浏览器订阅地址，参数三是消息本身
            messagingTemplate.convertAndSendToUser("chenzy",
                    "/queue/notifications", principal.getName() + "-send:" + msg);
        } else {
            messagingTemplate.convertAndSendToUser("nasus",
                    "/queue/notifications", principal.getName() + "-send:" + msg);
        }
    }
}
