package dao.daoImpl;

import dao.UserDao;
import domain.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import utils.JdbcUtils;

import java.util.List;

/**
 * Description:
 * User: HC
 * Date: 2019-12-21-21:14
 */
public class UserDaoImpl implements UserDao {
    private JdbcTemplate template = new JdbcTemplate(JdbcUtils.getDataSource());

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public int register(User user) {
        String sql = "INSERT INTO tab_user(uid,username,PASSWORD,NAME,birthday,sex,telephone,email,STATUS,CODE) VALUES(?,?,?,?,?,?,?,?,?,?)";
        return template.update(sql, user.getUid(), user.getUsername(), user.getPassword(), user.getName(), user.getBirthday(),
                user.getSex(), user.getTelephone(), user.getEmail(), user.getStatus(), user.getCode());

    }

    /**
     * 用户激活
     * @param code
     * @return
     */
    @Override
    public int active(String code) {
        String sql="UPDATE tab_user SET STATUS='Y' WHERE CODE=?";
        return template.update(sql,code);
    }

    /**
     * 用户登录
     * @param name
     * @param password
     */
    @Override
    public User login(String name, String password) {
        String sql="SELECT * FROM tab_user WHERE USERNAME=? AND PASSWORD =?";
        List<User> list = template.query(sql, new BeanPropertyRowMapper<>(User.class), name, password);
        if (list != null && list.size()>0) {
            return list.get(0);
        }
        return null;
    }


}
