package com.example.dits.service.impl;

import com.example.dits.DAO.UserRepository;
import com.example.dits.entity.Role;
import com.example.dits.entity.User;
import com.example.dits.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }


    @Transactional
    public void create(User user) {
        repository.save(user);
    }

    @Transactional
    public void update(User user, int id) {
        Optional<User> us = repository.findById(id);
        if(us.isEmpty())
            return;
        else
            repository.save(user);
    }

    @Transactional
    public void delete(User user) {
        repository.delete(user);
    }

    @Transactional
    public void save(User user) {
        repository.save(user);
    }

    @Transactional
    public List<User> findAll() {
        return repository.findAll();
    }

    @Transactional
    public User getUserByLogin(String login){
        return repository.getUserByLogin(login);
    }

    @Transactional
    @Override
    public User getUserByUserId(int id) {
        return repository.getUserByUserId(id);
    }

    @Transactional
    @Override
    public void deleteUserByUserId(int userId){
        repository.deleteUserByUserId(userId);
    }

    @Override
    public void saveUserWithEncodedPassword(User user, String password){
        setPassword(user,password);
        save(user);
    }

    @Override
    public void updateUser(User user, int id, String firstName, String lastName, Role role, String login) {
        updateUser(user, firstName, lastName, role, login);
        update(user, id);
    }

    @Override
    public void updateUserWithPassword(User user, int id, String firstName, String lastName, Role role, String login, String password){
        updateUser(user, firstName, lastName, role, login);
        setPassword(user,password);
        update(user,id);
    }

    private void setPassword(User user, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
    }

    private void updateUser(User user, String firstName, String lastName,
                            Role role, String login){
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole(role);
        user.setLogin(login);
    }
}
