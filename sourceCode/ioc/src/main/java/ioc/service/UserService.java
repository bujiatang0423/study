package ioc.service;

import ioc.entity.User;

import java.util.HashMap;
import java.util.List;

public interface UserService {
    List<User> queryUser(HashMap<String, Object> params);
}
