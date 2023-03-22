package com.cosmos.astrologer.service;

import com.cosmos.astrologer.dto.AstrologerDto;
import com.cosmos.astrologer.entity.Astrologer;

import java.util.List;

public interface AstrologerService {
    AstrologerDto addAstrologer(AstrologerDto astrologerDto);
    AstrologerDto updateAstrologer(Long astrologerId,AstrologerDto astrologerDto);
    List<AstrologerDto> findAllAstrologer();
    AstrologerDto findById(Long id);
    void deleteAstrologerById(Long id);
    Astrologer findByEmail(String email);
}
