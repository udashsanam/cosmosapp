package com.cosmos.videochat.config;

import com.cosmos.common.exception.CustomException;
import com.cosmos.common.security.JwtTokenProvider;
import com.cosmos.login.entity.AppUser;
import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class WebSocketHeaderInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        HttpHeaders headers = request.getHeaders();
        List<String> token =  headers.get("token");

        AppUser appUser = userRepository.findByDeviceId(token.get(0));

        if(appUser == null || appUser.getUserId() == null){
            String jwt = "";
            try {
                 jwt = jwtTokenProvider.getUsername(token.get(0));
            }catch (Exception ex){
                throw new CustomException("jwt mal formed", HttpStatus.UNAUTHORIZED);
            }
            appUser = appUserRepo.findByEmail(jwt);
        }
        if(appUser == null){
            throw new RuntimeException("authentication fail ");
        }
        headers.add("userid", "header-value");



        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception exception) {
        // Do nothing
    }

}
