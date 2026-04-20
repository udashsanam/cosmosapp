package com.cosmos.meta.service;

import com.cosmos.login.entity.AppUser;
import com.cosmos.meta.model.FlowStep;
import com.cosmos.meta.model.RegistrationFlowEntity;
import com.cosmos.meta.repo.RegistrationFlowRepository;
import com.cosmos.questionPool.dto.EnglishQuestionDto;
import com.cosmos.questionPool.service.EnglishQuestionPoolService;
import com.cosmos.user.dto.UserDto;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import com.cosmos.user.service.UserServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

@Service
public class MetaWebhookService {

    private static final Logger log = LoggerFactory.getLogger(MetaWebhookService.class);

    @Value("${meta.verify-token}")
    private String verifyToken;

    @Value("${meta.app-secret}")
    private String appSecret;

    @Value("${meta.app-initial-start-message}")
    private String START_CONVERSATION;

    @Value("${meta.app-update-detail}")
    private String UPDATE_DETAILS;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserRepository userRepository;

    private final MessengerService messengerService;

    private final RegistrationFlowRepository registrationFlowRepository;

    private final EnglishQuestionPoolService englishQuestionPoolService;

    private final UserServiceImpl userService;

    public MetaWebhookService(UserRepository userRepository,
                              MessengerService messengerService,
                              RegistrationFlowRepository registrationFlowRepository,
                              EnglishQuestionPoolService englishQuestionPoolService,
                              UserServiceImpl userService) {
        this.userRepository = userRepository;
        this.messengerService = messengerService;
        this.registrationFlowRepository = registrationFlowRepository;
        this.englishQuestionPoolService = englishQuestionPoolService;
        this.userService = userService;
    }


    // ── Token Verification ────────────────────────────────────────────────
    public boolean isValidToken(String token) {
        return verifyToken.equals(token);
    }

