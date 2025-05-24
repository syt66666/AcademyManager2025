package com.ruoyi.system.domain;

public class SemesterStatistics {
    private Integer competitionCount;
    private Integer activityCount;
    private Integer mentorshipCount;
    private Integer lectureCount;

    // 无参构造方法、Getter 和 Setter
    public SemesterStatistics() {}

    public Integer getCompetitionCount() { return competitionCount; }
    public void setCompetitionCount(Integer competitionCount) { this.competitionCount = competitionCount; }

    public Integer getActivityCount() { return activityCount; }
    public void setActivityCount(Integer activityCount) { this.activityCount = activityCount; }

    public Integer getMentorshipCount() { return mentorshipCount; }
    public void setMentorshipCount(Integer mentorshipCount) { this.mentorshipCount = mentorshipCount; }

    public Integer getLectureCount() { return lectureCount; }
    public void setLectureCount(Integer lectureCount) { this.lectureCount = lectureCount; }
}
