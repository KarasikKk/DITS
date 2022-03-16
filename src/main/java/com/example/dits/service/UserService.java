package com.example.dits.service;

import com.example.dits.entity.Role;
import com.example.dits.entity.User;

import java.util.List;

public interface UserService {
    void create(User user);
    void update(User user, int id);
    void delete(User user);
    void save(User user);
    void saveUserWithEncodedPassword(User user, String password);
    void updateUser(User user, int id, String firstName, String lastName, Role role, String login);
    void updateUserWithPassword(User user, int id, String firstName, String lastName, Role role, String login, String password);
    List<User> findAll();
    User getUserByLogin(String login);
    User getUserByUserId(int id);
    void deleteUserByUserId(int userId);
}