    // ── Signature Verification ────────────────────────────────────────────
    public boolean isValidSignature(String rawBody, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));

            String expected = "sha256=" + bytesToHex(hash);
            return MessageDigest.isEqual(expected.getBytes(), signature.getBytes());

        } catch (Exception e) {
            log.error("Signature verification failed", e);
            return false;
        }
    }

    // ── Payload Processing ────────────────────────────────────────────────
    public void processPayload(String rawBody) {
        try {
            JsonNode root = objectMapper.readTree(rawBody);
            String object = root.path("object").asText();

            switch (object) {
                case "page":
                    handlePageEvents(root);
                    break;

                case "instagram":
                    handleInstagramEvents(root);
                    break;

                case "whatsapp_business_account":
                    handleWhatsAppEvents(root);
                    break;

                default:
                    log.warn("Unknown object type: {}", object);
                    break;
            }

        } catch (Exception e) {
            log.error("Failed to process payload", e);
        }
    }

    // ── Facebook Page Events ──────────────────────────────────────────────
    private void handlePageEvents(JsonNode root) {
        root.path("entry").forEach(entry -> {
            String pageId = entry.path("id").asText();
            entry.path("messaging").forEach(event -> {
                if (event.has("message")) {
                    handleMessage(pageId, event);
                } else if (event.has("postback")) {
                    handlePostback(pageId, event);
                }
            });
        });
    }

    // ── Instagram Events ──────────────────────────────────────────────────
    private void handleInstagramEvents(JsonNode root) {
        root.path("entry").forEach(entry ->
                entry.path("changes").forEach(change -> {
                    String field = change.path("field").asText();
                    JsonNode value = change.path("value");
                    log.info("Instagram change - field: {}, value: {}", field, value);
                    // handle: comments, mentions, story_insights, etc.
                })
        );
    }

    // ── WhatsApp Events ───────────────────────────────────────────────────
    private void handleWhatsAppEvents(JsonNode root) {
        root.path("entry").forEach(entry ->
                entry.path("changes").forEach(change -> {
                    JsonNode value = change.path("value");
                    value.path("messages").forEach(msg -> handleWhatsAppMessage(msg, value));
                    value.path("statuses").forEach(this::handleWhatsAppStatus);
                })
        );
    }

    private void handleMessage(String pageId, JsonNode event) {
        String senderId = event.path("sender").path("id").asText();
        String text = null;
        try {
            text = event.path("message").path("text").asText();
        } catch (Exception e) {
            log.error("Failed to process message", e);
            return;
        }

        User appUser = userRepository.findByDeviceId(senderId);
        RegistrationFlowEntity registrationFlowEntity = registrationFlowRepository.findByIsCurrentStateAndSenderId(true, senderId);
        if (registrationFlowEntity == null) {
            if (appUser == null) {
                messengerService.sendMessage(senderId, "User is not register please do register first.");
                log.info("User not found:");
            } else {
                EnglishQuestionDto englishQuestionDto = new EnglishQuestionDto();
                englishQuestionDto.setUserId(appUser.getUserId());
                englishQuestionDto.setQuestionPrice(0.00);
                englishQuestionDto.setEngQuestion(text);
                englishQuestionPoolService.addQuestionToPool(englishQuestionDto);
            }
        }
        log.info("[Page {}] Message from {}: {}", pageId, senderId, text);
        handleRegistrationFlow(text, senderId);
    }

    private void handlePostback(String pageId, JsonNode event) {
        String senderId = event.path("sender").path("id").asText();
        String payload = event.path("postback").path("payload").asText();
        if (START_CONVERSATION.equals(payload) || UPDATE_DETAILS.equals(payload)) {
            User appUser = userRepository.findByDeviceId(senderId);
            if (appUser != null && START_CONVERSATION.equals(payload)) {
                messengerService.sendMessage(senderId, "User already exists start conversation.");
                return;
            }
            log.info("App initialized starting message: {}", payload);
            messengerService.sendMessage(senderId, "What is your full name? format: [First Name] [Last Name]");
            registrationFlowRepository.save(RegistrationFlowEntity.builder()
                    .flowStep(FlowStep.NAME)
                    .response(null)
                    .createdAt(new Date())
                    .isCurrentState(true)
                    .latestFlow(true)
                    .senderId(senderId)
                    .build());
        }
        log.info("[Page {}] Postback from {}: {}", pageId, senderId, payload);
    }

    @Transactional
    public void handleRegistrationFlow(String response, String senderId) {
        RegistrationFlowEntity registrationFlowEntity = registrationFlowRepository.findByIsCurrentStateAndSenderId(true, senderId);
        if (registrationFlowEntity == null) return;
        FlowStep flowStep = registrationFlowEntity.getFlowStep();
        registrationFlowEntity.setIsCurrentState(false);
        registrationFlowEntity.setUpdatedAt(new Date());
        registrationFlowEntity.setResponse(response);
        registrationFlowRepository.save(registrationFlowEntity);
        RegistrationFlowEntity newEntry = RegistrationFlowEntity.builder()
                .createdAt(new Date())
                .isCurrentState(true)
                .senderId(senderId)
                .latestFlow(true)
                .build();
        UserDto userDto = null;
        switch (flowStep) {
            case NAME:
                newEntry.setFlowStep(FlowStep.DATE_OF_BIRTH);
                messengerService.sendMessage(senderId, "Enter your date of birth (YYYY-MM-DD) AD");
                break;
            case DATE_OF_BIRTH:
                newEntry.setFlowStep(FlowStep.TIME_OF_BIRTH);
                messengerService.sendMessage(senderId, "Enter your time of birth? (HH:MM) 24 hours time formate");
                break;
            case TIME_OF_BIRTH:
                newEntry.setFlowStep(FlowStep.CITY);
                messengerService.sendMessage(senderId, "Enter your city?");
                break;
            case CITY:
                userDto = new UserDto();
                userDto.setDeviceId(senderId);
                userDto.setAccurateTime(true);
                List<RegistrationFlowEntity> registrationFlowEntityList = registrationFlowRepository.findAllByLatestFlowAndSenderId(true, senderId);
                for (RegistrationFlowEntity registrationFlow : registrationFlowEntityList) {
                    if (FlowStep.NAME.equals(registrationFlow.getFlowStep())) {
                        String[] names = registrationFlow.getResponse().split("\\s+");
                        userDto.setFirstName(names[0]);
                        userDto.setLastName(names[1]);
                    } else if (FlowStep.DATE_OF_BIRTH.equals(registrationFlow.getFlowStep())) {
                        userDto.setDateOfBirth(registrationFlow.getResponse());
                    } else if (FlowStep.TIME_OF_BIRTH.equals(registrationFlow.getFlowStep())) {
                        userDto.setBirthTime(registrationFlow.getResponse());
                    } else if (FlowStep.CITY.equals(registrationFlow.getFlowStep())) {
                        userDto.setCity(registrationFlow.getResponse());
                    }
                    registrationFlow.setLatestFlow(false);
                    registrationFlow.setIsCurrentState(false);
                }
                registrationFlowRepository.saveAll(registrationFlowEntityList);
                break;
            default:
                log.warn("Unknown flow step: {}", flowStep);
                break;
        }
        if (flowStep.equals(FlowStep.CITY)) {
            AppUser appUser = userRepository.findByDeviceId(senderId);
            if (appUser == null) {
                userDto.setUserId(0l);
            }else {
                userDto.setUserId(appUser.getUserId());
            }
            userService.processUserRegistration(userDto);
            messengerService.sendMessage(senderId, "Registration/Update process successful");
        } else {
            registrationFlowRepository.save(newEntry);
        }

    }

    private void handleWhatsAppMessage(JsonNode msg, JsonNode value) {
        String from = msg.path("from").asText();
        String type = msg.path("type").asText();
        String phone = value.path("metadata").path("display_phone_number").asText();

        if ("text".equals(type)) {
            String text = msg.path("text").path("body").asText();
            log.info("[WhatsApp {}] Message from {}: {}", phone, from, text);
        } else {
            log.info("[WhatsApp {}] {} message from {}", phone, type, from);
        }
    }

    private void handleWhatsAppStatus(JsonNode status) {
        String id = status.path("id").asText();
        String recipient = status.path("recipient_id").asText();
        String state = status.path("status").asText(); // sent, delivered, read
        log.info("Message {} to {} is now: {}", id, recipient, state);
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}