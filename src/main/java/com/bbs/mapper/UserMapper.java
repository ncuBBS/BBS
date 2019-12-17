package com.bbs.mapper;

import com.bbs.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Insert("insert into user_table(tel,password) values(#{tel},#{password})")
   int registerUser(String tel, String password);
//用于插入个人账号密码
    @Select("Select * from user_table")
    List<User> selectAll();
//用于展示所有用户信息，供管理员调用
    @Select("Select * from user_table where tel = #{tel}")
    User selectByTel(String tel);
    //用于找到某个用户的积分
    @Select("Select Integral from user_table where tel = #{tel}")
    int selectIntegral(String tel);
    //用于找到某个用户的信誉值
    @Select("Select reputationValue from user_table where tel = #{tel}")
    int selectValue(String tel);
//根据tel找到该用户全部信息
    @Delete("delete from user_table where tel=#{tel}")
    int deleteUser(String tel);
//根据tel删除某个用户信息
    @Update("Update user_table Set password = #{password},name= #{name},sex=#{sex},sign=#{sign},head=#{head},edu=#{edu},job=#{job},workPlace=#{workPlace},registerTime=#{registerTime},birthday=#{birthday},home=#{home},integral=#{integral},reputationValue=#{reputationValue}where id = #{user.id}")
    int updateInformation(User user);
//更新个人信息，先用selectByTel找到该用户，再把想要修改的覆盖
}
