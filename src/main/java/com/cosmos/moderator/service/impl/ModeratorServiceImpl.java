package com.cosmos.moderator.service.impl;

import com.cosmos.astrologer.entity.NepaliAnswerPool;
import com.cosmos.astrologer.repo.NepaliAnswerPoolRepo;
import com.cosmos.common.exception.CustomException;
import com.cosmos.common.security.UniqueCodeGenerator;
import com.cosmos.email.helper.EmailHtmlSender;
import com.cosmos.login.dto.CurrentlyLoggedInUser;
import com.cosmos.login.entity.Role;
import com.cosmos.login.service.impl.AppUserServiceImpl;
import com.cosmos.moderator.dto.CurrentJobForModerator;
import com.cosmos.moderator.dto.ModeratorDto;
import com.cosmos.moderator.dto.QuestionAnswerPoolForModerator;
import com.cosmos.moderator.entity.Moderator;
import com.cosmos.moderator.repo.ModeratorRepo;
import com.cosmos.moderator.service.ModeratorService;
import com.cosmos.questionPool.entity.EnglishQuestionPool;
import com.cosmos.questionPool.repo.EnglishQuestionPoolRepo;
import com.cosmos.user.service.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ModeratorServiceImpl implements ModeratorService {
    @Autowired
    private ModeratorRepo moderatorRepo;
    @Autowired
    private AppUserServiceImpl appUserService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EnglishQuestionPoolRepo englishQuestionPoolRepo;
    @Autowired
    private NepaliAnswerPoolRepo nepaliAnswerPoolRepo;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private EmailHtmlSender emailHtmlSender;
    @Autowired
    private UniqueCodeGenerator generator;

    @Override
    public ModeratorDto addModerator(ModeratorDto moderatorDto) {

        if(moderatorRepo.findByEmail(moderatorDto.getEmail()) != null) {
            throw new CustomException("Moderator with this email exists!", HttpStatus.CONFLICT);
        }

        Moderator moderator = modelMapper.map(moderatorDto, Moderator.class);
        moderator.setHashPassword(passwordEncoder.encode(moderatorDto.getPassword()));
        moderator.setEnabled(false);
        moderator.setAccountNonLocked(true);
        moderator.setInitialPasswordChanged(true);
        moderator.setRole(Role.ROLE_MODERATOR);

        Moderator newMod = moderatorRepo.save(moderator);

        // Sending Verification Email
        String uniqueCode = generator.generateUUID();
        String fullName = moderatorDto.getFirstName() + moderatorDto.getLastName();
        emailHtmlSender.sendVerifyEmail(moderator.getEmail(), uniqueCode, moderatorDto.getPassword(), fullName);
        appUserService.createPasswordResetTokenForUser(newMod, uniqueCode);

        return modelMapper.map(newMod, ModeratorDto.class);
    }

    @Override
    public ModeratorDto updateModerator(Long moderatorId, ModeratorDto moderatorDto) {
        Moderator moderator = moderatorRepo.findByUserId(moderatorId);
        if (moderator == null) {
            throw new CustomException("No moderator found under this id: " + moderatorId, HttpStatus.NOT_FOUND);
        }

        moderator.setFirstName(moderatorDto.getFirstName());
        moderator.setLastName(moderatorDto.getLastName());
        moderator.setCity(moderatorDto.getCity());
        moderator.setCountry(moderatorDto.getCountry());
        moderator.setState(moderatorDto.getState());
        moderator.setGender(moderatorDto.getGender());
        moderator.setPhoneNumber(moderatorDto.getPhoneNumber());
        moderator.setProfileImageUrl(moderatorDto.getProfileImageUrl());

        return modelMapper.map(moderatorRepo.save(moderator),
                ModeratorDto.class);
    }

    @Override
    public List<ModeratorDto> findAllModerator() {
        List<Moderator> moderators = moderatorRepo.findAll();
        if (moderators.isEmpty()) {
            throw new CustomException("No moderators found", HttpStatus.NOT_FOUND);
        }
        return moderators.stream()
                .map(appUser -> modelMapper.map(appUser, ModeratorDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ModeratorDto findModeratorById(Long id) {
        Moderator moderator = moderatorRepo.findByUserId(id);
        if (moderator == null) {
            throw new CustomException("No moderator found under this id: " + id, HttpStatus.NOT_FOUND);
        }
        return modelMapper.map(moderator, ModeratorDto.class);
    }

    @Override
    public void deleteModeratorById(Long id) {
        appUserService.deleteAppUser(id);
    }

    @Override
    public QuestionAnswerPoolForModerator findModeratorUnfinishedTask(){
        QuestionAnswerPoolForModerator questionAnswerPoolForModerator = new QuestionAnswerPoolForModerator();
        CurrentJobForModerator currentJobForModerator = new CurrentJobForModerator();
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        NepaliAnswerPool nepaliAnswerPool = nepaliAnswerPoolRepo.selectModeratorUnfinishedAnswer(currentlyLoggedInUser.getCurrentlyLoggedInUserId());
        if(nepaliAnswerPool != null){
            currentJobForModerator.setCurrentJobType("nepali-answer");
            currentJobForModerator.setNepaliAnswer(nepaliAnswerPool);

            questionAnswerPoolForModerator.setCurrentJob(currentJobForModerator);
            questionAnswerPoolForModerator.setUserDetails(userService.findUserDetailsById(nepaliAnswerPool.getUserId()));
            questionAnswerPoolForModerator.setQuestionAnswerHistoryList(userService.findPrevQuestionHistoryOfUser(nepaliAnswerPool.getUserId()));
            return questionAnswerPoolForModerator;
        }

        EnglishQuestionPool englishQuestionPool = englishQuestionPoolRepo.selectModeratorUnfinishedQuestion(currentlyLoggedInUser.getCurrentlyLoggedInUserId());
        if(englishQuestionPool != null){
            currentJobForModerator.setCurrentJobType("english-question");
            currentJobForModerator.setEnglishQuestion(englishQuestionPool);

            questionAnswerPoolForModerator.setCurrentJob(currentJobForModerator);
            questionAnswerPoolForModerator.setUserDetails(userService.findUserDetailsById(englishQuestionPool.getUserId()));
            questionAnswerPoolForModerator.setQuestionAnswerHistoryList(userService.findPrevQuestionHistoryOfUser(englishQuestionPool.getUserId()));
            return questionAnswerPoolForModerator;
        }

        return null;
    }
}
