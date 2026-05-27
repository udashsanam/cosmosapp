package com.cosmos.khalti.controller;

import com.cosmos.khalti.service.KhaltiService;
import com.cosmos.payment.entity.PaymentDetail;
import com.cosmos.payment.repo.PaymentDetailRepo;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import com.cosmos.user.service.PackageSubscriptionServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("khalti")
@AllArgsConstructor
public class KhaltiController {

    private final KhaltiService khaltiService;

    private final PaymentDetailRepo paymentDetailRepo;
    private final PackageSubscriptionServiceImpl packageSubscriptionService;
    private final UserRepository userRepo;

    @GetMapping("/success")
    @Transactional
    public String success(@RequestParam("pidx") String pidx,
                          @RequestParam("transaction_id")String transactionId,
                          @RequestParam("tidx") String tIdx) {
        PaymentDetail paymentDetail =paymentDetailRepo.findByKhaltiCode(pidx);
        User user = userRepo.findByUserId(paymentDetail.getUserId());
        if(user == null) {
            throw new RuntimeException("User not found");
        }
        String response = paymentDetail.getKhaltiResponse();
        paymentDetail.setKhaltiResponse(
                (response == null ? "" : response + "|")
                        + pidx + "|" + transactionId + "|" + tIdx
        );

        paymentDetail.setIsKhalti(true);
        paymentDetail.setIsSuccess(Boolean.TRUE);
        paymentDetailRepo.save(paymentDetail);
        packageSubscriptionService.subscribePackage(paymentDetail.getPackageId(), user.getDeviceId());

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
                "        <p>Your Khalti payment has been completed successfully. You can start asking questions</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
