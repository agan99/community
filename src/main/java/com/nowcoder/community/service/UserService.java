package com.nowcoder.community.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService  implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    /**
     * 根据id查询用户信息
     * @param id 用户id
     * @return 用户信息
     */
//    public User findUserById(int id){
//        return userMapper.selectById(id);
//    }

    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账户不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账户已存在!");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册!");
            return map;
        }

        // 注册用户
        // 盐
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setUserType(0);
        user.setUserStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮箱
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

    /**
     * 账户激活
     * @param userId 用户id
     * @param code 链接
     * @return
     */
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if (user.getUserStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            // 激活账户
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 登录密码
     * @param expiredSeconds 过期时间
     * @return 提示信息
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        // 空置处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账户不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 账户验证
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "账户不存在!");
            return map;
        }

        // 验证激活
        if (user.getUserStatus() != 1) {
            map.put("usernameMsg", "账户未激活!");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成token
        String userTokenKey = RedisKeyUtil.getUserTokenKey(CommunityUtil.generateUUID());
        // 保存 redis
        redisTemplate.opsForValue().set(userTokenKey, user.getId(), expiredSeconds, TimeUnit.SECONDS);

//        LoginTicket loginTicket = new LoginTicket();
//        loginTicket.setUserId(user.getId());
//        loginTicket.setTicket(CommunityUtil.generateUUID());
//        loginTicket.setStatus(0);
//        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
//        loginTicketMapper.insertLoginTicket(loginTicket);

        // 登录凭证保存 Redis
//        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
//        redisTemplate.opsForValue().set(ticketKey, loginTicket);

        map.put("token", userTokenKey);
        return map;
    }

    /**
     * 退出登录,修改登录凭证状态
     * 0-有效; 1-无效;
     * @param token 凭证
     */
    public void logout(String token){
        redisTemplate.delete(token);
//        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
//        loginTicket.setStatus(1);
//        redisTemplate.opsForValue().set(ticketKey, loginTicket);
//        loginTicketMapper.updateStatus(ticket, 1);
    }

    /**
     * 查询凭证
     * @param ticket
     * @return
     */
//    public LoginTicket findLoginTicket(String ticket) {
////        return loginTicketMapper.selectByTicket(ticket);
//        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
//        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
//    }

    /**
     * 更新图片链接
     * @param userId 用户id
     * @param headerUrl 图片链接
     * @return id
     */
    public int updateHeader(int userId, String headerUrl){
        int rows = userMapper.updateHeader(userId, headerUrl);
        initCache(userId);
        return rows;
    }

    /**
     * 更改用户密码
     * @param user
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public Map<String, Object> changePassword(User user, String oldPassword, String newPassword) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(oldPassword)) {
            map.put("passwordMsg", "原密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "新密码不能为空！");
            return map;
        }

        // 验证原始密码是否正确
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("passwordMsg", "输入的原密码不正确!");
            return map;
        }

        // 更新密码
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        if (user.getPassword().equals(newPassword)){
            map.put("newPasswordMsg", "新密码不能和旧密码相同");
            return map;
        }
        this.updatePassword(user.getId(), newPassword);

        return map;
    }

    /**
     * 用户去敏
     * @param user
     * @return
     */
    public User getSafetyUser(User user){
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
//        safetyUser.setPassword("");
//        safetyUser.setSalt("");
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserType(user.getUserType());
        safetyUser.setUserStatus(user.getUserStatus());
//        safetyUser.setActivationCode();
        safetyUser.setHeaderUrl(user.getHeaderUrl());
        safetyUser.setCreateTime(user.getCreateTime());

        return safetyUser;

    }

    /**
     * 根据 用户id查询用户数据
     * @param userId
     * @return
     */
    public User findUserById(int userId){
        User user = getCache(userId);
        if (user != null) return getSafetyUser(user);
        return getSafetyUser(initCache(userId));

    }

    /**
     * 更新密码
     * @param id
     * @param password
     * @return
     */
    public int updatePassword(int id, String password){
        int row = userMapper.updatePassword(id, password);
        clearCache(id);
        return row;
    }


    /**
     * 1.优先从缓存中取出用户数据
     * @param userId
     * @return
     */
    public User getCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    /**
     * 2.取不到用户数据,初始化用户数据
     * @param userId
     * @return
     */
    public User initCache(int userId){
        User user = userMapper.selectById(userId);
        if (user == null) return null;
        String userKey = RedisKeyUtil.getUserKey(user.getId());
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    /**
     * 3.数据变更删除用户数据
     * @param userId
     */
    public void clearCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}
