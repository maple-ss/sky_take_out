package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    //微信小程序登录凭证校验(拿到openid)
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    //拿到我的微信小程序的appid和secret
    @Autowired
    private WeChatProperties weChatProperties;
    //注入mapper
    @Autowired
    private UserMapper userMapper;
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        if (userLoginDTO == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //调用微信接口服务，获得当前微信用户openid
        String openid = getOpenid(userLoginDTO.getCode());
        //判断openid是否为空，为空表示登录失败
        if(openid == null || openid.trim().isEmpty()){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);

        //如果是新用户，自动完成注册
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //返回这个用户对象
        return user;
    }

    private String getOpenid(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        Map<String, String> map = new HashMap<>();
        //下面四个put的内容是根据微信小程序登录官方文档得来的
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            Integer errcode = jsonObject == null ? null : jsonObject.getInteger("errcode");
            if (jsonObject == null || (errcode != null && errcode != 0)) {
                log.warn("微信登录凭证校验失败：{}", json);
                return null;
            }
            return jsonObject.getString("openid");
        } catch (Exception ex) {
            log.error("解析微信登录响应失败：{}", json, ex);
            return null;
        }
    }
}
