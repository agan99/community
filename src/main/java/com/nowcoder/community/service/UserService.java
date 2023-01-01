package com.nowcoder.community.service;

import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据id查询用户信息
     * @param id 用户id
     * @return 用户信息
     */
    public User findUserById(int id){
        return userMapper.selectById(id);
    }

}
