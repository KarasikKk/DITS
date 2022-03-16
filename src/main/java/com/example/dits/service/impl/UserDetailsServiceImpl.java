package com.example.dits.service.impl;

import com.example.dits.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserServiceImpl userService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userService.getUserByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("login " + login
                    + " not found");
        }
        System.out.println("---------------------> FOUND ------------->"
                + user.getFirstName());
        return (UserDetails) user;
    }
}
