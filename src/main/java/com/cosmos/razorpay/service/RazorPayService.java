package com.cosmos.razorpay.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RazorPayService {

    @Value("${razor.pay.key}")
    private String apiKey;

    @Value("${razor.pay.secret}")
    private String apiSecret;

    public Map<String, Object> createOrder(Double amount,String identifier) throws Exception {

        RazorpayClient client = new RazorpayClient(apiKey, apiSecret);

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100); // amount in paisa (500 INR)
        options.put("currency", "INR");
        options.put("receipt", identifier);

        Order order = client.orders.create(options);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", order.get("amount"));
        Order x =client.orders.fetch(order.get("id"));

        return response;
    }

    private  boolean verifyPayment(String razorpay_order_id, String razorpay_payment_id,
                                   String razorpay_signature) throws Exception {
        try {

            JSONObject options = new JSONObject();

            options.put("razorpay_order_id", razorpay_order_id);
            options.put("razorpay_payment_id", razorpay_payment_id);
            options.put("razorpay_signature", razorpay_signature);

            boolean isValid =
                    Utils.verifyPaymentSignature(options, apiSecret);

            if (isValid) {
                return true;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }
}
