package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import domain.PageBean;
import domain.QueryVO;
import domain.Route;
import domain.User;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * User: HC
 * Date: 2019-12-24-9:21
 */
public interface RouteService {
    Map<String, List<Route>> findRoute();

    String findCategory() throws JsonProcessingException;

    PageBean<Route> Search(String cid, String rname, int pageNumber, int pageSize);

    Route detail(String rid);

    boolean isFavoriate(String rid, User user);

    boolean addFavorite(String rid, User user);

    PageBean<Route> getFavorite(int pageNumber, int pageSize, User user);

    PageBean<Route> rankFavorite(QueryVO vo, int pageSize, int pageNumber);
}
