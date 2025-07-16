package ioc.service;

import ioc.dao.UserDao;
import ioc.entity.User;

import java.util.HashMap;
import java.util.List;

public class UserServiceImpl implements UserService {
    private UserDao userDao;

   public void setUserDao(UserDao userDao){
       this.userDao = userDao;
    }

    @Override
    public List<User> queryUser(HashMap<String, Object> params) {
        List<User> users = userDao.queryUser(params);
        for (User user : users) {
            System.out.println(user.toString());
        }
        return users;
    }
}
