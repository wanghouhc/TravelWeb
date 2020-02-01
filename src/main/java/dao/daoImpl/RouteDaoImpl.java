package dao.daoImpl;

import dao.RouteDao;
import domain.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import utils.JdbcUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * User: HC
 * Date: 2019-12-24-9:26
 */
public class RouteDaoImpl implements RouteDao {
    private JdbcTemplate template = new JdbcTemplate(JdbcUtils.getDataSource());

    @Override
    public List<Route> hotRoute() {
        //查找人气路线
        String sql = "SELECT * FROM tab_route WHERE rflag=1 ORDER BY COUNT DESC LIMIT 0,4";
        return template.query(sql, new BeanPropertyRowMapper<>(Route.class));
    }

    @Override
    public List<Route> newRoute() {
        //查找新路线
        String sql = "SELECT * FROM tab_route WHERE rflag=1 ORDER BY rdate DESC LIMIT 0,4";
        return template.query(sql, new BeanPropertyRowMapper<>(Route.class));


    }

    @Override
    public List<Route> themeRoute() {
        //查找主题路线
        String sql = "SELECT *  FROM tab_route WHERE rflag=1 AND isThemetour=1 LIMIT 0,4";
        return template.query(sql, new BeanPropertyRowMapper<>(Route.class));

    }

    @Override
    public List<Category> findCategory() {
        String sql = "SELECT * FROM tab_category ORDER BY cid";
        return template.query(sql, new BeanPropertyRowMapper<>(Category.class));
    }

    @Override
    public List<Route> Search(String cid, String rname, int index, int pageSize) {
        //String sql = "SELECT * FROM tab_route WHERE rflag = 1 AND rname LIKE ? AND cid = ? LIMIT ?,?";

        List<Object> params = new ArrayList<>();

        String sql = "SELECT * FROM tab_route WHERE rflag = 1";
        if (rname != null && !"".equals(rname)) {
            sql += " AND rname LIKE ?";
            params.add("%" + rname + "%");
        }
        if (cid != null && !"".equals(cid)) {
            sql += " AND cid = ?";
            params.add(cid);
        }
        sql += " LIMIT ?,?";
        params.add(index);
        params.add(pageSize);

        return template.query(sql, new BeanPropertyRowMapper<>(Route.class), params.toArray());
    }


    @Override
    public int SearchTotalCount(String cid, String rname) {
        //String sql = "SELECT COUNT(*) FROM tab_route WHERE rflag = 1 AND rname LIKE ? AND cid = ?";

        List<Object> params = new ArrayList<>();
        String sql = "SELECT COUNT(*) FROM tab_route WHERE rflag = 1";
        if (rname != null && !"".equals(rname)) {
            sql += " AND rname LIKE ?";
            params.add("%" + rname + "%");
        }
        if (cid != null && !"".equals(cid)) {
            sql += " AND cid = ?";
            params.add(cid);
        }

        return template.queryForObject(sql, Integer.class, params.toArray());

    }

    /**
     * 查找路线
     *
     * @param rid
     * @return
     */
    @Override
    public Route findRouteBy(String rid) {
        String sql = "SELECT * FROM tab_route WHERE rid = ?";
        List<Route> query = template.query(sql, new BeanPropertyRowMapper<>(Route.class), rid);
        return query.get(0);
    }

