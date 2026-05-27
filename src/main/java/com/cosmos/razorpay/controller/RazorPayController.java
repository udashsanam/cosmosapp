package com.cosmos.razorpay.controller;

import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/razor")
public class RazorPayController {


    private static final String WEBHOOK_SECRET = "your_webhook_secret";

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String razorpaySignature) {

        try {

            boolean isValid = verifyWebhookSignature(
                    payload,
                    razorpaySignature,
                    WEBHOOK_SECRET
            );

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid signature");
            }

            JSONObject json = new JSONObject(payload);
            String event = json.getString("event");

            switch (event) {

                // ✅ BEST EVENT FOR ORDERS
                case "order.paid": {
                    JSONObject order = json
                            .getJSONObject("payload")
                            .getJSONObject("order")
                            .getJSONObject("entity");

                    String orderId = order.getString("id");
                    int amount = order.getInt("amount");
                    String status = order.getString("status");

                    System.out.println("ORDER PAID");
                    System.out.println("Order ID: " + orderId);
                    System.out.println("Amount: " + amount);

                    // 👉 update DB using orderId
                    // mark order as PAID / SUCCESS

                    break;
                }

                case "payment.captured": {
                    JSONObject payment = json
                            .getJSONObject("payload")
                            .getJSONObject("payment")
                            .getJSONObject("entity");

                    String paymentId = payment.getString("id");
                    String orderId = payment.optString("order_id");
                    int amount = payment.getInt("amount");

                    System.out.println("PAYMENT CAPTURED");
                    System.out.println("Payment ID: " + paymentId);
                    System.out.println("Order ID: " + orderId);

                    // optional DB update

                    break;
                }

                case "payment.failed": {
                    JSONObject payment = json
                            .getJSONObject("payload")
                            .getJSONObject("payment")
                            .getJSONObject("entity");

                    String paymentId = payment.getString("id");

                    System.out.println("PAYMENT FAILED: " + paymentId);

                    // mark failed in DB

                    break;
                }

                default:
                    System.out.println("Unhandled event: " + event);
            }

            return ResponseEntity.ok("Webhook processed");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Webhook error");
        }
    }

    private boolean verifyWebhookSignature(
            String payload,
            String actualSignature,
            String secret) throws Exception {

        String generatedSignature = hmacSHA256(payload, secret);

        return generatedSignature.equals(actualSignature);
    }

    private String hmacSHA256(String data, String secret) throws Exception {

        Mac sha256Hmac = Mac.getInstance("HmacSHA256");

        SecretKeySpec secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        sha256Hmac.init(secretKey);

        byte[] hash = sha256Hmac.doFinal(
                data.getBytes(StandardCharsets.UTF_8)
        );

        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }



}


