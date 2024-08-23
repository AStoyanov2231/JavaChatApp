package com.example.ChatApp.controllers;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GroupsRepository extends JpaRepository<Groups, Integer> {
    Optional<Groups> findByGroupName(String groupName);
}