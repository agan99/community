package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiscussPostMapper {

    /**
     * 查询帖子信息
     * @param userId 用户id
     * @param offset 从第几条开始
     * @param limit 查询条数
     * @return 帖子信息
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // 如果只有一个参数，并且是在 <if> 里使用，则必须使用别名
    /**
     * 查询帖子条数
     * @param userId 用户id
     * @return 帖子条数
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 插入帖子
     * @param discussPost 帖子信息
     * @return id
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 根据id查询帖子
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     * 更新帖子数量
     * @param id
     * @param commentCount
     * @return
     */
    int updateCommentCount(int id, int commentCount);
}
