package com.example.ylipi.service.impl;

import com.example.ylipi.entity.Users;
import com.example.ylipi.mapper.UsersMapper;
import com.example.ylipi.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ylipi.utils.Md5Util;
import com.example.ylipi.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Yang xin
 * @since 2025-04-29
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {
    @Autowired
    private UsersMapper userMapper;


    @Override
    public Users findByUserName(String username) {
        Users u = userMapper.findByUserName(username);
        return u;
    }

    @Override
    public void register(String username, String password,String schoolNumber,String schoolPassword, String userType) {
        //密码加密存入数据库
        String md5String = Md5Util.getMD5String(password);
        String md5String2 = Md5Util.getMD5String(schoolPassword);
        //添加
        userMapper.add(username,md5String,schoolNumber,md5String2,userType);
    }

    @Override
    public boolean checkSchoolInfo(String schoolNumber, String schoolPassword) {
        int count = userMapper.countBySchoolNumberAndPassword(schoolNumber, schoolPassword);
        return count > 0;
    }

    @Override
    public boolean checkSchoolNumber(String schoolNumber) {
        int count = userMapper.countSchoolNumber(schoolNumber);
        return count >0;
    }

    @Override
    public String getUserTypeBySchoolNumber(String schoolNumber) {
        return userMapper.getUserTypeBySchoolNumber(schoolNumber);
    }

    @Override
    public boolean resetPassword(String username, String schoolNumber, String schoolPassword, String newPassword) {
        // 检查用户是否存在
        Users user = userMapper.findByUserName(username);
        if (user == null) {
            return false;
        }

        // 验证学号和学号密码
        if (!checkSchoolInfo(schoolNumber, schoolPassword)) {
            return false;
        }

        // 验证学号是否匹配
        if (!schoolNumber.equals(user.getSchoolNumber())) {
            return false;
        }

        // 更新密码
        String md5Password = Md5Util.getMD5String(newPassword);
        //Map<String,Object> map = ThreadLocalUtil.get();
        //Integer id = (Integer) map.get("id");
        Integer id =user.getUserId();
        userMapper.updatePwd(md5Password,id);
        return true;
    }

    @Override
    public void updatePwd(String newPwd,Integer userId) {
        //Map<String,Object> map = ThreadLocalUtil.get();
        //Integer id = (Integer) map.get("id");
        userMapper.updatePwd(Md5Util.getMD5String(newPwd),userId);
    }


}