    /**
     * 查找商家
     *
     * @param sid
     * @return
     */
    @Override
    public Seller findSeller(Integer sid) {
        String sql = "SELECT * FROM tab_seller WHERE sid = ?";
        Seller seller = null;
        try {
            seller = template.queryForObject(sql, new BeanPropertyRowMapper<>(Seller.class), sid);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return seller;
    }

    /**
     * 查找目录信息
     *
     * @param cid
     * @return
     */
    @Override
    public Category findCategorybyId(Integer cid) {
        String sql = "SELECT * FROM tab_category WHERE cid = ?";
        Category category = null;
        try {
            category = template.queryForObject(sql, new BeanPropertyRowMapper<>(Category.class), cid);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return category;
    }

    /**
     * 查找路线的图片信息
     *
     * @param rid
     * @return
     */
    @Override
    public List<RouteImg> findImag(String rid) {
        String sql = "SELECT * FROM tab_route_img WHERE rid = ?";
        return template.query(sql, new BeanPropertyRowMapper<>(RouteImg.class), rid);
    }

    /**
     * 查找被收藏的路线
     *
     * @param rid
     * @param user
     * @return
     */
    @Override
    public Favorite isFavoriate(String rid, User user) {
        String sql = "SELECT * FROM tab_favorite WHERE rid=? AND uid=?";
        List<Favorite> favoriteList = template.query(sql, new BeanPropertyRowMapper<>(Favorite.class), rid, user.getUid());
        if (favoriteList != null && favoriteList.size() > 0) {
            return favoriteList.get(0);
        }
        return null;
    }

    /**
     * 添加收藏
     *
     * @param rid
     * @param now
     * @param uid
     * @param template
     */
    @Override
    public void addFavorite(String rid, String now, Integer uid, JdbcTemplate template) {
        template.update("insert into tab_favorite (rid,date,uid) values (?,?,?)", rid, now, uid);
        ;
    }

    /**
     * 更新收藏
     *
     * @param rid
     * @param template
     */
    @Override
    public void updateFavoriteCount(String rid, JdbcTemplate template) {
        //使用Service传递进来的template对象执行SQL语句
        template.update("UPDATE tab_route SET COUNT = COUNT + 1 WHERE rid = ?", rid);
    }

    /**
     * 得到收藏的总量
     */
    @Override
    public int totalFavorite(int uid) {
        String sql = "SELECT COUNT(*) FROM tab_favorite WHERE uid = ?";
        return template.queryForObject(sql, Integer.class, uid);
    }

    /**
     * 得到被收藏的路线当前页面路线信息
     */
    @Override
    public List<Route> myFavorite(User user, int pageSize, int index) {
        String sql = "SELECT * FROM tab_favorite f LEFT JOIN tab_route r ON f.rid = r.rid WHERE uid = ? LIMIT ?,?";
        return template.query(sql, new BeanPropertyRowMapper<>(Route.class), user.getUid(), index, pageSize);
    }

    /**
     * 获取收藏的路线数量
     *
     * @param vo
     * @return
     */
    @Override
    public int rankTotalCount(QueryVO vo) {
        String sql = "select count(*) from tab_route where rflag=1";
        ArrayList<Object> list = new ArrayList<>();
        if (vo.getRname() != null && !"".equals(vo.getRname())) {
            sql += " and rname like ? ";
            list.add("%"+vo.getRname()+"%");
        }
        if (vo.getMinprice() != null&&!"".equals(vo.getMinprice())) {
            sql+=" and price>=?";
            list.add(vo.getMinprice());
        }
        if (vo.getMaxprice() != null&&!"".equals(vo.getMaxprice())) {
            sql+=" and price<=?";
            list.add(vo.getMaxprice());
        }

        return template.queryForObject(sql,Integer.class,list.toArray());
    }

    /**
     * 查询当前页面路线的详细信息
     * @param vo
     * @param index
     * @param pageSize
     */
    @Override
    public List<Route> rankRoutes(QueryVO vo, int index, int pageSize) {
        String sql = "select * from tab_route where rflag=1";
        ArrayList<Object> list = new ArrayList<>();
        if (vo.getRname() != null && !"".equals(vo.getRname())) {
            sql += " and rname like ?";
            list.add("%"+vo.getRname()+"%");
        }
        if (vo.getMinprice() != null&&!"".equals(vo.getMinprice())) {
            sql+=" and price>=?";
            list.add(vo.getMinprice());
        }
        if (vo.getMaxprice() != null&&!"".equals(vo.getMaxprice())) {
            sql+=" and price<=?";
            list.add(vo.getMaxprice());
        }
        sql+=" ORDER BY COUNT DESC LIMIT ?, ?";
        list.add(index);
        list.add(pageSize);

        return template.query(sql,new BeanPropertyRowMapper<>(Route.class),list.toArray());


    }


}



