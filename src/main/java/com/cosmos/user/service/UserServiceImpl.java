package com.cosmos.user.service;

import com.cosmos.admin.entity.Message;
import com.cosmos.admin.repo.MessageRepo;
import com.cosmos.astrologer.dto.AstrologerDto;
import com.cosmos.astrologer.entity.Astrologer;
import com.cosmos.astrologer.projection.AstrologerReplyProjection;
import com.cosmos.astrologer.repo.AstrologerRepo;
import com.cosmos.astrologer.repo.NepaliAnswerPoolRepo;
import com.cosmos.astrologer.service.AstrologerService;
import com.cosmos.astromode.enitity.AstroModeEntity;
import com.cosmos.astromode.repo.AstroModeRepo;
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
import com.cosmos.questionPool.entity.EnglishAnswerPool;
import com.cosmos.questionPool.entity.EnglishQuestionPool;
import com.cosmos.questionPool.projection.EnglishQuestionProjection;
import com.cosmos.questionPool.projection.EnglishReplyProjection;
import com.cosmos.questionPool.projection.NepaliQuestionProjection;
import com.cosmos.questionPool.repo.EnglishAnswerPoolRepo;
import com.cosmos.questionPool.repo.EnglishQuestionPoolRepo;
import com.cosmos.questionPool.repo.NepaliQuestionPoolRepo;
import com.cosmos.user.dto.RateDto;
import com.cosmos.user.dto.UserChangeLogDto;
import com.cosmos.user.dto.UserDto;
import com.cosmos.user.dto.UserQuestionAnswerHistory;
import com.cosmos.user.entity.User;
import com.cosmos.user.entity.UserChangeLog;
import com.cosmos.user.repo.UserChangeLogRepo;
import com.cosmos.user.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private UserChangeLogRepo userChangeLogRepo;

    @Autowired
    private AstrologerRepo astrologerRepo;

    @Autowired
    private AstroModeRepo astroModeRepo;

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
        UserChangeLog userChangeLog = new UserChangeLog();
        User user = userRepository.findByDeviceId(userDto.getDeviceId());
        if (userDto.getUserId() == 0 && user == null) {
            user = modelMapper.map(userDto, User.class);
            user.setRole(Role.ROLE_USER);
            user.setEnabled(true);
            user.setFirstLogin(true);
            user.setAccountNonLocked(true);
            userChangeLog.setPreAccurateTime(null);
            userChangeLog.setPreBirthTime(null);
            userChangeLog.setPreDateOfBirth(null);
        } else {
//            user = userRepository.findByUserId(user.getUserId());
//            if (user == null)
//                throw new CustomException("No user found under this ID!", HttpStatus.NOT_FOUND);
            userChangeLog.setPreDateOfBirth(user.getDateOfBirth());
            userChangeLog.setPreAccurateTime(user.getAccurateTime());
            userChangeLog.setPreBirthTime(user.getBirthTime());

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

        copyUserChangeLog(userChangeLog, userDto);
        userChangeLog.setUser(newUser);
        userChangeLogRepo.save(userChangeLog);
        return modelMapper.map(newUser, UserDto.class);
    }

    public UserDto findUserDetailsById(Long id) {
        User user = userRepository.findByUserId(id);
        if (user == null) {
            throw new CustomException("User not found under this id: " + id, HttpStatus.NOT_FOUND);
        }
        return modelMapper.map(user, UserDto.class);
    }

    private void copyUserChangeLog(UserChangeLog user, UserDto userDto){
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
        user.setDeviceToken(userDto.getDeviceToken());
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
            else if(prevEngQuestion.getQuestionStatus().equals("4"))
                questionAnswerHistory.setStatus("Detail_Changed");
            else
                questionAnswerHistory.setStatus("Unclear");

            questionAnswerHistory.setCreatedAt(prevEngQuestion.getCreatedAt());

            if (prevEngQuestion.getQuestionStatus().equalsIgnoreCase("2") || prevEngQuestion.getQuestionStatus().equalsIgnoreCase("3")) {
                AstrologerReplyProjection nepaliAnswer = null;
                EnglishReplyProjection englishAnswer = null;

                NepaliQuestionProjection translatedEngQuestion = nepaliQuestionPoolRepo.selectTranslatedEngQuestionByEngQuestionId(prevEngQuestion.getEngQuestionId());

                if (translatedEngQuestion != null) {
                    nepaliAnswer = nepaliAnswerPoolRepo.selectNepReplyByNepQuestionId(translatedEngQuestion.getNepQuestionId());
                }

                if (nepaliAnswer != null) {
                    englishAnswer = englishAnswerPoolRepo.selectEngReplyByNepAnswerId(nepaliAnswer.getNepAnswerId());
                }else {
                    System.out.println(prevEngQuestion.getEngQuestionId());
                    englishAnswer = englishAnswerPoolRepo.selectEngReplyByEnglishQuestionId(prevEngQuestion.getEngQuestionId());
                }

                questionAnswerHistory.setTranslatedEngQuestion(translatedEngQuestion);
                questionAnswerHistory.setNepaliAnswer(nepaliAnswer);
                questionAnswerHistory.setEnglishAnswer(englishAnswer);
            }else if (prevEngQuestion.getQuestionStatus().equalsIgnoreCase("4")){
                // set detail change history

                UserChangeLog userChangeLog = userChangeLogRepo.findById(prevEngQuestion.getEngQuestionId()).orElse(null);
                String message = String.format("User has changed date of birth  detail from %s time %s to %s time %s ",
                        userChangeLog.getPreDateOfBirth(),
                        userChangeLog.getPreBirthTime(), userChangeLog.getDateOfBirth() , userChangeLog.getBirthTime());
                questionAnswerHistory.setEngQuestion(message);
                if(userChangeLog.getPreBirthTime() == null) questionAnswerHistory = null;
            }

            if(questionAnswerHistory !=null) userQuestionAnswerHistoryList.add(questionAnswerHistory);
        }
        return userQuestionAnswerHistoryList;
    }

    public NotificationResponse sendAnswerToUserViaNotification(Long userId, String answer, Long astroId, Long questionId) {

        return  new NotificationResponse();
//        logger.info("Preparing to send notification to user...");
//        Astrologer astrologer = astrologerRepo.findById(astroId).orElse(null);
//        AstrologerDto astrologerDto = null;
//        AstroModeEntity astroModeEntity = astroModeRepo.findByUserId(astroId);
//         if (astrologer != null) {
//             astrologerDto =  modelMapper.map(astrologer, AstrologerDto.class);
//         }
//
//        NotificationDataPayload answerDataPayLoad = new NotificationDataPayload();
//        answerDataPayLoad.setEngQuestionId(questionId.toString());
//        answerDataPayLoad.setStatus("CLEAR");
//        answerDataPayLoad.setMessage(answer);
//        answerDataPayLoad.setRepliedBy(astrologer !=null? astrologer.getFirstName() + " " + astrologer.getLastName() : astroModeEntity.getFirstName() + " " + astroModeEntity.getLastName());
//        answerDataPayLoad.setProfileImgUrl(astrologer != null? astrologer.getProfileImageUrl() : astroModeEntity.getProfileImageUrl());
//
//        Notification notification = new Notification("Answer of your question", "Answer", "FLUTTER_NOTIFICATION_CLICK");
//
//        logger.info("Finding user detail to get device token...");
//        UserDto user = findUserDetailsById(userId);
//
//        // TODO make log
//        if (user.getDeviceToken() == null) {
//            logger.error("Cannot Send notification. Device Token is null...");
////			throw new CustomException("Cannot Send notification. Device Token is null", HttpStatus.SERVICE_UNAVAILABLE);
//        }
//
//
//        return notificationService.sendPushNotification(user.getDeviceToken(), notification, answerDataPayLoad);
    }

    public List<UserChangeLogDto> getAllChangeHistory(Long userId) {
        List<UserChangeLog> list = userChangeLogRepo.findAllByUserUserIdOrderByIdDesc(userId);
            return list.stream().map(userChangeLog -> {
                UserChangeLogDto userChangeLogDto = new UserChangeLogDto();
                copyChangeLog(userChangeLog, userChangeLogDto);
                return userChangeLogDto;
            }).collect(Collectors.toList());
    }

    private void copyChangeLog(UserChangeLog userDto, UserChangeLogDto user){
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setGender(userDto.getGender());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setBirthTime(userDto.getBirthTime());
        user.setAccurateTime(userDto.getAccurateTime());
        user.setCountry(userDto.getCountry());
        user.setCountryIso(userDto.getCountryIso());
        user.setState(userDto.getState());
        user.setCity(userDto.getCity());
        user.setDeviceToken(userDto.getDeviceToken());
        user.setCreatedAt(userDto.getCreatedAt());
    }

    public EnglishAnswerPool rateAnswer(RateDto rateDto) {
        AppUser user = userRepository.findByDeviceId(rateDto.getDeviceId());
        EnglishAnswerPool englishAnswerPool = englishAnswerPoolRepo.findById(rateDto.getEngAnswerId()).orElse(null);
        if (englishAnswerPool == null) { throw new CustomException("No answer found", HttpStatus.NOT_FOUND);
        }
        if(!englishAnswerPool.getUserId().equals(user.getUserId())) throw new CustomException("Wrong user id", HttpStatus.UNAUTHORIZED);
        if(null != englishAnswerPool.getRating()) throw new CustomException("Already rated", HttpStatus.ALREADY_REPORTED);
        englishAnswerPool.setRating(rateDto.getRate());
        return englishAnswerPoolRepo.save(englishAnswerPool);
    }
}
