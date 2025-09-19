package com.redflower.user.controller;

import com.redflower.common.result.Result;
import com.redflower.user.dto.UserLoginDTO;
import com.redflower.user.dto.UserRegisterDTO;
import com.redflower.user.service.UserService;
import com.redflower.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户注册、登录、信息管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody UserRegisterDTO dto) {
        Long userId = userService.register(dto);
        return Result.success("注册成功", userId);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody UserLoginDTO dto) {
        String token = userService.login(dto);
        return Result.success("登录成功", token);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        UserVO userVO = userService.getUserById(id);
        return Result.success(userVO);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserVO userVO) {
        userService.updateUser(id, userVO);
        return Result.success("更新成功");
    }
}