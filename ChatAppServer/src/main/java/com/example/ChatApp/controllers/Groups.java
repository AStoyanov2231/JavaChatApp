package com.example.ChatApp.controllers;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table (name = "chat_groups")
public class Groups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "group_id")
    private int groupId;

    @Column (name = "group_name", unique = true, nullable = false)
    private String groupName;

    // TODO: Новото е това
    @ManyToMany
    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    private List<Users> users;

    public int getGroupId() {return groupId;}

    public void setGroupId(int groupId) {this.groupId = groupId;}

    public String getGroupName() {return groupName;}

    public void setGroupName(String groupName) { this.groupName = groupName;}


    public List<Users> getUsers() {return users;}

    public void setUsers(List<Users> users) {this.users = users;}

    @Override
    public String toString() {
        return String.format("%s", this.groupName);
    }
}
