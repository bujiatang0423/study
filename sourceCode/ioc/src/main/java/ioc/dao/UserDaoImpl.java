package ioc.dao;

import ioc.entity.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDaoImpl implements UserDao{
        private DataSource dataSource;

        public void setDataSource(DataSource dataSource){
            this.dataSource = dataSource;
        }

        public void init(){
            System.out.println("userDaoImpl 初始化方法被调用");
        }

    @Override
    public List<User> queryUser(HashMap<String, Object> params) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<User> userList = new ArrayList<>();

        try{
            connection = dataSource.getConnection();
            StringBuilder sqlBuilder = new StringBuilder("select * from user where 1=1 ");
            List<Object> paramList = new ArrayList<>();

            // 处理userName参数
            if (params.containsKey("userName") && params.get("userName") != null) {
                sqlBuilder.append("and userName = ? ");
                paramList.add(params.get("userName"));
            }

            preparedStatement = connection.prepareStatement(sqlBuilder.toString());

            // 设置参数
            for (int i = 0; i < paramList.size(); i++) {
                preparedStatement.setObject(i + 1, paramList.get(i));
            }

            // 执行查询
            rs = preparedStatement.executeQuery();

            // 处理结果集
            while (rs.next()) {
                User user = new User();
                user.setUserName(rs.getString("userName"));
                user.setAge(rs.getInt("age"));
                // 根据实际User类的字段继续设置其他属性
                userList.add(user);
            }

        } catch (SQLException e) {
            // 处理SQL异常
            e.printStackTrace();
            throw new RuntimeException("查询用户失败", e);
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userList;
    }
}
