package com.cosmos.cybersource.service;

import com.cosmos.cybersource.dto.VerifyPaymentDto;
import com.cosmos.cybersource.entity.PaymentUserEntity;
import com.cosmos.cybersource.entity.VerifyPaymentEntity;
import com.cosmos.cybersource.repo.PaymentUserRepo;
import com.cosmos.cybersource.repo.VerifyPaymentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VerifyPaymentServiceImpl implements VerifyPaymentService {

    @Autowired
    private VerifyPaymentRepo verifyPaymentRepo;

    @Autowired
    private PaymentUserRepo paymentUserRepo;

    public VerifyPaymentServiceImpl(VerifyPaymentRepo verifyPaymentRepo) {
        this.verifyPaymentRepo = verifyPaymentRepo;
    }

    @Override
    public boolean savePayment(VerifyPaymentDto verifyPaymentDto) {
        VerifyPaymentEntity verifyPaymentEntity = VerifyPaymentEntity.builder()
                .amount(verifyPaymentDto.getAmount())
                .accessKey(verifyPaymentDto.getAccess_key())
                .authTransRefNo(verifyPaymentDto.getAuth_trans_ref_no())
                .reasonCode(verifyPaymentDto.getReason_code())
                .decision(verifyPaymentDto.getDecision())
                .referenceNumber(verifyPaymentDto.getReference_number())
                .profileId(verifyPaymentDto.getProfile_id())
                .transactionUUID(verifyPaymentDto.getTransaction_uuid())
                .build();
        try {
            verifyPaymentRepo.save(verifyPaymentEntity);
            PaymentUserEntity user = paymentUserRepo.findByAuthTransRefNoAndTransactionUUID(verifyPaymentDto.getAuth_trans_ref_no(), verifyPaymentDto.getTransaction_uuid());
            if(verifyPaymentDto.getReason_code().equals("100") && user != null){
                user.setIsSuccess(true);
                paymentUserRepo.save(user);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
