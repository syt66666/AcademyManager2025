package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StudentInformation;
import com.ruoyi.system.domain.StudentUserInformation;
import com.ruoyi.system.domain.StudentUserRole;
import com.ruoyi.system.mapper.StudentInformationMapper;
import com.ruoyi.system.service.StudentInformationService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class StudentInformationServiceImpl implements StudentInformationService {

    @Autowired
    private StudentInformationMapper studentInformationMapper;

    /**
     * 根据传入的表格批量插入学生信息
     * @param file
     */
    @Override
    public void multipleInsertStudent(MultipartFile file) {
        List<StudentInformation> studentInformationList = new LinkedList<>();
        List<StudentUserInformation> studentUserInformationList = new LinkedList<>();
        List<StudentUserRole> studentUserRoleList = new LinkedList<>();
        Workbook workbook = null;
        try {
            // 创建一个工作簿对象
            workbook = new XSSFWorkbook(file.getInputStream());
        } catch (IOException e) {
            // 打印异常信息到控制台
            e.printStackTrace();
            // 抛出 RuntimeException，并附带原始的 IOException
            throw new RuntimeException("发生了异常，传入表为空");
        }
        // 获取工作簿中的第一个sheet
        Sheet sheet = workbook.getSheetAt(0);

        // 获取所有行
        Iterator<Row> rowIterator = sheet.iterator();

        // 跳过标题行（假设第一行是标题）
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        // 遍历所有行
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int numberOfCells = row.getPhysicalNumberOfCells();
            String[] stu = new String[numberOfCells];
            for (int i = 0; i < numberOfCells; i++) {
                stu[i] = row.getCell(i).getStringCellValue();
            }
            //学生信息表信息
            StudentInformation studentInformation = new StudentInformation();
            studentInformation.setStudentId(stu[0]);
            studentInformation.setStudentName(stu[1]);
            studentInformation.setStudentAddress(stu[2]);
            studentInformation.setSystemMajor(stu[3]);
            studentInformation.setEnrollmentMajor(stu[4]);
            studentInformation.setStudentClass(stu[5]);
            studentInformation.setStudentNote(stu[6]);
            studentInformation.setStudentDiversionForm(stu[7]);
            studentInformation.setHaveQualification("是".equals(stu[8]) ? true : false);
            studentInformation.setEnglishName(stu[9]);
            studentInformation.setStudentSex(stu[10]);
            studentInformationList.add(studentInformation);
            //系统登录用户表信息
            StudentUserInformation studentUserInformation = new StudentUserInformation();
            studentUserInformation.setStudentId(stu[0]);
            studentUserInformation.setStudentName(stu[1]);
            studentUserInformation.setStudentSex("男".equals(stu[10]) ? "0" : "1");
            studentUserInformation.setCreateTime(LocalDateTime.now());
            studentUserInformationList.add(studentUserInformation);
        }
        studentInformationMapper.multipleInsertStudent(studentInformationList);
        studentInformationMapper.multipleInsertStudentAccount(studentUserInformationList);
        List<Integer> ids = studentInformationMapper.getInsertStudentAccountIds(studentUserInformationList);
        for(Integer id : ids){
            StudentUserRole studentUserRole = new StudentUserRole();
            studentUserRole.setRoleId(101);
            studentUserRole.setUserId(id);
            studentUserRoleList.add(studentUserRole);
        }
        //向关联表中插入数据
        studentInformationMapper.multipleInsertUserRole(studentUserRoleList);
    }

    /**
     * 插入一条学生数据
     * @param studentInformation
     */
    @Override
    public void insertOneStudent(StudentInformation studentInformation) {
        List<StudentInformation> studentInformationList = new LinkedList<>();
        studentInformationList.add(studentInformation);
        //插入学生数据
        studentInformationMapper.multipleInsertStudent(studentInformationList);

        List<StudentUserInformation> studentUserInformationList = new LinkedList<>();
        StudentUserInformation studentUserInformation = new StudentUserInformation();
        studentUserInformation.setStudentId(studentInformation.getStudentId());
        studentUserInformation.setStudentName(studentInformation.getStudentName());
        studentUserInformation.setStudentSex("男".equals(studentInformation.getStudentSex()) ? "0" : "1");
        studentUserInformation.setCreateTime(LocalDateTime.now());
        studentUserInformationList.add(studentUserInformation);
        //插入学生账号数据
        studentInformationMapper.multipleInsertStudentAccount(studentUserInformationList);

        List<Integer> ids = studentInformationMapper.getInsertStudentAccountIds(studentUserInformationList);
        List<StudentUserRole> studentUserRoleList = new LinkedList<>();
        StudentUserRole studentUserRole = new StudentUserRole();
        studentUserRole.setRoleId(101);
        studentUserRole.setUserId(ids.get(0));
        studentUserRoleList.add(studentUserRole);
        //向关联表中插入数据
        studentInformationMapper.multipleInsertUserRole(studentUserRoleList);
    }
}
