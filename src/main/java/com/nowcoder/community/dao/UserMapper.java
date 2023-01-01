package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;

public interface UserMapper {

    User selectById(int id);
}
