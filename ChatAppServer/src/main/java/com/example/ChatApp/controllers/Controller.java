package com.example.ChatApp.controllers;

import com.example.ChatApp.SessionHandler;
//import com.example.ChatApp.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class Controller {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private GroupsRepository groupsRepository;
    @Autowired
    private GroupChatLogRepository groupChatLogRepository;

    @GetMapping("/getJoinedGroups")
    public ResponseEntity<List<Groups>> getJoinedGroups (@RequestParam(name = "username") String username) {
        Optional<Users> optionalUser = usersRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Users user = optionalUser.get();
        List<Groups> groups = user.getGroups();

        for (Groups group : groups) {
            group.setUsers(null);
        }

        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Users>> getAllPeople() {
        List<Users> users = usersRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/personByUsername")
    public ResponseEntity<Users> getPersonByUsername(@RequestParam(name = "username") String username) {
        Optional<Users> optionalUser = usersRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
        } else {
            throw new UserNotFoundException("User not found with username: " + username);
        }
    }

    @GetMapping("/getMessages")
    public ResponseEntity<List<String>> getMessages(@RequestParam(name = "groupId") int groupId)
    {
        List<GroupChatLog> log = groupChatLogRepository.findByGroupId(groupId);

        // TODO: fill the array using a stream instead of a separate for loop
        List<String> messages = new ArrayList<>();

        for (GroupChatLog lst : log){
            messages.add(lst.toString());
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    // TODO: Friends List
    @GetMapping("/getFriends")
    public ResponseEntity<List<String>> getFriends(@RequestParam(name = "username") String username){
        Optional<Users> optionalUser = usersRepository.findByUsername(username);

        if (optionalUser.isEmpty()){
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Users user = optionalUser.get();
        List<Users> friends = user.getFriends();

        List<String> friendNames = new ArrayList<>();
        if (friends != null){
            for (Users friend : friends){
                friendNames.add(friend.getUsername());
            }
        }

        return new ResponseEntity<>(friendNames, HttpStatus.OK);
    }

    @PostMapping("/addFriend")
    public ResponseEntity<String> addFriend(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "friendUsername") String friendUsername) {

        Optional<Users> optionalUser = usersRepository.findByUsername(username);
        Optional<Users> optionalFriend = usersRepository.findByUsername(friendUsername);

        if (optionalUser.isEmpty() || optionalFriend.isEmpty()) {
            return new ResponseEntity<>("User or friend not found", HttpStatus.NOT_FOUND);
        }

        Users user = optionalUser.get();
        Users friend = optionalFriend.get();

        if (user.getFriends().contains(friend)) {
            return new ResponseEntity<>("Friend already added", HttpStatus.CONFLICT);
        }

        user.addFriend(friend);
        friend.addFriend(user);

        usersRepository.save(user);
        usersRepository.save(friend);

        return new ResponseEntity<>("Friend added successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam(name = "username") String username,
                                        @RequestParam(name = "password") String password) {
        Optional<Users> optionalUser = usersRepository.findByUsername(username);


        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);
        }

        Users user = optionalUser.get();
        if (!user.getPassword().equals(password)) {
            return new ResponseEntity<>("Invalid password!", HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>("Login successful!", HttpStatus.OK);
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestParam(name = "username") String username,
                                             @RequestParam(name = "password") String password)
    {
        Optional<Users> existingUser = usersRepository.findByUsername(username);


        if(existingUser.isPresent())
        {
            return new ResponseEntity<>("Username already taken!", HttpStatus.CONFLICT);
        }

        Users user = new Users();
        user.setUsername(username);
        user.setPassword(password);
        usersRepository.save(user);

        return new ResponseEntity<>("User created successfully!", HttpStatus.CREATED);
    }

    @PostMapping("/createGroup")
    public ResponseEntity<String> createGroup(@RequestParam(name = "group_name") String group_name)
    {
        Optional<Groups> existingGroup = groupsRepository.findByGroupName(group_name);

        if(existingGroup.isPresent())
        {
            return new ResponseEntity<>("Group name is already taken!", HttpStatus.CONFLICT);
        }

        Groups group = new Groups();
        group.setGroupName(group_name);
        groupsRepository.save(group);

        return new ResponseEntity<>("Group created successfully!", HttpStatus.CREATED);
    }

    @GetMapping("/searchGroup")
    public ResponseEntity<String> searchGroup(@RequestParam(name = "group_name") String group_name)
    {
        Optional<Groups> existingGroup = groupsRepository.findByGroupName(group_name);

        if(existingGroup.isPresent())
        {
            return new ResponseEntity<>("Group exists.", HttpStatus.OK);

        } else {
            return new ResponseEntity<>("No group", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/addUserToGroup")
    public ResponseEntity<String> addUserToGroup(@RequestParam(name = "username") String username,
                                                 @RequestParam(name = "group_name") String groupName)
    {
        Optional<Users> optionalUser = usersRepository.findByUsername(username);
        Optional<Groups> optionalGroup = groupsRepository.findByGroupName(groupName);

        if(optionalUser.isEmpty()) return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);

        if(optionalGroup.isEmpty()) return new ResponseEntity<>("Group not found!", HttpStatus.NOT_FOUND);

        Users user = optionalUser.get();
        Groups group = optionalGroup.get();

        //TODO: Това е новото
        List<Groups> userGroups = user.getGroups();
        if(!userGroups.contains(group)){
            userGroups.add(group);
            user.setGroups(userGroups);

            List<Users> groupUsers = group.getUsers();
            if(!groupUsers.contains(user)){
                groupUsers.add(user);
                group.setUsers(groupUsers);
            }

            usersRepository.save(user);
            groupsRepository.save(group);

            return new ResponseEntity<>("User added to group successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User is already a member of this group!", HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestParam(name = "username") String username,
                                                 @RequestParam(name = "oldPassword") String oldPassword,
                                                 @RequestParam(name = "newPassword") String newPassword) {

        Optional<Users> optionalUser = usersRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);}

        Users user = optionalUser.get();

        if (!user.getPassword().equals(oldPassword)) {return new ResponseEntity<>("Old password is incorrect", HttpStatus.BAD_REQUEST);}

        user.setPassword(newPassword);
        usersRepository.save(user);
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    // TODO: почти работи
    @DeleteMapping("/deleteUser")
    public ResponseEntity<String> deleteUserByUsernameAndPassword(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password) {

        Optional<Users> optionalUser = usersRepository.findByUsername(username);

        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);
        }
        Users user = optionalUser.get();
        if (!user.getPassword().equals(password)) {
            return new ResponseEntity<>("Password is incorrect!", HttpStatus.UNAUTHORIZED);
        }

        usersRepository.delete(user);
        return new ResponseEntity<>("User deleted successfully!", HttpStatus.OK);
    }

    @MessageMapping("/chat.sendMessage/{groupId}")
    @SendTo("/topic/{groupId}")
    public SessionHandler.ChatMessage sendMessage(@DestinationVariable int groupId, SessionHandler.ChatMessage message) {
        GroupChatLog groupChatLog = new GroupChatLog();
        groupChatLog.setGroupId(message.getCurrentGroupId());
        groupChatLog.setMessage(message.getName() + ": " + message.getContent());
        groupChatLogRepository.save(groupChatLog);

        return message;
    }

//// TODO: НЕ е готово още трябва му UI интерфейс
//    @MessageMapping("/chat.private/{username}")
//    @SendTo("/user/{receiver}/queue/messages")
//    public SessionHandler.ChatMessage sendPrivateMessage(@DestinationVariable String username, SessionHandler.ChatMessage message){
//
//        return message;
//    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}