package com.example.ylipi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ylipi.common.Result;
import com.example.ylipi.entity.Users;
import com.example.ylipi.service.impl.UsersServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 根据 ID 查询用户
     */
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Integer id) {
        Users user = usersService.getById(id);
        return Result.success(user);
    }

    /**
     * 查询所有用户
     */
    @GetMapping("/list")
    public Result getAllUsers() {
        List<Users> list = usersService.list();
        return Result.success(list);
    }

    /**
     * 添加用户
     */
    @PostMapping("/add")
    public Result addUser(@RequestBody Users user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        boolean saved = usersService.save(user);
        return saved ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result updateUser(@RequestBody Users user) {
        user.setUpdateTime(LocalDateTime.now());
        boolean updated = usersService.updateById(user);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteUser(@PathVariable Integer id) {
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
            @RequestParam(required = false) String userType
    ) {
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
    public Result updateCredit(@PathVariable Integer id, @RequestParam Integer creditScore) {
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
    public Result updateBalance(@PathVariable Integer id, @RequestParam BigDecimal amount) {
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
    public Result batchDeleteUsers(@RequestBody List<Integer> ids) {
        boolean removed = usersService.removeByIds(ids);
        return removed ? Result.success("批量删除成功") : Result.error("删除失败");
    }

    /**
     * 修改密码（前提：必须加密存储密码）
     */
    @PatchMapping("/password/{id}")
    public Result updatePassword(@PathVariable Integer id, @RequestParam String newPassword) {
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
    public Result checkUsernameExist(@RequestParam String username) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        long count = usersService.count(queryWrapper);
        return count > 0 ? Result.error("用户名已存在") : Result.success("用户名可用");
    }

    /**
     * 导出用户数据为 Excel（简化版本）
     */
    @GetMapping("/export")
    public void exportUsers(HttpServletResponse response) {
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

