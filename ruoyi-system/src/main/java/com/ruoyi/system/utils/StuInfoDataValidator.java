package com.ruoyi.system.utils;

import com.ruoyi.system.domain.StuInfo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 学生信息数据验证工具
 * 用于验证导入数据的格式和内容
 *
 * @author ruoyi
 */
@Component
public class StuInfoDataValidator {

    // 预定义的书院列表
    private static final List<String> VALID_ACADEMIES = Arrays.asList(
        "大煜书院", "伯川书院", "笃学书院", "令希书院", "厚德书院", "知行书院", "求实书院"
    );

    // 预定义的系统内专业列表
    private static final List<String> VALID_MAJORS = Arrays.asList(
        "物质创造学域", "智能制造学域", "理科强基学域", "智能建造学域", "人文社科学域", "信息技术学域（一）", "信息技术学域（二）"
    );

    // 预定义的分流形式列表
    private static final List<String> VALID_DIVERT_FORMS = Arrays.asList(
        "不可变更专业", "仅可转专业", "可类内任选，并转专业", "可类内任选，不可转专业", "可域内任选，并转专业"
    );

    // 学号格式验证正则表达式（11位数字）
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^\\d{11}$");

    // 姓名格式验证正则表达式（2-10个字符，支持中文、英文、数字、特殊字符如·等）
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9·\\-\\s]{2,10}$");

    // 行政班格式验证正则表达式（书院名+4位数字+班）
    private static final Pattern STUDENT_CLASS_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5]+\\d{4}班$");

    /**
     * 验证学生信息数据
     *
     * @param stuInfo 学生信息
     * @param rowIndex 行索引（用于错误提示）
     * @return 验证结果
     */
    public ValidationResult validate(StuInfo stuInfo, int rowIndex) {
        List<String> errors = new ArrayList<>();
        
        System.out.println("开始验证第" + rowIndex + "行数据...");
        
        // 首先检查stuInfo对象是否为null
        if (stuInfo == null) {
            System.out.println("❌ 第" + rowIndex + "行数据对象为null");
            errors.add("第" + rowIndex + "行：数据对象为空，请检查Excel文件格式");
            return new ValidationResult(false, errors);
        }
        
        System.out.println("第" + rowIndex + "行数据详情 - 学号: " + stuInfo.getStudentId() + 
            ", 姓名: " + stuInfo.getStudentName() + ", 书院: " + stuInfo.getAcademy());
        
        // 验证必填字段
        validateRequiredFields(stuInfo, errors, rowIndex);
        
        // 验证字段格式
        validateFieldFormats(stuInfo, errors, rowIndex);
        
        // 验证字段值范围
        validateFieldValues(stuInfo, errors, rowIndex);
        
        // 验证业务逻辑
        validateBusinessLogic(stuInfo, errors, rowIndex);
        
        if (errors.isEmpty()) {
            System.out.println("✅ 第" + rowIndex + "行数据验证通过");
        } else {
            System.out.println("❌ 第" + rowIndex + "行数据验证失败: " + errors);
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * 验证必填字段
     */
    private void validateRequiredFields(StuInfo stuInfo, List<String> errors, int rowIndex) {
        if (!StringUtils.hasText(stuInfo.getStudentId())) {
            errors.add("第" + rowIndex + "行：学生学号不能为空");
        }
        
        if (!StringUtils.hasText(stuInfo.getStudentName())) {
            errors.add("第" + rowIndex + "行：学生姓名不能为空");
        }
        
        if (!StringUtils.hasText(stuInfo.getAcademy())) {
            errors.add("第" + rowIndex + "行：所属书院不能为空");
        }
        
        if (!StringUtils.hasText(stuInfo.getOriginalSystemMajor())) {
            errors.add("第" + rowIndex + "行：系统内专业不能为空");
        }
        
        if (!StringUtils.hasText(stuInfo.getStudentClass())) {
            errors.add("第" + rowIndex + "行：行政班不能为空");
        }
        
        if (!StringUtils.hasText(stuInfo.getDivertForm())) {
            errors.add("第" + rowIndex + "行：分流形式不能为空");
        }
        
        // 如果创新班字段为空，设置默认值为0（不是创新班）
        if (stuInfo.getInnovationClass() == null) {
            stuInfo.setInnovationClass(0);
        }
    }

    /**
     * 验证字段格式
     */
    private void validateFieldFormats(StuInfo stuInfo, List<String> errors, int rowIndex) {
        // 验证学号格式
        if (StringUtils.hasText(stuInfo.getStudentId()) && 
            !STUDENT_ID_PATTERN.matcher(stuInfo.getStudentId()).matches()) {
            errors.add("第" + rowIndex + "行：学生学号格式错误，应为11位数字");
        }
        
        // 验证姓名格式
        if (StringUtils.hasText(stuInfo.getStudentName()) && 
            !NAME_PATTERN.matcher(stuInfo.getStudentName()).matches()) {
            errors.add("第" + rowIndex + "行：学生姓名格式错误，应为2-10个字符");
        }
        
        // 验证行政班格式（书院名+4位数字+班）
        if (StringUtils.hasText(stuInfo.getStudentClass()) && 
            !STUDENT_CLASS_PATTERN.matcher(stuInfo.getStudentClass()).matches()) {
            errors.add("第" + rowIndex + "行：行政班格式错误，应为：书院名+4位数字+班，如：求实2501班、大煜2502班");
        }
    }

    /**
     * 验证字段值范围
     */
    private void validateFieldValues(StuInfo stuInfo, List<String> errors, int rowIndex) {
        // 验证所属书院
        if (StringUtils.hasText(stuInfo.getAcademy()) && 
            !VALID_ACADEMIES.contains(stuInfo.getAcademy())) {
            errors.add("第" + rowIndex + "行：所属书院值无效，应为：" + String.join("、", VALID_ACADEMIES));
        }
        
        // 验证系统内专业
        if (StringUtils.hasText(stuInfo.getOriginalSystemMajor()) && 
            !VALID_MAJORS.contains(stuInfo.getOriginalSystemMajor())) {
            errors.add("第" + rowIndex + "行：系统内专业值无效，应为：" + String.join("、", VALID_MAJORS));
        }
        
        // 验证分流形式
        if (StringUtils.hasText(stuInfo.getDivertForm()) && 
            !VALID_DIVERT_FORMS.contains(stuInfo.getDivertForm())) {
            errors.add("第" + rowIndex + "行：分流形式值无效，应为：" + String.join("、", VALID_DIVERT_FORMS));
        }
        
        // 验证是否创新班或拔尖班（现在支持Integer类型）
        if (stuInfo.getInnovationClass() != null) {
            Integer innovationClass = stuInfo.getInnovationClass();
            if (innovationClass != 0 && innovationClass != 1) {
                errors.add("第" + rowIndex + "行：是否创新班或拔尖班值无效，应为：0 或 1");
            }
        }
    }

    /**
     * 验证业务逻辑
     */
    private void validateBusinessLogic(StuInfo stuInfo, List<String> errors, int rowIndex) {

        
        // 验证行政班与书院的匹配性
        if (StringUtils.hasText(stuInfo.getStudentClass()) && StringUtils.hasText(stuInfo.getAcademy())) {
            String classAcademy = stuInfo.getStudentClass().substring(0, 2); // 取前两个字符作为书院名
            String academyName = stuInfo.getAcademy().replace("书院", ""); // 移除"书院"后缀
            
            if (!classAcademy.equals(academyName)) {
                errors.add("第" + rowIndex + "行：行政班中的书院名与所属书院不匹配，行政班：" + classAcademy + "，所属书院：" + academyName);
            }
        }
    }

    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            return String.join("；", errors);
        }
    }
}
