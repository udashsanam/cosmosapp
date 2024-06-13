package com.cosmos.astromode.service;


import com.cosmos.astrologer.entity.NepaliAnswerPool;
import com.cosmos.astrologer.repo.NepaliAnswerPoolRepo;
import com.cosmos.astromode.dto.AstroModeDto;
import com.cosmos.astromode.dto.AstroModeReplyToUser;
import com.cosmos.astromode.enitity.AstroModeEntity;
import com.cosmos.astromode.repo.AstroModeRepo;
import com.cosmos.common.exception.CustomException;
import com.cosmos.login.dto.CurrentlyLoggedInUser;
import com.cosmos.login.entity.Role;
import com.cosmos.moderator.dto.CurrentJobForModerator;
import com.cosmos.moderator.dto.ModeratorDto;
import com.cosmos.moderator.dto.QuestionAnswerPoolForModerator;
import com.cosmos.moderator.entity.Moderator;
import com.cosmos.moderator.service.ModeratorService;
import com.cosmos.notification.model.NotificationResponse;
import com.cosmos.questionPool.entity.EnglishAnswerPool;
import com.cosmos.questionPool.entity.EnglishQuestionPool;
import com.cosmos.questionPool.entity.QuestionStatus;
import com.cosmos.questionPool.repo.EnglishAnswerPoolRepo;
import com.cosmos.questionPool.repo.EnglishQuestionPoolRepo;
import com.cosmos.questionPool.service.EnglishQuestionPoolService;
import com.cosmos.user.dto.UserDto;
import com.cosmos.user.entity.User;
import com.cosmos.user.service.UserServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AstroModeServiceImpl implements IAstroModeService {

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AstroModeRepo astroModeRepo;
    private final EnglishAnswerPoolRepo englishAnswerPoolRepo;
    private final UserServiceImpl userService;
    private final EnglishQuestionPoolRepo englishQuestionPoolRepo;
    private final NepaliAnswerPoolRepo nepaliAnswerPoolRepo;
    private final ModeratorService moderatorService;
    private final EnglishQuestionPoolService englishQuestionPoolService;

    public AstroModeServiceImpl(ModelMapper modelMapper, PasswordEncoder passwordEncoder,
                                AstroModeRepo astroModeRepo, EnglishAnswerPoolRepo englishAnswerPoolRepo,
                                UserServiceImpl userService, EnglishQuestionPoolRepo englishQuestionPoolRepo,
                                NepaliAnswerPoolRepo nepaliAnswerPoolRepo, ModeratorService moderatorService,
                                EnglishQuestionPoolService englishQuestionPoolService) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.astroModeRepo = astroModeRepo;
        this.englishAnswerPoolRepo = englishAnswerPoolRepo;
        this.userService = userService;
        this.englishQuestionPoolRepo = englishQuestionPoolRepo;
        this.nepaliAnswerPoolRepo = nepaliAnswerPoolRepo;
        this.moderatorService = moderatorService;
        this.englishQuestionPoolService = englishQuestionPoolService;
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

    @Override
    public AstroModeDto updateModerator(Long astroModeId, AstroModeDto astroModeDto) {

        AstroModeEntity moderator = astroModeRepo.findByUserId(astroModeId);
        if (moderator == null) {
            throw new CustomException("No Astro moderator  found under this id: " + astroModeId, HttpStatus.NOT_FOUND);
        }

        moderator.setFirstName(astroModeDto.getFirstName());
        moderator.setLastName(astroModeDto.getLastName());
        moderator.setCity(astroModeDto.getCity());
        moderator.setCountry(astroModeDto.getCountry());
        moderator.setState(astroModeDto.getState());
        moderator.setGender(astroModeDto.getGender());
        moderator.setPhoneNumber(astroModeDto.getPhoneNumber());
        moderator.setProfileImageUrl(astroModeDto.getProfileImageUrl());

        return modelMapper.map(astroModeRepo.save(moderator),
                AstroModeDto.class);
    }

    @Override
    public AstroModeDto findModeratorById(Long id) {
        return modelMapper.map(astroModeRepo.findByUserId(id), AstroModeDto.class);
    }

    @Override
    public void deleteModeratorById(Long id) {

    }


    @Override
    public EnglishAnswerPool saveFinalAnswer(AstroModeReplyToUser astroModeReplyToUser, Long userId) {

        /*fetch question from english question pool*/
        EnglishQuestionPool engQsn = englishQuestionPoolRepo.getOne(astroModeReplyToUser.getEngQuesId());
        /*check if this question has already an entry*/
        EnglishAnswerPool queryWithReply = englishAnswerPoolRepo.getOneByQuestionId(astroModeReplyToUser.getEngQuesId());
        if (queryWithReply == null) {
            EnglishAnswerPool qwr = new EnglishAnswerPool();
            qwr.setQuestionId(queryWithReply.getQuestionId());
            qwr.setQuestion(engQsn.getEngQuestion());
            qwr.setAnswer(astroModeReplyToUser.getTranslatedAns());

            qwr.setUserId(engQsn.getUserId());
            qwr.setAstroModId(getCurrentUserId());
            qwr.setEngToNepQsnMod(engQsn.getAssignedModId());
            qwr.setNepToEngRepMod(getCurrentUserId());

            qwr.setCreatedAt(new Date());
            qwr.setUpdatedAt(new Date());

            NotificationResponse notificationResponse = userService.sendAnswerToUserViaNotification(astroModeReplyToUser.getUserId(),
                    astroModeReplyToUser.getTranslatedAns(), astroModeReplyToUser.getAstroModeratorId(), astroModeReplyToUser.getEngQuesId());

            qwr.setSentStatus(notificationResponse.isSuccess());
            qwr.setMessageId(notificationResponse.getMessageId());
            qwr.setFailureMsg(notificationResponse.getFailureMsg());

            return englishAnswerPoolRepo.save(qwr);
        } else {
            queryWithReply.setAnswer(astroModeReplyToUser.getTranslatedAns());
            queryWithReply.setUpdatedAt(new Date());
            return englishAnswerPoolRepo.save(queryWithReply);
        }    }

    @Override
    public QuestionAnswerPoolForModerator fetchAstroModeCurrentJob() {
        QuestionAnswerPoolForModerator questionAnswerPoolForModerator = new QuestionAnswerPoolForModerator();

        CurrentJobForModerator currentJobForModerator = new CurrentJobForModerator();

        // First Find previously unfinished task. i.e if moderator has not submitted
        //
        //todo: create for astro moderator
        QuestionAnswerPoolForModerator previousAssignedTask = moderatorService.findAstroModeratorUnfinishedTask();
        if (previousAssignedTask != null) {
            return previousAssignedTask;
        }

        // find if there is unassigned astrologer reply
        NepaliAnswerPool nepaliAnswerPool = nepaliAnswerPoolRepo.getUnassignedQA();
        if (nepaliAnswerPool != null) {
            // todo: assigme to new user type astro moderator
            System.out.println("inside nepali answer pool != null");
            CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            nepaliAnswerPool.setStatus(QuestionStatus.Assigned);
            nepaliAnswerPool.setModeratorId(currentlyLoggedInUser.getCurrentlyLoggedInUserId());
            nepaliAnswerPoolRepo.save(nepaliAnswerPool);

            currentJobForModerator.setCurrentJobType("nepali-answer");
            currentJobForModerator.setNepaliAnswer(nepaliAnswerPool);

            questionAnswerPoolForModerator.setCurrentJob(currentJobForModerator);
            questionAnswerPoolForModerator.setUserDetails(userService.findUserDetailsById(nepaliAnswerPool.getUserId()));
            questionAnswerPoolForModerator.setQuestionAnswerHistoryList(userService.findPrevQuestionHistoryOfUser(nepaliAnswerPool.getUserId()));
            return questionAnswerPoolForModerator;
        }

        // Otherwise find unassigned question and return to moderator and mark this question dirty
        EnglishQuestionPool englishQuestionPool = englishQuestionPoolService.findTopUnAssignedQuestionFromPool();
        englishQuestionPoolService.assignToAstroModerator(englishQuestionPool);

        currentJobForModerator.setCurrentJobType("english-question");
        currentJobForModerator.setEnglishQuestion(englishQuestionPool);

        questionAnswerPoolForModerator.setCurrentJob(currentJobForModerator);
        questionAnswerPoolForModerator.setUserDetails(userService.findUserDetailsById(englishQuestionPool.getUserId()));
        questionAnswerPoolForModerator.setQuestionAnswerHistoryList(userService.findPrevQuestionHistoryOfUser(englishQuestionPool.getUserId()));
        return questionAnswerPoolForModerator;
    }

    private Long getCurrentUserId() {
        CurrentlyLoggedInUser currentlyLoggedInUser = (CurrentlyLoggedInUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return currentlyLoggedInUser.getCurrentlyLoggedInUserId();
    }
}
