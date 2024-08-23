package com.example.ChatApp.controllers;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupChatLogRepository extends JpaRepository<GroupChatLog, Integer> {
        List<GroupChatLog> findByGroupId(Integer groupId);
}