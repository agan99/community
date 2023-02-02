package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义登录拦截器
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor, CommunityConstant {

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 在Controller之前执行 请求
     * 请求开始时查询登录用户，携带本次请求的用户数据
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // 1. 从 cookie中获取登录凭证
        String token = CookieUtil.getValue(request, "token");
//
//        // 2. 判断凭证是否有效
//        if (ticket != null) {
//            LoginTicket loginTicket = userService.findLoginTicket(ticket);
//            if (loginTicket != null && (loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date()))) {
//                User userById = userService.findUserById(loginTicket.getUserId());
//                // 3. 持有本次用户凭证
//                hostHolder.setUser(userById);
//            }
//        }

        // 取出 token
//        String token = request.getHeader("token");
        // 查询 数据库是否有 token
        if (token != null){
            Integer userId = (Integer) redisTemplate.opsForValue().get(token);
            if (userId != null) {
//                redisTemplate.expire(token, DEFAULT_EXPIRED_SECONDS, TimeUnit.SECONDS);
                User user = userService.findUserById(userId);
                hostHolder.setUser(user);
            }
        }

//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        return true;
    }

    /**
     * 在Controller之后执行 响应
     * 将请求的数据传送给前端
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 在TemplateEngine之后执行
     * 请求结束时清理用户数据
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
