package com.team5.librarymanager.service;

import com.team5.librarymanager.entity.User;
import com.team5.librarymanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User auth(String un,String pw){
        User user = userRepository.findByUsername(un);

        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(pw)) {
            return null;
        }

        return user;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.findAllByFullNameContainingIgnoreCase(keyword);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        User user = userRepository.findById(id).get();
        user.setStatus(false);
        userRepository.save(user);
    }

    public Long count() {
        return userRepository.count();
    }

    public void saveAll(List<User> list) {
        userRepository.saveAll(list);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public Long countNormalUsers() {
        return userRepository.countNormalUsers();
    }
}