package com.cosmos.questionPool.entity;

public enum QuestionStatus {
    Assigned,   //  When Moderator/Astrologer fetches question then its status will be Assigned
    UnAssigned,// When user submits question and no moderator/astrologer has fetched for further processing or moderator/astrologer has skipped question then its status will be Unassigned
    Clear,
    Unclear
}
