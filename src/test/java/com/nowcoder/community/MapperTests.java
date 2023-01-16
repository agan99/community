package com.nowcoder.community;
import java.util.Date;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    LoginTicketMapper loginTicketMapper;

    @Autowired
    UserService userService;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(1);
        System.out.println(user);
    }


    @Test
    public void testSelectPosts(){
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
        System.out.println(discussPostMapper.selectDiscussPostRows(132));
    }

    @Test
    public void testSelectPosts1(){
        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(0, 0, 0);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }
        System.out.println(discussPostMapper.selectDiscussPostRows(132));
    }

    /**
     * 添加登录凭证
     */
    @Test
    public void testInsertLogin(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("AAA");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    /**
     * 登录凭证
     * 测试 查询 删除功能
     */
    @Test
    public void testLoginTicket(){
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("AAA");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatus("AAA", 1);
        LoginTicket loginTicket1 = loginTicketMapper.selectByTicket("AAA");
        System.out.println(loginTicket1);
    }

    /**
     * 测试用户更改密码
     */
    @Test
    public void testChangePassword(){
        User user = userMapper.selectById(154);
        System.out.println(CommunityUtil.md5("123" + user.getSalt()));
//        Map<String, Object> map = userService.changePassword(user, "123456", "1234");
//        System.out.println(map.get("passwordMsg"));
    }

    /**
     * 插入新帖子
     */
    @Test
    public void testInsertDiscussPost(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(154);
        discussPost.setTitle("111111111");
        discussPost.setContent("3333333");
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);
    }
}
