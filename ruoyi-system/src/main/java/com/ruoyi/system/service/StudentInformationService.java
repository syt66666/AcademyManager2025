package com.ruoyi.system.service;

import com.ruoyi.system.domain.StudentInformation;
import org.springframework.web.multipart.MultipartFile;

/**
 * 管理学生信息
 */
public interface StudentInformationService {
    public void multipleInsertStudent(MultipartFile file);

    public void insertOneStudent(StudentInformation studentInformation);
}
