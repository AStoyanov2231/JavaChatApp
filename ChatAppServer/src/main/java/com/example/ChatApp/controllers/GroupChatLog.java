package com.example.ChatApp.controllers;

import jakarta.persistence.*;


@Entity
@Table (name = "group_chat_log")
public class GroupChatLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "msgId")
    private int msgId;

    private int groupId;
    private String message;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("%s", message);
    }
}
