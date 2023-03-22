package com.cosmos.admin.service;

import com.cosmos.admin.dto.DashboardDailyReport;
import com.cosmos.admin.service.AdminService;
import com.cosmos.astrologer.projection.AstrologerWorkReport;
import com.cosmos.astrologer.repo.NepaliAnswerPoolRepo;
import com.cosmos.common.exception.CustomException;
import com.cosmos.moderator.dto.ModeratorOrAstrologerResponse;
import com.cosmos.moderator.dto.ModeratorWorkReport;
import com.cosmos.moderator.dto.QsAns;
import com.cosmos.questionPool.entity.EnglishAnswerPool;
import com.cosmos.questionPool.projection.MonthlyRevenueReport;
import com.cosmos.questionPool.repo.EnglishAnswerPoolRepo;
import com.cosmos.questionPool.repo.EnglishQuestionPoolRepo;
import com.cosmos.ritual.entity.RitualEnquiry;
import com.cosmos.ritual.repo.RitualEnquiryRepo;
import com.cosmos.user.entity.User;
import com.cosmos.user.repo.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private EnglishQuestionPoolRepo englishQuestionPoolRepo;
    @Autowired
    private EnglishAnswerPoolRepo finalRespRepo;
    @Autowired
    private NepaliAnswerPoolRepo nepaliAnswerPoolRepo;
    @Autowired
    private EnglishAnswerPoolRepo englishAnswerPoolRepo;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    RitualEnquiryRepo ritualEnquiryRepo;

    @Override
    public ModeratorOrAstrologerResponse allResponsesByModerator(Long modId, String startDate, String endDate) throws ParseException {
        ModeratorOrAstrologerResponse res = new ModeratorOrAstrologerResponse();

        User user = userRepo.getUserbyId(modId);
        res.setName(user.getFirstName() + ' ' + user.getLastName());

        if (user.getRole().getAuthority().equals("ROLE_MODERATOR")) {
            res.setUserType("MODERATOR");
            /*Fetch all responses*/
            List<EnglishAnswerPool> engToNep = finalRespRepo.findAllByEngToNepQsnMod(modId);
            List<EnglishAnswerPool> nepToEng = finalRespRepo.findAllByNepToEngRepMod(modId);

            List<QsAns> qsRes = new ArrayList<>();
            /*Adding list of question converted to nepali for astrologers*/
            engToNep.stream().filter(Objects::nonNull)
                    .forEach(e -> {
                        QsAns qsAns = new QsAns();
                        qsAns.setQuestion(e.getQuestion());
                        qsAns.setAnswer(e.getAnswer());
                        qsAns.setMode("QuestionToNepaliForAstrologer");
                        qsAns.setResponse_date(e.getCreatedAt());
                        qsRes.add(qsAns);
                    });

            /*Adding list of replies converted to english for clients*/
            nepToEng.stream().filter(Objects::nonNull)
                    .forEach(e -> {
                        QsAns qsAns = new QsAns();
                        qsAns.setQuestion(e.getQuestion());
                        qsAns.setAnswer(e.getAnswer());
                        qsAns.setMode("ReplyToEngForClient");
                        qsAns.setResponse_date(e.getCreatedAt());
                        qsRes.add(qsAns);
                    });
            res.setTotal_responded_qs(qsRes.size());
            res.setQs_answers(qsRes);

        } else if (user.getRole().getAuthority().equals("ROLE_ASTROLOGER")) {
            res.setUserType("ASTROLOGER");
            /*Fetch all responses*/
            List<EnglishAnswerPool> resp = finalRespRepo.findAllByAstroId(modId);

            List<QsAns> qsRes = new ArrayList<>();
            /*Adding list of responses given by astrologers*/
            resp.stream().filter(Objects::nonNull)
                    .forEach(e -> {
                        QsAns qsAns = new QsAns();
                        qsAns.setQuestion(e.getQuestion());
                        qsAns.setAnswer(e.getAnswer());
                        qsAns.setMode("AstrogerResponse");
                        qsAns.setResponse_date(e.getCreatedAt());
                        qsRes.add(qsAns);
                    });
            res.setTotal_responded_qs(qsRes.size());
            res.setQs_answers(qsRes);
        }

        /*Date wise filtration*/
        /*If no dates are provided*/
        if (startDate == null && endDate == null) {
            return res;
        }

        /*If startDate is only provided*/
        if (startDate != null && endDate == null) {
            Date providedDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            res.getQs_answers().removeIf(m -> providedDate.after((m.getResponse_date())));
            return res;
        }

        /*If both dates are provided*/
        if (startDate != null) {
            Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);

            res.getQs_answers().removeIf(m -> start.after((m.getResponse_date())));
            res.getQs_answers().removeIf(m -> end.before(m.getResponse_date()));
            return res;
        }
        return null;
    }

    @Override
    public DashboardDailyReport getDailyReport() {

        Integer dailyQuestionCount = englishQuestionPoolRepo.selectDailyQuestionCount();
        Integer dailyAstrologerWorkCount = nepaliAnswerPoolRepo.selectDailyAstrologerWorkCount();
        Integer dailyModeratorWorkCount = finalRespRepo.selectDailyModeratorWorkCount();
        Integer dailyUnclearQuestionCount = englishQuestionPoolRepo.selectDailyUnclearQuestionCount();
        Integer dailyFreeQuestionCount = englishQuestionPoolRepo.selectDailyFreeQuestionCount();
        Double dailyRevenue = englishQuestionPoolRepo.selectDailyRevenue();

        return new DashboardDailyReport(
                dailyQuestionCount,
                dailyAstrologerWorkCount,
                dailyModeratorWorkCount,
                dailyUnclearQuestionCount,
                dailyFreeQuestionCount,
                dailyRevenue == null ? 0.0 : dailyRevenue
        );
    }

    @Override
    public List<AstrologerWorkReport> getAstrologerWorkReport(String fromDate, String toDate) {
        List<AstrologerWorkReport> reportList = englishAnswerPoolRepo.selectAstrologerWorkReport(fromDate, toDate);

        if (reportList.isEmpty()) {
            throw new CustomException("No work report found on specified date!!!", HttpStatus.NOT_FOUND);
        }

        return reportList;
    }

    @Override
    public List<ModeratorWorkReport> getModeratorWorkReport(String fromDate, String toDate) {
        List<ModeratorWorkReport> reportList = englishAnswerPoolRepo.selectModeratorWorkReport(fromDate, toDate);

        if (reportList.isEmpty()) {
            throw new CustomException("No work report found on specified date!!!", HttpStatus.NOT_FOUND);
        }

        return reportList;
    }

    @Override
    public List<MonthlyRevenueReport> getMonthlyRevenueReport(Integer year) {
        return englishQuestionPoolRepo.selectMonthlyRevenue(year);
    }

    @Override
    public List<Map<String, Object>> getAllUsersWithRitual() {
        List<Map<String, Object>> dataMap = new LinkedList<>();
        for (Map<String, Object> x : userRepo.findAllUserWithRitualInquiry()) {
            Map<String, Object> newMap = new HashMap<>(x);
            newMap.put("ritualEnquiry", ritualEnquiryRepo.findAllByEndUserId(((BigInteger) x.get("userId")).longValue()));
            dataMap.add(newMap);
        }
        return dataMap;
    }

    @Override
    public RitualEnquiry saveRitualInquiryMessage(RitualEnquiry ritual) {
        return ritualEnquiryRepo.save(ritual);
    }

}
