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
        List<String> webToken = headers.get("sec-websocket-protocol");
        List<String> deviceId =  headers.get("token");


            if(deviceId!=null && deviceId.size() !=0){
                try {
                AppUser mobileUser = userRepository.findByDeviceId(deviceId.get(0));
                headers.add("userid", mobileUser.getUserId().toString());
                 } catch (Exception ex){
                throw new CustomException("Failed to connect to websocket", HttpStatus.BAD_REQUEST);
                    }
            } else if(webToken !=null && webToken.size() != 0){
                String jwt;
                try {
                     jwt = jwtTokenProvider.getUsername(webToken.get(0));
                }catch (Exception ex){
                    throw new CustomException("jwt mal formed", HttpStatus.UNAUTHORIZED);
                }
                AppUser webUser = appUserRepo.findByEmail(jwt);
                headers.add("userid", webUser.getUserId().toString());
            } else {
                throw new CustomException("Unauthorized", HttpStatus.UNAUTHORIZED);
            }

//        Sec-WebSocket-Protocol: deviceId, eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb3Ntb3Nhc3Ryb2xvZ3kuMTEyQGdtYWlsLmNvbSIsImRpc3BsYXlOYW1lIjoiQ29zbW9zIEFzdHJvbG9neSIsImFwcFVzZXJJZCI6MSwiYXV0aCI6IlJPTEVfQURNSU4iLCJpYXQiOjE2ODIzOTk0MjV9.uxVHWLUC-rmSfk3rMAKAJnMEjMBu59JGygh4ZtC_xKg
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception exception) {
        // Do nothing
    }

}
