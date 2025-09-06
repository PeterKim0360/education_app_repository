package com.zjxu.educationapp.common.config;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSONObject;
import com.zjxu.educationapp.common.constant.ErrorCode;
import com.zjxu.educationapp.common.utils.Result;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import com.zjxu.educationapp.common.constant.SecurityConstant;


public class WebSocketConfigurator extends ServerEndpointConfig.Configurator {

    /* +NOTE 关于拦截websocket请求
    token放在请求头中，第一次握手使用Get请求；
    协议升级：WebSocket握手虽然起始于一个HTTP GET请求，但它是一个协议升级请求（包含 Upgrade: websocket 等头信息）。
    它的目的是将HTTP协议切换到WebSocket协议。
    它不经过mvc拦截器，只经过servlet过滤器
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
        String token = null;
        if (request.getHeaders().containsKey(SecurityConstant.TOKEN_HEADER)) {
            //同一个头字段名可能对应多个值，所以取第一个（也是唯一一个）
            token = request.getHeaders().get(SecurityConstant.TOKEN_HEADER).get(0);
        }
        StpUtil.getLoginIdByToken(token);
        Map<String, Object> userProperties = config.getUserProperties();
        // 获取指定Token对应的账号id，如果未登录，则返回 null
        if (token != null) {
            Object loginIdObj = StpUtil.getLoginIdByToken(token);
            if (loginIdObj == null) {
                //token无效
                userProperties.put(SecurityConstant.AUTH_ERROR, "Invalid token");
            } else {
                userProperties.put(SecurityConstant.USER_ID, loginIdObj);
            }
        } else {
            userProperties.put(SecurityConstant.AUTH_ERROR, "Token is empty");
        }
        super.modifyHandshake(config, request, response);
    }
}
