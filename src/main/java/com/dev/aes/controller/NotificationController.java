package com.dev.aes.controller;


import com.dev.aes.config.Greeting;
import com.dev.aes.config.HelloMessage;
import com.dev.aes.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;

@Slf4j
@Controller
public class NotificationController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @MessageMapping("/hello")
    @SendToUser("/topic/greetings")
    public Greeting greeting(HelloMessage message, @Header("simpSessionId") String sessionId, Principal principal) throws Exception {
        log.info("Received greeting message {} from {} with sessionId {}", message, principal.getName(), sessionId);
        notificationService.addUserName(principal.getName());
        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
}
