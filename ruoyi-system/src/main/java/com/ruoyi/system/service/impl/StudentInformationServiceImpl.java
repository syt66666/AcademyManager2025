package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.StudentInformation;
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
        List<StudentInformation> list = new LinkedList<>();
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
            studentInformation.setPassword("123");
            list.add(studentInformation);
        }
        studentInformationMapper.multipleInsertStudent(list);
        System.out.println(132);
    }
}
