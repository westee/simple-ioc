package com.github.westee.ioc;

public class UserDao {
    public User getUserById(Integer id) {
        return new User(id, "user" + id);
    }
}
