package com.cosmos.astrologer.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.cosmos.common.security.UniqueCodeGenerator;
import com.cosmos.email.helper.EmailHtmlSender;
import com.cosmos.astrologer.service.AstrologerService;
import com.cosmos.login.entity.Role;
import com.cosmos.login.service.impl.AppUserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cosmos.astrologer.dto.AstrologerDto;
import com.cosmos.astrologer.entity.Astrologer;
import com.cosmos.astrologer.repo.AstrologerRepo;
import com.cosmos.common.exception.CustomException;

@Service
public class AstrologerServiceImpl implements AstrologerService {
    @Autowired
    private AstrologerRepo astrologerRepo;
    @Autowired
    private AppUserServiceImpl appUserService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmailHtmlSender emailHtmlSender;
    @Autowired
    private UniqueCodeGenerator generator;

    @Override
    public AstrologerDto addAstrologer(AstrologerDto astrologerDto) {

        if(findByEmail(astrologerDto.getEmail()) != null) {
            throw new CustomException("Astrologer with this email exists!", HttpStatus.CONFLICT);
        }

        Astrologer astrologer = modelMapper.map(astrologerDto, Astrologer.class);
        astrologer.setHashPassword(passwordEncoder.encode(astrologerDto.getPassword()));
        //TODO: Need to change to false when mail activated.
        astrologer.setEnabled(true);
        astrologer.setAccountNonLocked(true);
        astrologer.setInitialPasswordChanged(true);
        astrologer.setRole(Role.ROLE_ASTROLOGER);

        Astrologer newAstrologer = astrologerRepo.save(astrologer);

        // Sending Verification Email
        String fullName = astrologerDto.getFirstName() + astrologerDto.getLastName();
        String uniqueCode = generator.generateUUID();
        emailHtmlSender.sendVerifyEmail(astrologer.getEmail(), uniqueCode, astrologerDto.getPassword(), fullName);
        appUserService.createPasswordResetTokenForUser(newAstrologer, uniqueCode);

        return  modelMapper.map(newAstrologer, AstrologerDto.class);
    }

    @Override
    public AstrologerDto updateAstrologer(Long astrologerId, AstrologerDto astrologerDto) {
        Astrologer astrologer = astrologerRepo.findByUserId(astrologerId);

        if(astrologer == null) {
            throw new CustomException("No astrologer found under this id: " + astrologerId, HttpStatus.NOT_FOUND);
        }

        astrologer.setFirstName(astrologerDto.getFirstName());
        astrologer.setLastName(astrologerDto.getLastName());
        astrologer.setProfileImageUrl(astrologerDto.getProfileImageUrl());
        astrologer.setCity(astrologerDto.getCity());
        astrologer.setCountry(astrologerDto.getCountry());
        astrologer.setState(astrologerDto.getState());
        astrologer.setGender(astrologerDto.getGender());
        astrologer.setPhoneNumber(astrologerDto.getPhoneNumber());

        AstrologerDto newAstrologer = modelMapper.map(astrologerRepo.save(astrologer),
                AstrologerDto.class);

        return newAstrologer;
    }

    @Override
    public List<AstrologerDto> findAllAstrologer() {
        List<Astrologer> astrologers = astrologerRepo.findAll();
        
        if(astrologers.isEmpty()) {
        	throw new CustomException("No astrologers found", HttpStatus.NOT_FOUND);
        }
        
        List<AstrologerDto> astrologerDtoList = astrologers.stream()
                .map(appUser -> modelMapper.map(appUser, AstrologerDto.class))
                .collect(Collectors.toList());
        return astrologerDtoList;
    }

	@Override
	public AstrologerDto findById(Long id) {
		Astrologer astrologer = astrologerRepo.findByUserId(id);
		
		if(astrologer == null) {
        	throw new CustomException("No astrologer found under this id: " + id, HttpStatus.NOT_FOUND);
        }
		
		AstrologerDto astrologerDto = modelMapper.map(astrologer, AstrologerDto.class);
		return astrologerDto;
	}

	@Override
	public void deleteAstrologerById(Long id) {
		appUserService.deleteAppUser(id);
	}

    @Override
    public Astrologer findByEmail(String email) {
        return astrologerRepo.findByEmail(email);
    }
}
