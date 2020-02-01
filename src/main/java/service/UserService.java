package service;

import domain.User; /**
 * Description:
 * User: HC
 * Date: 2019-12-21-20:22
 */
public interface UserService {
    boolean register(User user) throws Exception;


    boolean active(String code);

    User login(String name, String password) throws Exception;
}
