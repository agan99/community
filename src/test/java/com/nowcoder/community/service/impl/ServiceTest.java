package com.nowcoder.community.service.impl;

import com.nowcoder.community.CommunityApplication;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ServiceTest {

    @Resource
    MessageService messageService;

    @Test
    public void getLetterListTest(){
        Page page = new Page();
        User user = new User();
        user.setId(111);
        List<Map<String, Object>> list = messageService.getLetterList(page, user);
        for (Map<String, Object> map : list) {
            System.out.println(map);
        }
    }

    @Test
    public void getCount(){
        Long letterUnreadCount = messageService.findLetterUnreadCount(111, null);
        System.out.println(letterUnreadCount);
    }


    @Test
    public void testGetLetterDetail(){
        Page page = new Page();
        List<Map<String, Object>> letterDetail = messageService.getLetterDetail("111_112", page);
        for (Map<String, Object> map : letterDetail) {
            System.out.println(map);
        }
    }

}
