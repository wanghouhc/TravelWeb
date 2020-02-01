package service.serviceImpl;

import dao.UserDao;
import dao.daoImpl.UserDaoImpl;
import domain.User;
import service.UserService;
import utils.Md5Utils;

/**
 * Description:
 * User: HC
 * Date: 2019-12-21-21:09
 */
public class UserServiceImpl implements UserService {
    private UserDao dao = new UserDaoImpl();

    @Override
    public boolean register(User user) throws Exception {
        //对密码进行加密
        String md5 = Md5Utils.encodeByMd5(user.getPassword());
        user.setPassword(md5);
        //调用注册的功能
        int count = dao.register(user);
        return count > 0;
    }

    /**
     * 用户激活
     *
     * @param code
     * @return
     */
    @Override
    public boolean active(String code) {
        int count = dao.active(code);
        return count > 0;
    }

    /*
    用户登录
     */
    @Override
    public User login(String name, String password) throws Exception {
        String md5 = Md5Utils.encodeByMd5(password);
        return dao.login(name, md5);
    }


}
