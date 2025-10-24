package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.Courses;
import com.ruoyi.system.mapper.CoursesMapper;
import com.ruoyi.system.service.ICourseStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 课程统计服务实现
 */
@Service
public class CourseStatisticsServiceImpl implements ICourseStatisticsService {

    @Autowired
    private CoursesMapper coursesMapper;

    @Override
    public Map<String, Object> getCourseOverview() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取总课程数
        Courses queryCourse = new Courses();
        List<Courses> allCourses = coursesMapper.selectCoursesList(queryCourse);
        int totalCourses = allCourses.size();
        
        // 计算不同状态的课程数
        Date now = new Date();
        int notStartedCourses = 0;
        int enrollingCourses = 0;
        int enrollmentEndedCourses = 0;
        int ongoingCourses = 0;
        int completedCourses = 0;
        int totalEnrollments = 0;
        
        for (Courses course : allCourses) {
            String status = getCourseStatus(course, now);
            switch (status) {
                case "not-started":
                    notStartedCourses++;
                    break;
                case "enrolling":
                    enrollingCourses++;
                    break;
                case "enrollment-ended":
                    enrollmentEndedCourses++;
                    break;
                case "ongoing":
                    ongoingCourses++;
                    break;
                case "completed":
                    completedCourses++;
                    break;
            }
            // courseCapacity 就是选课人数
            Long enrollments = course.getCourseCapacity() != null ? course.getCourseCapacity() : 0L;
            totalEnrollments += enrollments.intValue();
        }
        
        result.put("totalCourses", totalCourses);
        result.put("notStartedCourses", notStartedCourses);
        result.put("enrollingCourses", enrollingCourses);
        result.put("enrollmentEndedCourses", enrollmentEndedCourses);
        result.put("ongoingCourses", ongoingCourses);
        result.put("completedCourses", completedCourses);
        result.put("totalEnrollments", totalEnrollments);
        
