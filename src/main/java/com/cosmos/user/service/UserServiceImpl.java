package com.cosmos.user.service;

import com.cosmos.admin.entity.Message;
import com.cosmos.admin.repo.MessageRepo;
import com.cosmos.astrologer.dto.AstrologerDto;
import com.cosmos.astrologer.projection.AstrologerReplyProjection;
import com.cosmos.astrologer.repo.NepaliAnswerPoolRepo;
import com.cosmos.astrologer.service.AstrologerService;
import com.cosmos.common.exception.CustomException;
import com.cosmos.common.security.JwtTokenProvider;
import com.cosmos.credit.entity.Credit;
import com.cosmos.credit.service.CreditServiceImpl;
import com.cosmos.login.entity.AppUser;
import com.cosmos.login.entity.Role;
import com.cosmos.login.service.impl.AppUserServiceImpl;
import com.cosmos.notification.model.Notification;
import com.cosmos.notification.model.NotificationDataPayload;
import com.cosmos.notification.model.NotificationResponse;
import com.cosmos.notification.service.NotificationService;
import com.cosmos.questionPool.projection.EnglishQuestionProjection;
import com.cosmos.questionPool.projection.EnglishReplyProjection;
import com.cosmos.questionPool.projection.NepaliQuestionProjection;
import com.cosmos.questionPool.repo.EnglishAnswerPoolRepo;
import com.cosmos.questionPool.repo.EnglishQuestionPoolRepo;
import com.cosmos.questionPool.repo.NepaliQuestionPoolRepo;
import com.cosmos.user.dto.UserDto;
import com.cosmos.user.dto.UserQuestionAnswerHistory;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AppUserServiceImpl appUserService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private EnglishQuestionPoolRepo englishQuestionPoolRepo;
    @Autowired
    private NepaliQuestionPoolRepo nepaliQuestionPoolRepo;
    @Autowired
    private EnglishAnswerPoolRepo englishAnswerPoolRepo;
    @Autowired
    private NepaliAnswerPoolRepo nepaliAnswerPoolRepo;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private AstrologerService astrologerService;

    @Autowired
    private CreditServiceImpl creditService;

    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Deprecated
    public List<String> getWelcomeMessages(String messageType) {
        List<Message> messages = messageRepo.selectMessageByTypeAndSendMessage(messageType);
        return messages
                .stream()
                .map(Message::getText)
                .collect(Collectors.toList());
    }

    public List<Map<String,Object>> getInitialMessages(String messageType) {
        return messageRepo.fetchMessageByTypeAndSendMessage(messageType);
    }

    public List<UserDto> fetchAllEndUser() {
        return userRepository.getAllUserByUserType("user").stream().map(data -> modelMapper.map(data, UserDto.class)).collect(Collectors.toList());
    }

    public UserDto processUserRegistration(UserDto userDto) {
        User user = userRepository.findByDeviceId(userDto.getDeviceId());
        if (userDto.getUserId() == 0 && user == null) {
            user = modelMapper.map(userDto, User.class);
            user.setRole(Role.ROLE_USER);
            user.setEnabled(true);
            user.setFirstLogin(true);
            user.setAccountNonLocked(true);
        } else {
//            user = userRepository.findByUserId(user.getUserId());
//            if (user == null)
//                throw new CustomException("No user found under this ID!", HttpStatus.NOT_FOUND);

            user.setFirstName(userDto.getFirstName());
            user.setLastName(userDto.getLastName());
            user.setGender(userDto.getGender());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setDateOfBirth(userDto.getDateOfBirth());
            user.setBirthTime(userDto.getBirthTime());
            user.setAccurateTime(userDto.isAccurateTime());
            user.setCountry(userDto.getCountry());
            user.setCountryIso(userDto.getCountryIso());
            user.setState(userDto.getState());
            user.setCity(userDto.getCity());
            user.setProfileImageUrl(userDto.getProfileImageUrl());
            user.setDeviceToken(userDto.getDeviceToken());
            user.setFirstLogin(false);
        }

        User newUser = userRepository.save(user);
        if (userDto.getUserId() == 0) {
            //Grand Credit to new user.
            Credit credit = new Credit();
            credit.setEndUserId(newUser.getUserId());
            creditService.grantCreditToEndUser(credit);
        }
        return modelMapper.map(newUser, UserDto.class);
    }

    public UserDto findUserDetailsById(Long id) {
        User user = userRepository.findByUserId(id);
        if (user == null) {
            throw new CustomException("User not found under this id: " + id, HttpStatus.NOT_FOUND);
        }
        return modelMapper.map(user, UserDto.class);
    }

    public UserDto findUserDetailsByDeviceId(String deviceId) {
        User user = userRepository.findByDeviceId(deviceId);
        if (user == null) {
            throw new CustomException("User not found under this id: " + deviceId, HttpStatus.NOT_FOUND);
        }
        return modelMapper.map(user, UserDto.class);
    }

    public void subscribeUnsubscribeExpressPackage(String deviceId, boolean subscription) {
        User user = userRepository.findByDeviceId(deviceId);
        if (user == null) {
            throw new CustomException("User not found under this id: " + deviceId, HttpStatus.NOT_FOUND);
        }
        user.setSubscription(subscription);
        userRepository.save(user);
    }

    public List<UserQuestionAnswerHistory> findPrevQuestionHistoryOfUser(Long userId) {
        List<UserQuestionAnswerHistory> userQuestionAnswerHistoryList = new ArrayList<>();
        List<EnglishQuestionProjection> prevEngQuestionList = englishQuestionPoolRepo.selectPrevEngQuestionOfUser(userId);
        for (EnglishQuestionProjection prevEngQuestion : prevEngQuestionList) {

            UserQuestionAnswerHistory questionAnswerHistory = new UserQuestionAnswerHistory();
            questionAnswerHistory.setEngQuestion(prevEngQuestion.getEngQuestion());

            if (prevEngQuestion.getQuestionStatus().equals("0"))
                questionAnswerHistory.setStatus("Assigned");

            else if (prevEngQuestion.getQuestionStatus().equals("2"))
                questionAnswerHistory.setStatus("Clear");

            else
                questionAnswerHistory.setStatus("Unclear");

            questionAnswerHistory.setCreatedAt(prevEngQuestion.getCreatedAt());

            if (prevEngQuestion.getQuestionStatus().equalsIgnoreCase("2")) {
                AstrologerReplyProjection nepaliAnswer = null;
                EnglishReplyProjection englishAnswer = null;

                NepaliQuestionProjection translatedEngQuestion = nepaliQuestionPoolRepo.selectTranslatedEngQuestionByEngQuestionId(prevEngQuestion.getEngQuestionId());

                if (translatedEngQuestion != null) {
                    nepaliAnswer = nepaliAnswerPoolRepo.selectNepReplyByNepQuestionId(translatedEngQuestion.getNepQuestionId());
                }

                if (nepaliAnswer != null) {
                    englishAnswer = englishAnswerPoolRepo.selectEngReplyByNepAnswerId(nepaliAnswer.getNepAnswerId());
                }

                questionAnswerHistory.setTranslatedEngQuestion(translatedEngQuestion);
                questionAnswerHistory.setNepaliAnswer(nepaliAnswer);
                questionAnswerHistory.setEnglishAnswer(englishAnswer);
            }

            userQuestionAnswerHistoryList.add(questionAnswerHistory);
        }
        return userQuestionAnswerHistoryList;
    }

    public NotificationResponse sendAnswerToUserViaNotification(Long userId, String answer, Long astroId, Long questionId) {
        logger.info("Preparing to send notification to user...");

        AstrologerDto astrologerDetails = astrologerService.findById(astroId);

        NotificationDataPayload answerDataPayLoad = new NotificationDataPayload();
        answerDataPayLoad.setEngQuestionId(questionId.toString());
        answerDataPayLoad.setStatus("CLEAR");
        answerDataPayLoad.setMessage(answer);
        answerDataPayLoad.setRepliedBy(astrologerDetails.getFirstName() + " " + astrologerDetails.getLastName());
        answerDataPayLoad.setProfileImgUrl(astrologerDetails.getProfileImageUrl());

        Notification notification = new Notification("Answer of your question", "Answer", "FLUTTER_NOTIFICATION_CLICK");

        logger.info("Finding user detail to get device token...");
        UserDto user = findUserDetailsById(userId);

        // TODO make log
        if (user.getDeviceToken() == null) {
            logger.error("Cannot Send notification. Device Token is null...");
//			throw new CustomException("Cannot Send notification. Device Token is null", HttpStatus.SERVICE_UNAVAILABLE);
        }


        return notificationService.sendPushNotification(user.getDeviceToken(), notification, answerDataPayLoad);
    }
}
