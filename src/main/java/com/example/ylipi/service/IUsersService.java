package com.example.ylipi.service;

import com.example.ylipi.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Yang xin
 * @since 2025-04-29
 */
public interface IUsersService extends IService<Users> {
    Users findByUserName( String username);

    void register(String username , String password,String schoolNumber,String schoolPassword, String userType);

    boolean checkSchoolInfo(String schoolNumber, String schoolPassword);

    boolean checkSchoolNumber(String schoolNumber);

    String getUserTypeBySchoolNumber(String schoolNumber);

    boolean resetPassword(String username, String schoolNumber, String schoolPassword, String newPassword);

    void updatePwd(String newPwd,Integer userId);

}
