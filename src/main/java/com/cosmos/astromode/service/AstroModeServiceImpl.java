package com.cosmos.astromode.service;

import com.cosmos.astrologer.dto.AstrologerDto;
import com.cosmos.astrologer.entity.Astrologer;
import com.cosmos.astromode.dto.AstroModeDto;
import com.cosmos.astromode.enitity.AstroModeEntity;
import com.cosmos.astromode.repo.AstroModeRepo;
import com.cosmos.common.exception.CustomException;
import com.cosmos.login.entity.Role;
import com.cosmos.user.dto.UserDto;
import com.cosmos.user.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AstroModeServiceImpl implements IAstroModeService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AstroModeRepo astroModeRepo;

    public AstroModeServiceImpl(ModelMapper modelMapper, PasswordEncoder passwordEncoder,
                                AstroModeRepo astroModeRepo) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.astroModeRepo = astroModeRepo;
    }

    @Override
    public AstroModeDto saveAstroMode(AstroModeDto user) {
        if(astroModeRepo.findByEmail(user.getEmail()) != null) {
            throw new CustomException("Astrologer and moderator  with this email exists!", HttpStatus.CONFLICT);
        }

        AstroModeEntity astrologer = modelMapper.map(user, AstroModeEntity.class);
        astrologer.setHashPassword(passwordEncoder.encode(user.getPassword()));
        //TODO: Need to change to false when mail activated.
        astrologer.setEnabled(true);
        astrologer.setAccountNonLocked(true);
        astrologer.setInitialPasswordChanged(true);
        astrologer.setRole(Role.ROlE_ASTRO_MODE);

        AstroModeEntity newAstrologer = astroModeRepo.save(astrologer);

        // Sending Verification Email
//        String fullName = astrologerDto.getFirstName() + astrologerDto.getLastName();
//        String uniqueCode = generator.generateUUID();
//        emailHtmlSender.sendVerifyEmail(astrologer.getEmail(), uniqueCode, astrologerDto.getPassword(), fullName);
//        appUserService.createPasswordResetTokenForUser(newAstrologer, uniqueCode);

        return  modelMapper.map(newAstrologer, AstroModeDto.class);
    }
}
