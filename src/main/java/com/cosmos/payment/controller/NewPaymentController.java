package com.cosmos.payment.controller;

import com.cosmos.admin.entity.QuestionPackage;
import com.cosmos.admin.entity.QuestionPrice;
import com.cosmos.admin.repo.QuestionPackageRepo;
import com.cosmos.admin.repo.QuestionPriceRepo;
import com.cosmos.esewa.model.PaymentModel;
import com.cosmos.esewa.service.EsewaService;
import com.cosmos.khalti.service.KhaltiService;
import com.cosmos.payment.entity.PaymentDetail;
import com.cosmos.payment.repo.PaymentDetailRepo;
import com.cosmos.payment.service.AES;
import com.cosmos.razorpay.service.RazorPayService;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/payment")
@RequiredArgsConstructor
public class NewPaymentController {

    private final String esewaServiceUrl = "https://rc-checkout.esewa.com.np/api/client/intent/payment/book";

    private final EsewaService esewaService;

    private final KhaltiService khaltiService;

    private final RazorPayService razorPayService;

    private final QuestionPackageRepo questionPackageRepo;

    private final QuestionPriceRepo questionPriceRepo;

    private final UserRepository userRepository;

    private final PaymentDetailRepo paymentDetailRepo;




    @GetMapping("/checkout")
    @Transactional
    public String index(@RequestParam("code") String code,@RequestParam("package") String packageId, Model model) throws Exception {

        QuestionPrice questionPrice = questionPriceRepo.selectLatestPrice();
        QuestionPackage questionPackage = questionPackageRepo.getById(Long.parseLong(packageId));
        Double amount = questionPackage.getQuestionPackage() * questionPrice.getQuestionPrice();
        amount = amount - questionPackage.getPackageDiscount();
        User user = userRepository.findByDeviceId(AES.decrypt(code));
        if (user == null) {
            throw new Exception("Unknown user");
        }

        PaymentDetail paymentDetail = new PaymentDetail();
        paymentDetail.setIsSuccess(false);
        paymentDetail.setAmount(amount);
        paymentDetail.setPackageId(questionPackage.getId());
        String id= UUID.randomUUID().toString();
        paymentDetail.setIdentifier(id);
        paymentDetail.setUserId(user.getUserId());

//        String khaltiLink = khaltiService.initKhalti(amount * 100);
//        model.addAttribute("khaltiLink", khaltiLink);
//        String pidx = khaltiLink.split("pidx=")[1];
//        paymentDetail.setKhaltiCode(pidx);
//        System.out.println(pidx);


        Map<String, String>  paymentData = esewaService.initiatePayment(amount);
        model.addAttribute("paymentData", paymentData);
        paymentDetail.setEsewaCode(paymentData.get("transaction_uuid"));
        String.valueOf(paymentData.get("transaction_uuid"));

        Map<String, Object> order = razorPayService.createOrder(amount, id);
        model.addAttribute("orderId", order.get("orderId").toString());
        model.addAttribute("amount", order.get("amount"));
        paymentDetail.setRazorPayCode(String.valueOf(order.get("orderId")));
        System.out.println(String.valueOf(order.get("orderId")));

        paymentDetailRepo.save(paymentDetail);

        return "payment";
    }
    @GetMapping("/package")
    public String packagePayment(@RequestParam("code") String code, Model model)   {
        List<QuestionPackage> questionPackages = questionPackageRepo.findAllQuestionsPackage();
        QuestionPrice questionPrice = questionPriceRepo.selectLatestPrice();
        List<PaymentModel> paymentModels = new ArrayList<>();
        for (QuestionPackage questionPackage : questionPackages) {
            PaymentModel paymentModel = new PaymentModel();
            paymentModel.setId(questionPackage.getId());
            paymentModel.setAmount(questionPackage.getQuestionPackage() * questionPrice.getQuestionPrice());
            paymentModel.setPackageName(questionPackage.getPackageName() + "(" + questionPackage.getTargetedCountry() + ")");
            paymentModel.setQuestionCount(questionPackage.getQuestionPackage());
            paymentModel.setDiscount(questionPackage.getPackageDiscount());
            paymentModel.setFinalAmount(paymentModel.getAmount() - paymentModel.getDiscount());
            paymentModels.add(paymentModel);
        }
        model.addAttribute("paymentModels", paymentModels);
        model.addAttribute("code", code);
        return "package";
    }
}
