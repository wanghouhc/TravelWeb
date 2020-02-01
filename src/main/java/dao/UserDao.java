package dao;

import domain.User;

/**
 * Description:
 * User: HC
 * Date: 2019-12-21-20:22
 */
public interface UserDao {
    int register(User user);


    int active(String code);

    User login(String name, String password);
}
