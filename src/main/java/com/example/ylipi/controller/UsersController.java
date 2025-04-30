package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.Users;
import com.example.ylipi.service.impl.UsersServiceImpl;
import com.example.ylipi.utils.JwtUtil;
import com.example.ylipi.utils.Md5Util;
import com.example.ylipi.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Pattern;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Yang xin
 * @since 2025-04-29
 */
@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersServiceImpl usersService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 注册接口
     * @param username
     * @param password
     * @param schoolNumber
     * @param schoolPassword
     * @return
     */
    @PostMapping("/register")
    public Result register(
            @RequestParam @Pattern(regexp = "^\\S{5,16}$") String username,
            @RequestParam @Pattern(regexp = "^\\S{5,20}$") String password,
            @RequestParam String schoolNumber,
            @RequestParam String schoolPassword) {



        // 检查用户名是否已存在
        Users u = usersService.findByUserName(username);
        if (u != null) {
            return Result.error("用户名已被占用");
        }

        // 校验 schoolNumber 和 schoolPassword 是否正确
        boolean schoolValid = usersService.checkSchoolInfo(schoolNumber, schoolPassword);
        if (!schoolValid) {
            return Result.error("学号或学号密码错误");
        }

        boolean schoolNumberRepeat = usersService.checkSchoolNumber(schoolNumber);
        if (schoolNumberRepeat) {
            return Result.error("该学号已经注册过了！");
        }

        // 获取user_type
        String userType = usersService.getUserTypeBySchoolNumber(schoolNumber);
        if (userType == null) {
            return Result.error("获取用户类型失败");
        }

        // 注册用户
        usersService.register(username, password,schoolNumber,schoolPassword, userType);
        return Result.success();
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestParam @Pattern(regexp = "^\\S{5,16}$") String username, @RequestParam @Pattern(regexp = "^\\S{5,20}$") String password) {
        //根据用户名查询用户
        Users loginUser = usersService.findByUserName(username);
        //判断用户是否存在
        if(loginUser==null){
            return Result.error("用户名错误");
        }
        //判断密码是否正确
        if(Md5Util.getMD5String(password).equals(loginUser.getPassword())){
            //登录成功
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",loginUser.getUserId());
            claims.put("username",loginUser.getUsername());
            String token = JwtUtil.genToken(claims);

            ThreadLocalUtil.set(claims); // 存储到当前线程

            //把token存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,12, TimeUnit.HOURS);

            Map<String,Object> data = new HashMap<>();
            data.put("user",loginUser);
            data.put("token",token);

            return Result.success(data);
        }

        return Result.error("密码错误");
    }

    /**
     * 忘记密码并修改
     * @param params
     * @return
     */
    @PatchMapping("/resetPwd")
    public Result resetPassword(@RequestBody Map<String,String> params) {
        //1.校验参数
        String username = params.get("username");
        String schoolNumber= params.get("school_number");
        String schoolPassword = params.get("school_password");
        String newPassword = params.get("new_password");
        String rePassword = params.get("re_password");

        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(schoolNumber)
                || !StringUtils.hasLength(schoolPassword) || !StringUtils.hasLength(newPassword)
                || !StringUtils.hasLength(rePassword)){
            return Result.error("缺少必要的参数");
        }

        //newPwd和rePwd是否一样
        if (!rePassword.equals(newPassword)){
            return Result.error("两次填写的新密码不一样");
        }

        // 重置密码
        boolean success = usersService.resetPassword(username, schoolNumber, schoolPassword, newPassword);
        if (!success) {
            return Result.error("重置密码失败，请检查用户名、学号和学号密码是否正确");
        }

        return Result.success();
    }


    /**
     * 修改密码
     * @param params
     * @param token
     * @return
     */
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params , @RequestHeader("Authorization") String token){
        //1.校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");

        if (!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)){
            return Result.error("缺少必要的参数");
        }

        //原密码是否正确
        //调用userService根据用户名拿到原密码，再和old_pwd比对
        //Map<String,Object> map = ThreadLocalUtil.get();
        //String username = (String) map.get("username");


        // 2.从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");


        Users loginUser = usersService.findByUserName(username);
        if (!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))){
            return Result.error("原密码填写错误");
        }

        //newPwd和rePwd是否一样
        if (!rePwd.equals(newPwd)){
            return Result.error("两次填写的新密码不一样");
        }

        //2.调用service完成密码更新
        usersService.updatePwd(newPwd,userId);

        //密码更新成功后删除redis中的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);

        return Result.success();
    }




    /**
     * 根据 ID 查询用户
     */
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        Users user = usersService.getById(id);

        System.out.println("我在测试接口hhhhhhhhhh");
        return Result.success(user);
    }

    /**
     * 查询所有用户
     */
    @GetMapping("/list")
    public Result getAllUsers(@RequestHeader("Authorization") String token) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        List<Users> list = usersService.list();
        return Result.success(list);
    }

    /**
     * 添加用户
     */
    @PostMapping("/add")
    public Result addUser(@RequestBody Users user, @RequestHeader("Authorization") String token) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        boolean saved = usersService.save(user);
        return saved ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result updateUser(@RequestBody Users user, @RequestHeader("Authorization") String token) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        user.setUpdateTime(LocalDateTime.now());
        boolean updated = usersService.updateById(user);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteUser(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        boolean removed = usersService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 分页查询 + 条件（用户名模糊 / 角色 / 用户类型）
     */
    @GetMapping("/page")
    public Result getUsersPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String userType,
            @RequestHeader("Authorization") String token
    ) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String loggedInUsername = (String) claims.get("username");

        Page<Users> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();

        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        if (role != null && !role.isEmpty()) {
            queryWrapper.eq("role", role);
        }
        if (userType != null && !userType.isEmpty()) {
            queryWrapper.eq("user_type", userType);
        }

        Page<Users> resultPage = usersService.page(page, queryWrapper);
        return Result.success(resultPage);
    }

    /**
     * 修改用户信用分
     */
    @PatchMapping("/credit/{id}")
    public Result updateCredit(@PathVariable Integer id, @RequestParam Integer creditScore, @RequestHeader("Authorization") String token) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        Users user = new Users();
        user.setUserId(id);
        user.setCreditScore(creditScore);
        user.setUpdateTime(LocalDateTime.now());
        boolean updated = usersService.updateById(user);
        return updated ? Result.success("信用分更新成功") : Result.error("更新失败");
    }

    /**
     * 修改账户余额
     */
    @PatchMapping("/balance/{id}")
    public Result updateBalance(@PathVariable Integer id, @RequestParam BigDecimal amount, @RequestHeader("Authorization") String token) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        Users user = new Users();
        user.setUserId(id);
        user.setAccountBalance(amount);
        user.setUpdateTime(LocalDateTime.now());
        boolean updated = usersService.updateById(user);
        return updated ? Result.success("余额更新成功") : Result.error("更新失败");
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batchDelete")
    public Result batchDeleteUsers(@RequestBody List<Integer> ids, @RequestHeader("Authorization") String token) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        boolean removed = usersService.removeByIds(ids);
        return removed ? Result.success("批量删除成功") : Result.error("删除失败");
    }

    /**
     * 修改密码（前提：必须加密存储密码）
     */
    @PatchMapping("/password/{id}")
    public Result updatePassword(@PathVariable Integer id, @RequestParam String newPassword, @RequestHeader("Authorization") String token) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        // 示例中直接保存新密码，实际应进行加密处理
        Users user = new Users();
        user.setUserId(id);
        user.setPassword(newPassword); // 建议使用 BCrypt 等方式加密
        user.setUpdateTime(LocalDateTime.now());
        boolean updated = usersService.updateById(user);
        return updated ? Result.success("密码修改成功") : Result.error("密码修改失败");
    }

    /**
     * 校验用户名是否存在
     */
    @GetMapping("/checkUsername")
    public Result checkUsernameExist(@RequestParam String username, @RequestHeader("Authorization") String token) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String loggedInUsername = (String) claims.get("username");

        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        long count = usersService.count(queryWrapper);
        return count > 0 ? Result.error("用户名已存在") : Result.success("用户名可用");
    }

    /**
     * 导出用户数据为 Excel（简化版本）
     */
    @GetMapping("/export")
    public void exportUsers(HttpServletResponse response, @RequestHeader("Authorization") String token) {
        // 从 Token 中解析出用户信息
        Map<String, Object> claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get("id");
        String username = (String) claims.get("username");

        List<Users> userList = usersService.list();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("用户信息");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("用户名");
            header.createCell(2).setCellValue("角色");
            header.createCell(3).setCellValue("用户类型");
            header.createCell(4).setCellValue("邮箱");

            int rowNum = 1;
            for (Users user : userList) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getUserId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getRole());
                row.createCell(3).setCellValue(user.getUserType());
                row.createCell(4).setCellValue(user.getEmail());
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment;filename=users.xlsx");
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

