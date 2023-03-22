package com.cosmos.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDailyReport {
    private Integer dailyQuestionCount;
    private Integer dailyAstrologerWorkCount;
    private Integer dailyModeratorWorkCount;
    private Integer dailyUnclearQuestionCount;
    private Integer dailyFreeQuestionCount;
    private Double dailyRevenue;
}
