package com.cosmos.questionPool.projection;

public interface MonthlyRevenueReport {
    Integer getYear();
    Integer getMonth();
    Double getRevenue();
}
