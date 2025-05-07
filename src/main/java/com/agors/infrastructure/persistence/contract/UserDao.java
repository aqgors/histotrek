package com.agors.infrastructure.persistence.contract;

import com.agors.domain.entity.User;
import java.util.List;

public interface UserDao {
    void addUser(User user);
    User getUserById(int id);
    User getByUsernameOrEmail(String loginOrEmail);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(int id);
}
