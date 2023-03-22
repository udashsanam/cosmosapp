package com.cosmos.common.utility;

import com.cosmos.admin.entity.QuestionPrice;
import com.cosmos.admin.service.QuestionPriceService;
import com.cosmos.login.entity.AppUser;
import com.cosmos.login.entity.Role;
import com.cosmos.login.repo.AppUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "runner.enabled", matchIfMissing = true, havingValue = "true")
public class PopulatePredefinedValues implements CommandLineRunner {

    @Autowired
    private AppUserRepo appUserRepo;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private QuestionPriceService questionPriceService;

    @Override
    public void run(String... args) throws Exception {
        AppUser appUser = new AppUser();
        appUser.setFirstName("Cosmos");
        appUser.setLastName("Astrology");
        appUser.setEmail("cosmosastrology.112@gmail.com");
        appUser.setHashPassword(passwordEncoder.encode("System@123"));
        appUser.setProfileImageUrl("");
        appUser.setEnabled(true);
        appUser.setAccountNonLocked(true);
        appUser.setInitialPasswordChanged(false);
        appUser.setRole(Role.ROLE_ADMIN);
        appUserRepo.save(appUser);

        QuestionPrice questionPrice = new QuestionPrice();
        questionPrice.setQuestionPrice(1);
        questionPrice.setDiscountInPercentage(0);
        questionPriceService.saveQuestionPrice(questionPrice);
    }
}