        return result;
    }

    @Override
    public Map<String, Object> getCollegeCourseStats() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> collegeData = new HashMap<>();
        
        // 获取所有课程
        Courses queryCourse = new Courses();
        List<Courses> allCourses = coursesMapper.selectCoursesList(queryCourse);
        Date now = new Date();
        
        // 按书院统计
        Map<String, List<Courses>> coursesByCollege = new HashMap<>();
        for (Courses course : allCourses) {
            String organizer = course.getOrganizer();
            if (organizer != null && !organizer.isEmpty()) {
                coursesByCollege.computeIfAbsent(organizer, k -> new ArrayList<>()).add(course);
            }
        }
        
        // 计算各书院统计
        for (Map.Entry<String, List<Courses>> entry : coursesByCollege.entrySet()) {
            String college = entry.getKey();
            List<Courses> collegeCourses = entry.getValue();
            
            int totalCourses = collegeCourses.size();
            int enrollingCourses = 0;
            int totalEnrollments = 0;
            
            for (Courses course : collegeCourses) {
                String status = getCourseStatus(course, now);
                if ("enrolling".equals(status)) {
                    enrollingCourses++;
                }
                // courseCapacity 就是选课人数
                Long enrollments = course.getCourseCapacity() != null ? course.getCourseCapacity() : 0L;
                totalEnrollments += enrollments.intValue();
            }
            
            Map<String, Object> collegeStats = new HashMap<>();
            collegeStats.put("totalCourses", totalCourses);
            collegeStats.put("enrollingCourses", enrollingCourses);
            collegeStats.put("enrollments", totalEnrollments);
            
            collegeData.put(college, collegeStats);
        }
        
        result.put("collegeData", collegeData);
        return result;
    }

    @Override
    public Map<String, Object> getCourseTypeDistribution() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Integer> typeDistribution = new HashMap<>();
        
        // 获取所有课程
        Courses queryCourse = new Courses();
        List<Courses> allCourses = coursesMapper.selectCoursesList(queryCourse);
        
        // 统计课程类型分布
        for (Courses course : allCourses) {
            Long courseType = course.getCourseType();
            if (courseType != null) {
                // 将数据库中的数字分类映射到标准分类名称
                String mappedCategory = mapCourseType(courseType);
                typeDistribution.put(mappedCategory, typeDistribution.getOrDefault(mappedCategory, 0) + 1);
            }
        }
        
        result.put("typeDistribution", typeDistribution);
        return result;
    }

    @Override
    public Map<String, Object> getRecentCourses() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取最近创建的课程，按创建时间降序排列
        Courses queryCourse = new Courses();
        List<Courses> allCourses = coursesMapper.selectCoursesList(queryCourse);
        
        // 按创建时间排序
        allCourses.sort((a, b) -> {
            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        
        // 取前10个
        List<Map<String, Object>> recentCourses = new ArrayList<>();
        for (int i = 0; i < Math.min(10, allCourses.size()); i++) {
            Courses course = allCourses.get(i);
            Map<String, Object> courseItem = new HashMap<>();
            courseItem.put("id", course.getCourseId());
            courseItem.put("name", course.getCourseName());
            courseItem.put("college", course.getOrganizer());
            courseItem.put("type", course.getCourseCategory());
            courseItem.put("date", course.getCreatedAt());
            // courseCapacity 就是选课人数
            Long enrollments = course.getCourseCapacity() != null ? course.getCourseCapacity() : 0L;
            courseItem.put("enrollments", enrollments);
            courseItem.put("status", getCourseStatus(course, new Date()));
            
            recentCourses.add(courseItem);
        }
        
        result.put("recentCourses", recentCourses);
        return result;
    }

    @Override
    public Map<String, Object> getCourseStatsByCollege(String college) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取指定书院的课程
        Courses queryCourse = new Courses();
        queryCourse.setOrganizer(college);
        List<Courses> collegeCourses = coursesMapper.selectCoursesList(queryCourse);
        
        int totalCourses = collegeCourses.size();
        Date now = new Date();
        int notStartedCourses = 0;
        int enrollingCourses = 0;
        int enrollmentEndedCourses = 0;
        int ongoingCourses = 0;
        int completedCourses = 0;
        int totalEnrollments = 0;
        
        for (Courses course : collegeCourses) {
            String status = getCourseStatus(course, now);
            switch (status) {
                case "not-started":
                    notStartedCourses++;
                    break;
                case "enrolling":
                    enrollingCourses++;
                    break;
                case "enrollment-ended":
                    enrollmentEndedCourses++;
                    break;
                case "ongoing":
                    ongoingCourses++;
                    break;
                case "completed":
                    completedCourses++;
                    break;
            }
            // courseCapacity 就是选课人数
            Long enrollments = course.getCourseCapacity() != null ? course.getCourseCapacity() : 0L;
            totalEnrollments += enrollments.intValue();
        }
        
        result.put("totalCourses", totalCourses);
        result.put("notStartedCourses", notStartedCourses);
        result.put("enrollingCourses", enrollingCourses);
        result.put("enrollmentEndedCourses", enrollmentEndedCourses);
        result.put("ongoingCourses", ongoingCourses);
        result.put("completedCourses", completedCourses);
        result.put("totalEnrollments", totalEnrollments);
        
        // 获取该书院的课程类型分布
        Map<String, Integer> typeDistribution = new HashMap<>();
        for (Courses course : collegeCourses) {
            Long courseType = course.getCourseType();
            if (courseType != null) {
                // 将数据库中的数字分类映射到标准分类名称
                String mappedCategory = mapCourseType(courseType);
                typeDistribution.put(mappedCategory, typeDistribution.getOrDefault(mappedCategory, 0) + 1);
            }
        }
        result.put("typeDistribution", typeDistribution);
        
        // 获取该书院的最近课程
        collegeCourses.sort((a, b) -> {
            if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
            if (a.getCreatedAt() == null) return 1;
            if (b.getCreatedAt() == null) return -1;
            return b.getCreatedAt().compareTo(a.getCreatedAt());
        });
        
        List<Map<String, Object>> recentCourses = new ArrayList<>();
        for (int i = 0; i < Math.min(10, collegeCourses.size()); i++) {
            Courses course = collegeCourses.get(i);
            Map<String, Object> courseItem = new HashMap<>();
            courseItem.put("id", course.getCourseId());
            courseItem.put("name", course.getCourseName());
            courseItem.put("college", course.getOrganizer());
            courseItem.put("type", course.getCourseCategory());
            courseItem.put("date", course.getCreatedAt());
            // courseCapacity 就是选课人数
            Long enrollments = course.getCourseCapacity() != null ? course.getCourseCapacity() : 0L;
            courseItem.put("enrollments", enrollments);
            courseItem.put("status", getCourseStatus(course, now));
            
            recentCourses.add(courseItem);
        }
        result.put("recentCourses", recentCourses);
        
        return result;
    }

    @Override
    public Map<String, Object> getCollegeCourses(String college) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取指定书院的课程
        Courses queryCourse = new Courses();
        queryCourse.setOrganizer(college);
        List<Courses> collegeCourses = coursesMapper.selectCoursesList(queryCourse);
        
        List<Map<String, Object>> courses = new ArrayList<>();
        Date now = new Date();
        
        for (Courses course : collegeCourses) {
            Map<String, Object> courseItem = new HashMap<>();
            courseItem.put("id", course.getCourseId());
            courseItem.put("name", course.getCourseName());
            courseItem.put("type", course.getCourseCategory());
            courseItem.put("location", course.getCourseLocation());
            courseItem.put("capacity", course.getCourseTotalCapacity());
            // courseCapacity 就是选课人数
            Long enrollments = course.getCourseCapacity() != null ? course.getCourseCapacity() : 0L;
            courseItem.put("enrollments", enrollments);
            courseItem.put("status", getCourseStatus(course, now));
            courseItem.put("startTime", course.getStartTime());
            courseItem.put("endTime", course.getEndTime());
            courseItem.put("createdAt", course.getCreatedAt());
            
            courses.add(courseItem);
        }
        
        result.put("courses", courses);
        result.put("total", courses.size());
        
        return result;
    }

    /**
     * 获取课程状态
     */
    private String getCourseStatus(Courses course, Date now) {
        if (course.getCourseStart() == null || course.getCourseDeadline() == null || 
            course.getStartTime() == null || course.getEndTime() == null) {
            return "unknown";
        }
        
        // 选课未开始
        if (now.before(course.getCourseStart())) {
            return "not-started";
        }
        // 选课进行中
        else if (now.after(course.getCourseStart()) && now.before(course.getCourseDeadline())) {
            return "enrolling";
        }
        // 选课已结束，课程未开始
        else if (now.after(course.getCourseDeadline()) && now.before(course.getStartTime())) {
            return "enrollment-ended";
        }
        // 课程进行中
        else if (now.after(course.getStartTime()) && now.before(course.getEndTime())) {
            return "ongoing";
        }
        // 课程已结束
        else if (now.after(course.getEndTime())) {
            return "completed";
        }
        
        return "unknown";
    }

    /**
     * 将数据库中的课程类型数字映射到标准分类名称
     */
    private String mapCourseType(Long courseType) {
        if (courseType == null) {
            return "其他";
        }
        
        // 根据数字分类映射到标准分类名称
        switch (courseType.intValue()) {
            case 1:
                return "人格塑造与价值引领课程类";
            case 2:
                return "知识融合与思维进阶课程类";
            case 3:
                return "能力锻造与实践创新课程类";
            case 4:
                return "社会责任与领军意识课程类";
            default:
                return "其他";
        }
    }
}
