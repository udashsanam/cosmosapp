package com.cosmos.meta.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

@Service
public class MessengerMenuApp {

    @Value("${meta.app-accesstoken}")
    private String PAGE_ACCESS_TOKENS;
    private static final String API_URL =
        "https://graph.facebook.com/v25.0/me/messenger_profile?access_token=" ;

    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    public  String createPersistentMenu(List<LocaleMenu> menus) {
        Map<String, Object> body = Map.of("persistent_menu", menus);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(API_URL + PAGE_ACCESS_TOKENS, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Create failed: " + response.getBody());
        }

        return response.getBody();
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    public  String deletePersistentMenu() {
        Map<String, Object> body = Map.of("fields", List.of("persistent_menu"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            API_URL+ PAGE_ACCESS_TOKENS , HttpMethod.DELETE, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Delete failed: " + response.getBody());
        }

        return response.getBody();
    }

    // -------------------------------------------------------------------------
    // DTOs
    // -------------------------------------------------------------------------

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MenuItem {
        public String type;
        public String title;
        public String payload;
        public String url;

        @JsonProperty("webview_height_ratio")
        public String webviewHeightRatio;

        @JsonProperty("call_to_actions")
        public List<MenuItem> callToActions;

        public static MenuItem postback(String title, String payload) {
            MenuItem m = new MenuItem();
            m.type = "postback"; m.title = title; m.payload = payload;
            return m;
        }

        public static MenuItem webUrl(String title, String url) {
            MenuItem m = new MenuItem();
            m.type = "web_url"; m.title = title; m.url = url;
            return m;
        }

        public static MenuItem nested(String title, List<MenuItem> children) {
            MenuItem m = new MenuItem();
            m.type = "nested"; m.title = title; m.callToActions = children;
            return m;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LocaleMenu {
        public String locale;

        @JsonProperty("composer_input_disabled")
        public boolean composerInputDisabled;

        @JsonProperty("call_to_actions")
        public List<MenuItem> callToActions;

        public LocaleMenu(String locale, boolean composerInputDisabled, List<MenuItem> callToActions) {
            this.locale = locale;
            this.composerInputDisabled = composerInputDisabled;
            this.callToActions = callToActions;
        }
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------

//    public void  mains() {
//
//
//        // --- CREATE example --------------------------------------------------
//        List<MenuItem> items = List.of(
//            MenuItem.postback("Restart registration Process",  "UPDATE_DETAILS")
//        );
//
//        List<LocaleMenu> menus = List.of(new LocaleMenu("default", false, items));
//        System.out.println("Create: " + createPersistentMenu(menus));
//
//    }

    public void deletePersistentMenus() {
        String url = "https://graph.facebook.com/v25.0/me/messenger_profile?access_token=" + PAGE_ACCESS_TOKENS;
        Map<String, Object> body = Map.of(
                "fields", List.of("persistent_menu", "get_started")
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
    }

    public  String setGetStarted(String payload) {
        Map<String, Object> body = Map.of(
                "get_started", Map.of("payload", payload)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(API_URL + PAGE_ACCESS_TOKENS, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("setGetStarted failed: " + response.getBody());
        }

        return response.getBody();
    }
}