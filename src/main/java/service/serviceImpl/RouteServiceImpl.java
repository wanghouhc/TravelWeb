package service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.RouteDao;
import dao.daoImpl.RouteDaoImpl;
import domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import service.RouteService;
import utils.JdbcUtils;
import utils.JedisUtils;
import utils.PageUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * User: HC
 * Date: 2019-12-24-9:23
 */
public class RouteServiceImpl implements RouteService {
    private RouteDao routeDao = new RouteDaoImpl();

    /**
     * 查询热门,新的,主题路线信息
     */
    @Override
    public Map<String, List<Route>> findRoute() {
        Map<String, List<Route>> map = new HashMap<>();
        List<Route> hotRoute = routeDao.hotRoute();
        map.put("hotRoute", hotRoute);
        List<Route> newRoute = routeDao.newRoute();
        map.put("newRoute", newRoute);
        List<Route> themeRoute = routeDao.themeRoute();
        map.put("themeRoute", themeRoute);

        return map;
    }

    /**
     * 从缓存中获取目录信息
     */
    @Override
    public String findCategory() throws JsonProcessingException {
        //先从缓存中找,如果缓存中没有再去mysql数据库中寻找
        String categories = JedisUtils.getCache("categories");
        if (categories == null || "".equals(categories)) {
            List<Category> categoryList = routeDao.findCategory();
            ObjectMapper mapper = new ObjectMapper();
            categories = mapper.writeValueAsString(categoryList);
            //获取到数据转换为json格式后将数据缓存在gedis中
            JedisUtils.setCache("categories", categories);
        }
        return categories;
    }

    /**
     * 获取显示的路线线路列表
     */
    @Override
    public PageBean<Route> Search(String cid, String rname, int pageNumber, int pageSize) {
        //创建pagebean来存储返回的页面数据
        PageBean<Route> pageBean = new PageBean<>();
        //获取有多少条数据
        int totalCount = routeDao.SearchTotalCount(cid, rname);
        pageBean.setTotalCount(totalCount);
        //看看这些数据能分成多少页
        int pageCount = (int) Math.ceil(totalCount * 1.0 / pageSize);
        pageBean.setPageCount(pageCount);
        //设置当前页
        pageBean.setPageNumber(pageNumber);
        //设置每页多少条
        pageBean.setPageSize(pageSize);

        //页码从那条开始显示,从结束
        int[] pagination = PageUtils.pagination(pageNumber, pageCount);
        //页码条从几行开始显示
        pageBean.setStart(pagination[0]);
        //页码条显示几行结束
        pageBean.setEnd(pagination[1]);

        //当前页面信息集合
        int index = (pageNumber - 1) * pageSize;
        List<Route> routeList = routeDao.Search(cid, rname, index, pageSize);
        pageBean.setData(routeList);
        return pageBean;
    }

    /**
     * 查找路线详细信息
     */
    @Override
    public Route detail(String rid) {
        //#1. rid对应的路线
        Route route = routeDao.findRouteBy(rid);
        //#2. 路线所属的商家。路线里有sid，是关联的商家id

        Seller seller = routeDao.findSeller(route.getSid());
        route.setSeller(seller);
        //#3. 路线所属的分类。路线里有cid，是关联的分类id
        Category categories = routeDao.findCategorybyId(route.getCid());
        route.setCategory(categories);
        //#4. 路线拥有的图片
        List<RouteImg> imglist = routeDao.findImag(rid);
        route.setRouteImgList(imglist);

        return route;
    }

    /**
     * 判断是否已经收藏
     */

    @Override
    public boolean isFavoriate(String rid, User user) {
        Favorite favoriate = routeDao.isFavoriate(rid, user);
        return favoriate != null;
    }

    /**
     * 添加事务来管理收藏的同步
     */
    @Override
    public boolean addFavorite(String rid, User user) {
        //开启jdbc的事物
        //1.获取连接池对象
        DataSource dataSource = JdbcUtils.getDataSource();
        //2.使用这个连接池,创建一个jdbcTemplate对象
        JdbcTemplate template = new JdbcTemplate(dataSource);
        //3.使用spring框架提供的一个工具类初始化事物的标志
        TransactionSynchronizationManager.initSynchronization();
        //4.用spring框架提供的一个工具,从连接池里获取一个连接
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            //5.开启事务
            connection.setAutoCommit(false);
            //6.调用dao,向收藏表插入一条记录
            String now = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            routeDao.addFavorite(rid, now, user.getUid(), template);


            //7.调用dao,修改收藏的数量
            routeDao.updateFavoriteCount(rid, template);

            //提交事务
            connection.commit();
            System.out.println("事务提交了");
            return true;
        } catch (Exception e) {
            //回滚事务
            try {
                System.out.println("回滚事务");
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                //9.把连接对象重置成默认的事物
                connection.setAutoCommit(true);
                //10.清理事物的标志
                TransactionSynchronizationManager.clearSynchronization();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 查找我的收藏信息
     */
    @Override
    public PageBean<Route> getFavorite(int pageNumber, int pageSize, User user) {
        //查询和分页所需要的返回数据
        //封装pageBean对象
        PageBean<Route> pageBean = new PageBean<>();
        //1.封装当前页
        pageBean.setPageNumber(pageNumber);
        //2.封装页面大小
        pageBean.setPageSize(pageSize);
        //3.获取总页数
        int totalCount = routeDao.totalFavorite(user.getUid());
        pageBean.setTotalCount(totalCount);
        //4.获取导航条的个数
        int pageCount = PageUtils.calcPageCount(totalCount, pageSize);
        pageBean.setPageCount(pageCount);
        //5.获取起始页面和终止页f面
        int[] pagination = PageUtils.pagination(pageNumber, pageCount);
        pageBean.setStart(pagination[0]);
        pageBean.setEnd(pagination[1]);
        //6.获取当前页的数据
        int index = PageUtils.calcSqlLimitIndex(pageNumber, pageSize);
        List<Route> routeList = routeDao.myFavorite(user, pageSize, index);//存储的是当前页面的路线数据
        pageBean.setData(routeList);
        return pageBean;

    }

    @Override
    public PageBean<Route> rankFavorite(QueryVO vo, int pageSize, int pageNumber) {
        //封装pageBean
        PageBean<Route> pageBean = new PageBean<>();
        //查询和分页所需要的返回数据
        //1.封装当前页
        pageBean.setPageNumber(pageNumber);
        //2.封装页面大小
        pageBean.setPageSize(pageSize);
        //3.获取总页数
        int totalCount = routeDao.rankTotalCount(vo);
        pageBean.setTotalCount(totalCount);
        //4.获取导航条的个数
        int pageCount = PageUtils.calcPageCount(totalCount, pageSize);
        pageBean.setPageCount(pageCount);
        //5.获取起始页面和终止页面
        int[] pagination = PageUtils.pagination(pageNumber, pageCount);
        pageBean.setStart(pagination[0]);
        pageBean.setEnd(pagination[1]);
        //6.获取当前页的数据
        int index = PageUtils.calcSqlLimitIndex(pageNumber, pageSize);
        List<Route> routes = routeDao.rankRoutes(vo, index, pageSize);
        pageBean.setData(routes);
        return pageBean;
    }
}

