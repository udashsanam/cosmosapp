package com.cosmos.paypal.service;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.orders.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaypalService {

    @Value("${payment.base.url}")
    private String baseUrl;
    @Value("${paypal.client.id}")
    private   String PAYPAL_CLIENT_ID ;
    @Value("${paypal.secret}")
    private  String PAYPAL_CLIENT_SECRET;
    public static PayPalHttpClient client;

    public  PayPalHttpClient getClient() {

        if (client == null) {
            PayPalEnvironment environment =
                    new PayPalEnvironment.Sandbox(PAYPAL_CLIENT_ID, PAYPAL_CLIENT_SECRET);

            client = new PayPalHttpClient(environment);
        }
        return client;
    }

    public Map<String, String> createOrder(String amount) throws IOException {
        OrdersCreateRequest request =
                new OrdersCreateRequest();

        request.prefer("return=representation");

        request.requestBody(
                new OrderRequest()
                        .checkoutPaymentIntent("CAPTURE")
                        .applicationContext(
                                new ApplicationContext()
                                        .returnUrl(
                                                baseUrl + "/paypal/success"
                                        )
                                        .cancelUrl(
                                                baseUrl + "/paypal/cancel"
                                        )
                        )
                        .purchaseUnits(
                                java.util.List.of(
                                        new PurchaseUnitRequest()
                                                .amountWithBreakdown(
                                                        new AmountWithBreakdown()
                                                                .currencyCode("USD")
                                                                .value(amount)
                                                )
                                )
                        )
        );

        var response =
                getClient().execute(request);

        Order order = response.result();
        Map<String, String> map = new HashMap<>();

        for (LinkDescription link : order.links()) {

            if ("approve".equals(link.rel())) {
                map.put("orderId", order.id());
                map.put("paymentLink", link.href());
                return  map;
            }
        }

        return new HashMap<>();
    }


}
