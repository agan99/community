package com.nowcoder.community.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nowcoder.community.entity.User;

public interface UserMapper  extends BaseMapper<User>{

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int userStatus);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);
}
