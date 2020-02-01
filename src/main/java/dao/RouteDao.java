package dao;

import domain.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Description:
 * User: HC
 * Date: 2019-12-24-9:20
 */
public interface RouteDao {


    List<Route> hotRoute();

    List<Route> newRoute();

    List<Route> themeRoute();

    List<Category> findCategory();

    List<Route> Search(String cid, String rname, int pageNumber, int pageSize);

    int SearchTotalCount(String cid, String rname);


    Route findRouteBy(String rid);

    Seller findSeller(Integer sid);

    Category findCategorybyId(Integer cid);

    List<RouteImg> findImag(String rid);

    Favorite isFavoriate(String rid, User user);


    void addFavorite(String rid, String now, Integer uid, JdbcTemplate template);

    void updateFavoriteCount(String rid, JdbcTemplate template);

    int totalFavorite(int uid);

    List<Route> myFavorite(User user, int pageSize, int index);

    int rankTotalCount(QueryVO vo);

    List<Route> rankRoutes(QueryVO vo, int index, int pageSize);
}
