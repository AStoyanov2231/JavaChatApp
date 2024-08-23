package com.example.ChatApp.controllers;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private int id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column (name = "password", nullable = false)
    private String password;

    @ManyToMany(mappedBy = "users")
    private List<Groups> groups;

    @ManyToMany
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friends_id")
    )

    private List<Users> friends;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Groups> getGroups() {
        return groups;
    }

    public void setGroups(List<Groups> groups) {
        this.groups = groups;
    }

    public List<Users> getFriends() {
        return friends;
    }
    public void setFriends(List<Users> friends) {
        this.friends = friends;
    }

    public void addFriend(Users friend) {
        if (this.friends == null) {
            this.friends = new ArrayList<>();
        }
        if (!this.friends.contains(friend)) {
            this.friends.add(friend);
        }
    }
}