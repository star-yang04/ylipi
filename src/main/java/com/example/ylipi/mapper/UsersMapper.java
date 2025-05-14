package com.example.ylipi.mapper;

import com.example.ylipi.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Yang xin
 * @since 2025-04-29
 */
public interface UsersMapper extends BaseMapper<Users> {
    @Select("select * from users where username=#{username}")
    Users findByUserName(String username);

    @Insert("insert into users(username,password,role,school_number,school_password,user_type,create_time,update_time,avatar_url)" +
            "values (#{username},#{password},'campus',#{schoolNumber},#{schoolPassword},#{userType},now(),now(),#{avatar_url})")
    void add(String username, String password,String schoolNumber, String schoolPassword, String userType, String avatarUrl);

    @Select("select count(*) from usst where school_number=#{schoolNumber} and school_password=#{schoolPassword}")
    int countBySchoolNumberAndPassword(String schoolNumber, String schoolPassword);

    @Select("select count(*) from users where school_number=#{schoolNumber}")
    int countSchoolNumber(String schoolNumber);

    @Select("select user_type from usst where school_number=#{schoolNumber}")
    String getUserTypeBySchoolNumber(String schoolNumber);

    @Update("update users set password=#{md5String},update_time=now() where user_id =#{id}")
    void updatePwd(String md5String, Integer id);

}

