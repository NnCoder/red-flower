package com.redflower.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.redflower.common.exception.BusinessException;
import com.redflower.user.dto.UserLoginDTO;
import com.redflower.user.dto.UserRegisterDTO;
import com.redflower.user.entity.User;
import com.redflower.user.mapper.UserMapper;
import com.redflower.user.service.UserService;
import com.redflower.user.util.JwtUtil;
import com.redflower.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public Long register(UserRegisterDTO dto) {
        // 检查用户名是否已存在
        User existUser = getUserByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户
        User user = new User();
        BeanUtil.copyProperties(dto, user);
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());

        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public String login(UserLoginDTO dto) {
        User user = getUserByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("用户已被禁用");
        }

        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        // 生成JWT令牌
        return JwtUtil.generateToken(user);
    }

    @Override
    public UserVO getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public User getUserByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public void updateUser(Long id, UserVO userVO) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        BeanUtil.copyProperties(userVO, user, "id", "username", "password", "createTime");
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
}