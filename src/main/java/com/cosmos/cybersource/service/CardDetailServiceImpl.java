package com.cosmos.cybersource.service;

import com.cosmos.cybersource.dto.CardDetailDto;
import com.cosmos.cybersource.dto.CardDetailRequestDto;
import com.cosmos.cybersource.entity.CardDetailEntity;
import com.cosmos.cybersource.repo.CardDetailRepo;
import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CardDetailServiceImpl implements CardDetailService {

    @Autowired
    private CardDetailRepo cardDetailRepo;

    @Autowired
    private AppUserRepo appUserRepo;

    @Autowired
    private UserRepository userRepository;


    @Override
    public CardDetailDto saveCardDetail(CardDetailRequestDto cardDetailRequestDto) {

        if(null == cardDetailRequestDto.getDeviceId()) throw new RuntimeException("card detail must not be null");
        User user=  userRepository.findByDeviceId(cardDetailRequestDto.getDeviceId());
        if(null == user && !cardDetailRequestDto.getDeviceId().equals(user.getDeviceId())) throw new RuntimeException("user must register in cosmos astrology application");

        CardDetailEntity cardDetailEntity = new CardDetailEntity();
        cardDetailEntity.setCardNumber(cardDetailRequestDto.getCardNumber());
        cardDetailEntity.setDeviceId(cardDetailRequestDto.getDeviceId());
        cardDetailEntity.setFirstName(cardDetailRequestDto.getFirstName());
        cardDetailEntity.setMiddleName(cardDetailRequestDto.getMiddleName());
        cardDetailEntity.setLastName(cardDetailRequestDto.getLastName());
        cardDetailEntity.setExiryDate(cardDetailRequestDto.getExiryDate());

        try {
            cardDetailRepo.save(cardDetailEntity);
        }catch (Exception exception){
            throw new RuntimeException("Error saving cardetis");
        }

        return new CardDetailDto("Successfully saved");
    }

    @Override
    public List<CardDetailDto> getAllCardDetailsOfDevice(String deviceId) {
        if(null == deviceId) throw new RuntimeException("device id must not be null");
        User user=  userRepository.findByDeviceId(deviceId);
        if(null == user && !deviceId.equals(user.getDeviceId())) throw new RuntimeException("user must register in cosmos astrology application");
        List<CardDetailEntity> cardDetailEntities = cardDetailRepo.findAllByDeviceIdOrderByCreatedDateDesc(deviceId);


        return cardDetailEntities.stream().map(cardDetailEntity -> {
            return CardDetailDto.builder()
                    .exiryDate(cardDetailEntity.getExiryDate())
                    .createdDate(cardDetailEntity.getCreatedDate())
                    .firstName(cardDetailEntity.getFirstName())
                    .lastName(cardDetailEntity.getFirstName())
                    .middleName(cardDetailEntity.getMiddleName())
                    .cardNumber(cardDetailEntity.getCardNumber())
                    .build();
        }).collect(Collectors.toList());
    }
}
