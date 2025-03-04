package com.ruoyi.system.service.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.system.domain.dto.StuCompetitionRecord;
import com.ruoyi.system.mapper.StuCompetitionRecordMapper;
import com.ruoyi.system.mapper.StuInfoMapper;
import com.ruoyi.system.service.StuCompetitionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StuCompetitionRecordServiceImpl implements StuCompetitionRecordService {

    @Autowired
    private StuCompetitionRecordMapper mapper;

    @Autowired
    private StuInfoMapper stuInfoMapper;

    // ------------------- 插入竞赛记录（增强事务管理） -------------------
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertStuCompetitionRecord(StuCompetitionRecord record) {
        try {
            // 获取当前登录用户信息
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String studentId = parseStudentId(principal);

            // 设置学生信息
            setupStudentInfo(record, studentId);

            // 插入数据库
            int result = mapper.insertStuCompetitionRecord(record);
            if (result <= 0) {
                throw new RuntimeException("数据库插入失败");
            }
        } catch (Exception e) {
            // 回滚时清理已上传的文件
            if (record.getProofMaterial() != null) {
                deleteUploadedFiles(record.getProofMaterial());
            }
            throw new RuntimeException("操作失败: " + e.getMessage(), e);
        }
    }
    // ------------------- 文件清理逻辑（使用 JavaType） -------------------
    private void deleteUploadedFiles(String filePathsJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType type = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class);  // 明确集合类型
            List<String> paths = objectMapper.readValue(filePathsJson, type);

            for (String path : paths) {
                File file = new File(path);
                if (file.exists() && !file.delete()) {
                    System.err.println("文件删除失败: " + path);  // 实际项目应使用日志框架
                }
            }
        } catch (Exception e) {
            System.err.println("文件清理失败: " + e.getMessage());  // 实际项目应使用日志框架
        }
    }

    // ------------------- 辅助方法 -------------------
    // 解析学生ID
    private String parseStudentId(Object principal) {
        // 如果用户凭证为空，抛出异常
        if (principal == null) {
            throw new IllegalArgumentException("用户凭证为空");
        }
        // 如果用户凭证是UserDetails类型，返回用户名
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        // 如果用户凭证是String类型，直接返回
        } else if (principal instanceof String) {
            return (String) principal;
        }
        // 如果用户凭证类型无效，抛出异常
        throw new IllegalArgumentException("无效的用户凭证类型");
    }

// 设置学生信息
    private void setupStudentInfo(StuCompetitionRecord record, String studentId) {
        // 设置学生ID
        record.setStudentId(studentId);
        // 根据学生ID查询学生信息
        String studentName = String.valueOf(stuInfoMapper.selectByStudentId(studentId));
        // 如果学生信息为空，抛出异常
        if (studentName == null) {
            throw new RuntimeException("未找到学生信息: " + studentId);
        }
        // 设置学生姓名
        record.setStudentName(studentName);
        // 设置审核状态为未审核
        record.setAuditStatus("未审核");
        // 设置申请时间为当前时间
        record.setApplyTime(LocalDateTime.now());
    }

    @Override
    // 重写父类方法，获取所有比赛记录
    public List<StuCompetitionRecord> getAllCompetitionRecords(StuCompetitionRecord record) {
        // 获取当前登录用户的学号
        String studentId = parseStudentId(
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        );
        // 将学号设置到record对象中
        record.setStudentId(studentId);
        // 调用mapper对象中的getAllCompetitionRecords方法，获取所有比赛记录
        return mapper.getAllCompetitionRecords(record);
    }
}
