package web;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.ResultInfo;
import domain.User;
import service.UserService;
import service.serviceImpl.UserServiceImpl;
import utils.BeanUtils;
import utils.MailUtils;
import utils.UUIDUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.Line;
import java.io.IOException;
import java.util.Map;

/**
 * Description:
 * User: HC
 * Date: 2019-12-21-19:41
 */
@WebServlet(urlPatterns = "/user", name = "UserServlet")
public class UserServlet extends BaseServlet {
    private UserService service = new UserServiceImpl();

    /*
     * 用户登录
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultInfo info = null;
        try {

            String check = request.getParameter("check");
            String checkcode_server = (String) request.getSession().getAttribute("CHECKCODE_SERVER");
            //判断验证码是否正确
            boolean b = checkcode_server.equalsIgnoreCase(check);
            if (b) {//验证码正确,调用service层
                String name = request.getParameter("username");
                String password = request.getParameter("password");
                User user = service.login(name, password);
                //校验激活住哪个台
                if (user == null) {
                    info = new ResultInfo(false, "用户名或密码错误");
                } else {
                    if ("Y".equals(user.getStatus())) {
                        info = new ResultInfo(true);
                        request.getSession().setAttribute("user", user);
                    } else {
                        info = new ResultInfo(false, "用户未激活");
                    }
                }
            } else {//验证码错误
                info = new ResultInfo(false, "验证码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            info = new ResultInfo(false, "系统正忙请稍后再试!");
        }
        //将结果转化为json对象传回去
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        response.getWriter().print(json);
    }

    /**
     * 用户退出
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       request.getSession().invalidate();
       response.sendRedirect(request.getContextPath()+"/login.html");
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void getUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResultInfo info = null;
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            request.getSession().setAttribute("loginUser",user);
            info = new ResultInfo(true, user.getName(), "");
        } else {
            info = new ResultInfo(false);
        }

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        response.getWriter().print(json);
    }

    /**
     * 用户注册
     */

    public void register(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultInfo info = null;
        try {
            String check = request.getParameter("check");
            String checkcode_server = (String) request.getSession().getAttribute("CHECKCODE_SERVER");

            boolean b = checkcode_server.equalsIgnoreCase(check);
            if (b) {//如果验证码正确,就将信息封装打包为javabean对象
                Map<String, String[]> map = request.getParameterMap();
                User user = BeanUtils.populate(map, User.class);
                user.setStatus("N");//设置默认状态为未激活
                user.setCode(UUIDUtils.getUuid());//激活码,要求非空唯一
                //调用service,执行sql语句
                boolean success = service.register(user);
                info = new ResultInfo(success);
                String url = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/user?action=active&code=" + user.getCode();
                String context = user.getName() + ",您好,您的账号已经注册成功,请<a href='" + url + "'>点击激活</a>后登陆";
                MailUtils.sendMail(user.getEmail(), context);
            } else {//不成功就提示失败信息
                info = new ResultInfo(false, "验证码错误");
            }
        } catch (Exception e) {
            info = new ResultInfo(false, "系统忙,请稍后...");
            e.printStackTrace();
        }
        //将结果转化为json对象传回去
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);
        response.getWriter().print(json);
    }

    /**
     * 用户激活
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取激活码
        String code = request.getParameter("code");
        //调用service
        boolean b = service.active(code);
        if (b) {//如果激活成功就进行跳转
            response.sendRedirect("./login.html");
        }

    }
}