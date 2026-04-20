package com.cosmos.meta;

import com.cosmos.meta.service.MessengerService;
import com.cosmos.meta.service.MetaWebhookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/webhook")
public class MetaWebhookController {

    private final MetaWebhookService webhookService;

    private final MessengerService messengerService;

    public MetaWebhookController(MetaWebhookService webhookService, MessengerService messengerService) {
        this.webhookService = webhookService;
        this.messengerService = messengerService;
    }

    @GetMapping
    public ResponseEntity<String> verify(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {
        System.out.println(challenge);

        if ("subscribe".equals(mode) && webhookService.isValidToken(token)) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification failed");
    }

    // ── Receive Events (POST) ─────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<String> receive(
            @RequestBody String rawBody,
            @RequestHeader("X-Hub-Signature-256") String signature) throws JsonProcessingException {

        if (!webhookService.isValidSignature(rawBody, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        System.out.println(rawBody);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(rawBody);

        JsonNode entry = root.path("entry").get(0);
        JsonNode messaging = entry.path("messaging").get(0);
        String senderId = messaging.path("sender").path("id").asText();

        try {
            webhookService.processPayload(rawBody);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error in webhook");
            e.printStackTrace();
        }


        System.out.println("Sender ID: " + senderId);

        return ResponseEntity.ok("EVENT_RECEIVED");
    }

    @PostMapping("/send-to-messenger")
    public ResponseEntity<?> send(@RequestBody Map<String, String> body) {

        String fullName = body.get("fullName");
        String dob = body.get("dob");

        String message = "Name: " + fullName + ", DOB: " + dob;

        // Call Facebook API
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://graph.facebook.com/v18.0/me/messages?access_token=PAGE_ACCESS_TOKEN";

        Map<String, Object> request = new HashMap<>();
        request.put("recipient", Map.of("id", "PSID_USER_ID"));
        request.put("message", Map.of("text", message));

        restTemplate.postForObject(url, request, String.class);

        return ResponseEntity.ok(Map.of("status", "sent"));
    }




}