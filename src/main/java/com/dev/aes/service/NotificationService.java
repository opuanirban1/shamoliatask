package com.dev.aes.service;

import com.dev.aes.payloads.response.Notification;

public interface NotificationService {
    void sendMessages();

    void sendMessagesNotification(Notification notification);

    void addUserName(String username);

    java.util.List<String> getUserNames();

    void setUserNames(java.util.List<String> userNames);
}
