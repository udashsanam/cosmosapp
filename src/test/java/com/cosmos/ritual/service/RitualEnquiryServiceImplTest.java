package com.cosmos.ritual.service;

import com.cosmos.ritual.entity.RitualEnquiry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class RitualEnquiryServiceImplTest {

    @Autowired
    RitualEnquiryServiceImpl ritualEnquiryService;
    private EntityManager entityManager= mock(EntityManager.class);



    @Test
    public void test(){
            System.out.println("sdflkh");
            when(entityManager.merge(new RitualEnquiry())).thenReturn(new RitualEnquiry());
            ritualEnquiryService.replyRitualEnquiry(new RitualEnquiry());
    }



}