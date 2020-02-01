package web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.PageBean;
import domain.ResultInfo;
import domain.Route;
import domain.User;
import domain.QueryVO;
import service.RouteService;
import service.serviceImpl.RouteServiceImpl;
import utils.BeanUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * User: HC
 * Date: 2019-12-24-9:49
 */
@WebServlet(urlPatterns = "/route", name = "RouteServlet")
public class RouteServlet extends BaseServlet {
    private RouteService routeService = new RouteServiceImpl();


    public void findRoute(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //得到路线的map集合,将集合封装成json,返回到页面
        ResultInfo info = null;
        try {
            Map<String, List<Route>> route = routeService.findRoute();
            info = new ResultInfo(true, route, "");
        } catch (Exception e) {
            e.printStackTrace();
            info = new ResultInfo(false, "系统忙,请稍后");
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        response.getWriter().print(json);
    }

    public void Category(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultInfo info = null;

        try {
            String category = routeService.findCategory();
            info = new ResultInfo(true, category, "");
        } catch (JsonProcessingException e) {
            info = new ResultInfo(false, "系统忙,请稍后");
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        response.getWriter().print(json);
    }

    public void Search(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultInfo info = null;
        try {
            String rname = request.getParameter("rname");
            String cid = request.getParameter("cid");
            //当前默认页码
            int pageNumber = 1;
            //每页显示的数量
            int pageSize = 8;
            //将当前页码修改为设置的页码
            String pageNumberStr = request.getParameter("pageNumber");
            if (pageNumberStr != null && !"".equals(pageNumberStr)) {
                pageNumber = Integer.parseInt(pageNumberStr);
            }
            //将数据封装返回
            PageBean<Route> pageBean = routeService.Search(cid, rname, pageNumber, pageSize);
            info = new ResultInfo(true, pageBean, "");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            info = new ResultInfo(false, "系统忙,请稍后...");
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        response.getWriter().print(json);
    }

    public void detail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultInfo info = null;
        try {
            String rid = request.getParameter("rid");
            Route route = routeService.detail(rid);
            info = new ResultInfo(true, route, "");
        } catch (Exception e) {
            e.printStackTrace();
            info = new ResultInfo(false, "系统忙,请稍后");
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        response.getWriter().print(json);
    }

    public void inFavorite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultInfo info = null;
        //1.接收参数,根据数据库的内容判定,查询是否收藏需要rid和uid共同判断
        String rid = request.getParameter("rid");
        User user = (User) request.getSession().getAttribute("loginUser");

        //user可能为空这时候就属于未登录的状态直接提示用户未登录
        try {
            if (user != null) {
                boolean b = routeService.isFavoriate(rid, user);
                info = new ResultInfo(true, b, "");
            } else {
                info = new ResultInfo(true, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            info = new ResultInfo(false, "系统忙，请稍候");
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        response.getWriter().print(json);
    }

    public void addFavorite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultInfo info = null;
        //1.接收参数,根据数据库的内容判定,查询是否收藏需要rid和uid共同判断
        try {
            User user = (User) request.getSession().getAttribute("loginUser");
            if (user == null) {
                info = new ResultInfo(true, -1);
            } else {
                //user可能为空这时候就属于未登录的状态直接提示用户未登录
                String rid = request.getParameter("rid");
                boolean success = routeService.addFavorite(rid, user);
                //处理结果
                if (success) {
                    //收藏成功了,页面进行刷新
                    Route route = routeService.detail(rid);
                    info = new ResultInfo(true, route.getCount(), "");
                } else {
                    //收藏失败了
                    info = new ResultInfo(false, "收藏失败，请重试或联系管理员");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            info = new ResultInfo(false, "系统忙，请稍候");
        }

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        response.getWriter().print(json);

    }

    public void getFavorite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultInfo info = null;
        //判断当前用户是否登录

        try {
            User user = (User) request.getSession().getAttribute("loginUser");
            if (user == null) {
                info = new ResultInfo(true, -1);
            } else {
                //接受参数并传递
                //给数据库传递查询和分页的参数
                //1.当前页,如果判断参数是否为空再决定是否覆盖默认页
                int pageNumber = 1;
                //2.每页大小
                int pageSize = 12;
                String pageNumberstr = request.getParameter("pageNumber");
                if (pageNumberstr != null && !"".equals(pageNumberstr)) {
                    pageNumber = Integer.parseInt(pageNumberstr);
                }
                //3.调用service并传参
                PageBean<Route> pageBean = routeService.getFavorite(pageNumber, pageSize, user);
                info = new ResultInfo(true, pageBean, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            info = new ResultInfo(false, "系统忙,请稍后");
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        response.getWriter().print(json);

    }

    /**
     * 收藏排序
     *
     * @param request
     * @param response
     * @throws Exception
     */
    public void rankFavorite(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultInfo info = null;
        try {
            //对参数进行接收
            Map<String, String[]> map = request.getParameterMap();
            int pageSize = 8;
            int pageNumber = 1;
            String pageNumberstr = request.getParameter("pageNumber");
            if (pageNumberstr != null && !"".equals(pageNumberstr)) {
                pageNumber = Integer.parseInt(pageNumberstr);
            }
            //2.封装实体：把搜索条件封装成一个QueryVO对象
            QueryVO vo = BeanUtils.populate(map, QueryVO.class);
            //传递参数
            PageBean<Route> pageBean = routeService.rankFavorite(vo, pageSize, pageNumber);
            info = new ResultInfo(true, pageBean, "");
        } catch (Exception e) {
            e.printStackTrace();
            info = new ResultInfo(false, "系统忙,请稍后");
        }
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        response.getWriter().print(json);
    }


}




