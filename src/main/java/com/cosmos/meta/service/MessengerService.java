package com.cosmos.meta.service;

import com.cosmos.login.entity.AppUser;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import com.cosmos.user.service.PackageSubscriptionServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessengerService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${meta.app-accesstoken}")
    private String PAGE_ACCESS_TOKEN;

    private final UserRepository userRepository;

    private final PackageSubscriptionServiceImpl packageSubscriptionService;

    public MessengerService(UserRepository userRepository,
                            PackageSubscriptionServiceImpl packageSubscriptionService) {
        this.userRepository = userRepository;
        this.packageSubscriptionService = packageSubscriptionService;
    }

    public void sendMessage(String recipientId, String text) {

        String url = "https://graph.facebook.com/v25.0/me/messages?access_token=" + PAGE_ACCESS_TOKEN;

        Map<String, Object> body = new HashMap<>();
        body.put("recipient", Map.of("id", recipientId));
        body.put("message", Map.of("text", text));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForObject(url, request, String.class);
    }
    public String getUserName(String psid) {
        String url = "https://graph.facebook.com/v18.0/" + psid +
                "?fields=first_name,last_name&access_token=" + PAGE_ACCESS_TOKEN;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        return response;
    }

    public void sendAnswerToUser(Long userId, String text) {
        User user = userRepository.findByUserId(userId);
        packageSubscriptionService.useSubscribePackage(user.getDeviceId());
        sendMessage(user.getDeviceId(), text);
    }


}