package com.cosmos.khalti.controller;

import com.cosmos.khalti.service.KhaltiService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("khalti")
@AllArgsConstructor
public class KhaltiController {

    private final KhaltiService khaltiService;

    @GetMapping("/success")
    public String success(@RequestParam("pidx") String pidx,
                          @RequestParam("transaction_id")String transactionId,
                          @RequestParam("tidx") String tIdx) {
        return "success";
    }
}
