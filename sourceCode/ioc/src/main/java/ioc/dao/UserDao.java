package ioc.dao;

import ioc.entity.User;

import java.util.HashMap;
import java.util.List;

public interface UserDao {
    List<User> queryUser(HashMap<String, Object> params);

}
