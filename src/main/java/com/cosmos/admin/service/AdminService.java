package com.cosmos.admin.service;

import com.cosmos.admin.dto.DashboardDailyReport;
import com.cosmos.astrologer.projection.AstrologerWorkReport;
import com.cosmos.moderator.dto.ModeratorOrAstrologerResponse;
import com.cosmos.moderator.dto.ModeratorWorkReport;
import com.cosmos.questionPool.projection.MonthlyRevenueReport;
import com.cosmos.ritual.dto.UserRitualDto;
import com.cosmos.ritual.entity.RitualEnquiry;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface AdminService {
    ModeratorOrAstrologerResponse allResponsesByModerator(Long modId, String startDate, String endDate) throws ParseException;
    DashboardDailyReport getDailyReport();
    List<AstrologerWorkReport> getAstrologerWorkReport(String fromDate, String toDate);
    List<ModeratorWorkReport> getModeratorWorkReport(String fromDate, String toDate);
    List<MonthlyRevenueReport> getMonthlyRevenueReport(Integer year);
    List<Map<String,Object>> getAllUsersWithRitual();
    RitualEnquiry saveRitualInquiryMessage(RitualEnquiry ritual);
}
