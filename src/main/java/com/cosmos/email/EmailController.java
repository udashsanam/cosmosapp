package com.cosmos.email;


import com.cosmos.email.dto.Test;
import com.cosmos.login.repo.AppUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/email")
@CrossOrigin
public class EmailController {

    @Autowired
    public AppUserRepo appUserRepo;

    public EmailController(AppUserRepo appUserRepo){
        this.appUserRepo = appUserRepo;
    }


    @PostMapping("/change-password")
    public String resetPassword(@RequestParam("email") String email,
                                           @RequestParam("oldPassword") String oldPassword,
                                           @RequestParam("password") String password,
                                           @RequestParam("confirmPassword") String confirmPassword){

        System.out.println(email);
        System.out.println(password);

        return "<h1> WElcome <h1>";
    }

    @PostMapping("/reset-password")
    public String resetPasswords(@ModelAttribute("test")Test test){



        return "<h1> WElcome <h1>";
    }

    @GetMapping("/test")
    public ResponseEntity<ModelAndView> getUi(){
        ModelAndView modelAndView = new ModelAndView("reset-password");
        return ResponseEntity.ok(modelAndView);
    }
}
