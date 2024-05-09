package com.cosmos.cybersource.service;


import com.cosmos.cybersource.dto.CardDetailDto;
import com.cosmos.cybersource.dto.CardDetailRequestDto;

import java.util.List;

public interface CardDetailService {

    CardDetailDto saveCardDetail(CardDetailRequestDto cardDetailRequestDto);

    List<CardDetailDto>  getAllCardDetailsOfDevice(String deviceId);
}
