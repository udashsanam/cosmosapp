package com.cosmos.cybersource.controller;

import com.cosmos.cybersource.dto.CardDetailDto;
import com.cosmos.cybersource.dto.CardDetailRequestDto;
import com.cosmos.cybersource.service.CardDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/card-detail")
public class CardDetailController {

    private final CardDetailService cardDetailService;

    public CardDetailController(CardDetailService cardDetailService) {
        this.cardDetailService = cardDetailService;
    }


    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody CardDetailRequestDto cardDetailRequestDto){
        CardDetailDto cardDetailDto = cardDetailService.saveCardDetail(cardDetailRequestDto);
        return ResponseEntity.ok(cardDetailDto);
    }

    @GetMapping("/get-all-card-detail")
    public ResponseEntity<?> getAll(@RequestParam("deviceId") String deviceId){
        List<CardDetailDto> cardDetailDtos = cardDetailService.getAllCardDetailsOfDevice(deviceId);
        return ResponseEntity.ok(cardDetailDtos);
    }
}
