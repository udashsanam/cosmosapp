package com.cosmos.videochat.config;

import com.cosmos.common.security.JwtTokenProvider;
import com.cosmos.login.entity.AppUser;
import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import com.cosmos.videochat.util.VideoChatUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthenticationHandler implements ChannelInterceptor {

    private static String SOURCE_TYPE = "source_type";

    private static String TOKEN = "token";

    private static String DEVICE_ID = "device_id";

    private final AppUserRepo appUserRepo;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    public AuthenticationHandler(AppUserRepo appUserRepo, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.appUserRepo = appUserRepo;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // Instantiate an object for retrieving the STOMP headers
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        // Check that the object is not null
        assert accessor != null;
        // If the frame is a CONNECT frame
        if(accessor.getCommand() == StompCommand.CONNECT){
             String source = accessor.getFirstNativeHeader(TOKEN);
            UsernamePasswordAuthenticationToken user = null;
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = getAuthenticationFromToken(source);
             accessor.setUser(usernamePasswordAuthenticationToken);
             System.out.println(accessor);

        }

        return message;
    }


    private UsernamePasswordAuthenticationToken getAuthenticationFromToken(String token) {

        Boolean isAuthenticted  = jwtTokenProvider.validateToken(token);

        AppUser appUser = appUserRepo.findByEmail(jwtTokenProvider.getUsername(token));
        if(appUser == null || appUser.getUserId() == null){
             appUser= userRepository.findByDeviceId(token);
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken( appUser.getEmail(), null,
                Collections.singletonList(new SimpleGrantedAuthority(appUser.getRole().getAuthority()))
        );

        // Erase the password in the token after verifying it because we will pass it to the STOMP headers.
        authenticationToken.eraseCredentials();

        return authenticationToken;
    }

    private UsernamePasswordAuthenticationToken getAuthenticationFromDeviceId(String device){
        User appUser = userRepository.findByDeviceId(device);
        if(appUser.getDeviceId() ==null){
            throw new RuntimeException("Device id not found ");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken( appUser.getEmail(),null,
                Collections.singletonList(new SimpleGrantedAuthority(appUser.getRole().getAuthority()))
        );

        // Erase the password in the token after verifying it because we will pass it to the STOMP headers.
        authenticationToken.eraseCredentials();

        return authenticationToken;
    }



}
