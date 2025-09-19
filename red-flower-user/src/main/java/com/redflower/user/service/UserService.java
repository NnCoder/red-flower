package com.redflower.user.service;

import com.redflower.user.dto.UserLoginDTO;
import com.redflower.user.dto.UserRegisterDTO;
import com.redflower.user.entity.User;
import com.redflower.user.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    Long register(UserRegisterDTO dto);

    /**
     * 用户登录
     */
    String login(UserLoginDTO dto);

    /**
     * 根据ID获取用户信息
     */
    UserVO getUserById(Long id);

    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);

    /**
     * 更新用户信息
     */
    void updateUser(Long id, UserVO userVO);
}