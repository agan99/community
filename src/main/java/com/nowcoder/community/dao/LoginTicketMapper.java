package com.nowcoder.community.dao;


import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 登录凭证信息表
 */
@Deprecated
public interface LoginTicketMapper {

    /**
     * 添加登录凭证
     *
     * @param loginTicket 凭证信息
     * @return id
     */
    @Insert({"INSERT INTO login_ticket(user_id, ticket, status, expired) " +
            "VALUES(#{userId}, #{ticket}, #{status}, #{expired})"})
    @Options(useCache = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 查询登录凭证
     *
     * @param ticket 凭证
     * @return 凭证信息
     */
    @Select("SELECT id, user_id, ticket, status, expired FROM login_ticket WHERE ticket = #{ticket}")
    LoginTicket selectByTicket(String ticket);

    /**
     * 删除登录凭证
     * @param ticket 凭证
     * @param status 状态
     * @return id
     */
    @Update({
            "<script>",
            "UPDATE login_ticket SET status = #{status} WHERE ticket = #{ticket}",
            "<if test = \" ticket != null \" > ",
            "and 1 = 1 ",
            "</if>",
            "</script>"
    })
    int updateStatus(String ticket, Integer status);

}
