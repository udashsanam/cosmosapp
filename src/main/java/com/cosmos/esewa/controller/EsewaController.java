package com.cosmos.esewa.controller;

import com.cosmos.esewa.model.EsewaPaymentResponse;
import com.cosmos.esewa.service.EsewaService;
import com.cosmos.payment.entity.PaymentDetail;
import com.cosmos.payment.repo.PaymentDetailRepo;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import com.cosmos.user.service.PackageSubscriptionServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import java.util.Map;

@RestController
@RequestMapping("/esewa")
public class EsewaController {

    private final EsewaService esewaService;
    private final PaymentDetailRepo paymentDetailRepo;
    private final PackageSubscriptionServiceImpl packageSubscriptionService;
    private final UserRepository userRepository;
    ObjectMapper mapper = new ObjectMapper();
    public EsewaController(EsewaService esewaService,
                           PaymentDetailRepo paymentDetailRepo,
                           PackageSubscriptionServiceImpl packageSubscriptionService,
                           UserRepository userRepository) {
        this.esewaService = esewaService;
        this.paymentDetailRepo = paymentDetailRepo;
        this.packageSubscriptionService = packageSubscriptionService;
        this.userRepository = userRepository;
    }


    @GetMapping("/success")
    @Transactional
    public String handleSuccess(@RequestParam Map<String, String> params) throws JsonProcessingException {

        String rawResponse =  esewaService.decodeBase64(params.get("data"));
        JSONObject firstObject = new JSONObject(rawResponse);

        EsewaPaymentResponse response = mapper.readValue(firstObject.toString(), EsewaPaymentResponse.class);

        PaymentDetail paymentDetail = paymentDetailRepo.findByEsewaCode(response.getTransactionUuid());
        if (paymentDetail == null) throw new RuntimeException("Payment detail not found");
        paymentDetail.setIsSuccess(Boolean.TRUE);
        paymentDetail.setIsEsewa(Boolean.TRUE);
        User user = userRepository.findByUserId(paymentDetail.getUserId());
        packageSubscriptionService.subscribePackage(paymentDetail.getPackageId(), user.getDeviceId());
        paymentDetail.setEsewaResponse(
                paymentDetail.getEsewaResponse() == null
                        ? rawResponse
                        : paymentDetail.getEsewaResponse() + " | " + rawResponse
        );
        paymentDetailRepo.save(paymentDetail);
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Payment Success</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            margin: 0;\n" +
                "            height: 100vh;\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            background: #f5f7fb;\n" +
                "            font-family: Arial, sans-serif;\n" +
                "        }\n" +
                "        .card {\n" +
                "            background: #fff;\n" +
                "            padding: 40px 30px;\n" +
                "            border-radius: 14px;\n" +
                "            box-shadow: 0 10px 25px rgba(0,0,0,0.08);\n" +
                "            text-align: center;\n" +
                "            width: 320px;\n" +
                "        }\n" +
                "        .icon {\n" +
                "            font-size: 50px;\n" +
                "            color: #22c55e;\n" +
                "        }\n" +
                "        h2 {\n" +
                "            margin: 10px 0;\n" +
                "            color: #1f2937;\n" +
                "        }\n" +
                "        p {\n" +
                "            color: #6b7280;\n" +
                "            font-size: 14px;\n" +
                "        }\n" +
                "        .btn {\n" +
                "            margin-top: 20px;\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 16px;\n" +
                "            background: #22c55e;\n" +
                "            color: white;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 8px;\n" +
                "        }\n" +
                "        .btn:hover {\n" +
                "            background: #16a34a;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"card\">\n" +
                "        <div class=\"icon\">✓</div>\n" +
                "        <h2>Payment Successful</h2>\n" +
                "        <p>Your eSewa payment has been completed successfully.</p>\n" +
                "        <a class=\"btn\" href=\"/\">Go Home</a>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

    }

    @GetMapping("/failure")
    public String handleFailure(@RequestParam Map<String, String> params) throws JsonProcessingException {

        String transactionUuid = params.get("transaction_uuid");

        PaymentDetail payment = paymentDetailRepo.findByEsewaCode(transactionUuid);
        if (payment != null) {
            String responseRaw =esewaService.checkEsewaStatus("EPAYTEST", transactionUuid, String.valueOf(payment.getAmount()));
            JSONObject firstObject = new JSONObject(responseRaw);
            EsewaPaymentResponse response = mapper.readValue(firstObject.toString(), EsewaPaymentResponse.class);
            payment.setIsSuccess(false);
            payment.setIsEsewa(true);
            // optional: store raw failure params
            payment.setEsewaResponse(
                    payment.getEsewaResponse() == null
                            ? responseRaw
                            : payment.getEsewaResponse() + " | " + responseRaw
            );            paymentDetailRepo.save(payment);
        }


        return "<h3>Payment Failed</h3><p>Please try again.</p>";
    }

    public boolean verifyTransaction(String uuid) {
        String url = "https://rc.esewa.com.np/api/epay/transaction/status/?product_code=EPAYTEST&transaction_uuid=" + uuid;

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        return response.contains("COMPLETE");
    }
}