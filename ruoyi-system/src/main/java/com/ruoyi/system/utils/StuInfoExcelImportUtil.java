package com.ruoyi.system.utils;

import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生信息Excel导入工具
 * 增强的Excel解析功能，处理各种异常情况
 *
 * @author ruoyi
 */
@Component
public class StuInfoExcelImportUtil {

    /**
     * 安全地解析Excel文件
     *
     * @param inputStream Excel文件输入流
     * @param academy 当前用户所属书院
     * @param domain 当前用户所属学域
     * @return 解析结果
     */
    public ExcelImportResult importExcel(InputStream inputStream, String academy, String domain) {
        ExcelImportResult result = new ExcelImportResult();
        
        try {
            // 自动检测表头位置并解析Excel
            List<StuInfo> stuInfoList = tryParseWithDifferentHeaderPositions(inputStream, academy, domain);
            
            // 检查解析结果
            if (stuInfoList == null) {
                result.setSuccess(false);
                result.setErrorMessage("Excel文件解析失败，返回null。请检查文件格式是否正确。");
                return result;
            }
            
            if (stuInfoList.isEmpty()) {
                result.setSuccess(false);
                result.setErrorMessage("Excel文件中没有数据");
                return result;
            }
            
            // 检查并过滤null对象和测试数据
            List<StuInfo> validList = new ArrayList<>();
            List<String> nullRowErrors = new ArrayList<>();
            
            for (int i = 0; i < stuInfoList.size(); i++) {
                StuInfo stuInfo = stuInfoList.get(i);
                if (stuInfo == null) {
                    nullRowErrors.add("第" + (i + 2) + "行：数据解析失败，可能为空行或格式错误");
                } else {
                    
                    // 过滤掉测试数据
                    String studentId = stuInfo.getStudentId() != null ? stuInfo.getStudentId().trim() : "";
                    String studentName = stuInfo.getStudentName() != null ? stuInfo.getStudentName().trim() : "";
                    
                    if (!isSampleData(studentId, studentName)) {
                        // 自动设置所属书院和系统内专业
                        stuInfo.setAcademy(academy);
                        stuInfo.setOriginalSystemMajor(domain);
                        validList.add(stuInfo);
                    }
                }
            }
            
            result.setSuccess(true);
            result.setStuInfoList(validList);
            result.setNullRowErrors(nullRowErrors);
            result.setTotalRows(stuInfoList.size());
            result.setValidRows(validList.size());
            result.setNullRows(nullRowErrors.size());
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("Excel文件解析异常：" + e.getMessage() + "。请检查Excel文件格式是否正确，确保表头与系统要求匹配。");
        }
        
        return result;
    }
    
    /**
     * 自动检测表头位置并解析Excel
     */
    private List<StuInfo> tryParseWithDifferentHeaderPositions(InputStream inputStream, String academy, String domain) {
        List<StuInfo> result = null;
        
        try {
            // 先将输入流转换为字节数组，以便多次读取
            byte[] excelData = org.apache.commons.io.IOUtils.toByteArray(inputStream);
            
            // 尝试不同的表头位置（0-10行）
            for (int headerRow = 0; headerRow <= 15; headerRow++) {
                try {
                    // 重新创建输入流
                    InputStream newStream = new java.io.ByteArrayInputStream(excelData);
                    
                    ExcelUtil<StuInfo> util = new ExcelUtil<StuInfo>(StuInfo.class);
                    List<StuInfo> testResult = util.importExcel(newStream, headerRow);
                    
                    if (testResult != null && !testResult.isEmpty()) {
                        // 过滤掉测试数据，只保留真实数据
                        List<StuInfo> validData = new ArrayList<>();
                        for (StuInfo info : testResult) {
                            if (info != null && info.getStudentId() != null && !info.getStudentId().trim().isEmpty()) {
                                String studentId = info.getStudentId().trim();
                                String studentName = info.getStudentName() != null ? info.getStudentName().trim() : "";
                                
                                
                                // 过滤掉测试数据
                                if (!isSampleData(studentId, studentName)) {
                                    // 自动设置所属书院和系统内专业
                                    info.setAcademy(academy);
                                    info.setOriginalSystemMajor(domain);
                                    validData.add(info);
                                }
                            }
                        }
                        
                        if (!validData.isEmpty()) {
                            result = validData;
                            break;
                        }
                    }
                    
                } catch (Exception e) {
                    // 静默处理异常，继续尝试下一个位置
                }
            }
            
        } catch (Exception e) {
            // 静默处理异常
        }
        
        return result;
    }
    
    /**
     * 判断是否为模板范例数据
     */
    private boolean isSampleData(String studentId, String studentName) {
        // 只过滤模板中的范例数据：学号20250000001，姓名张三
        return "20250000001".equals(studentId) && "张三".equals(studentName);
    }
    
    /**
     * Excel导入结果类
     */
    public static class ExcelImportResult {
        private boolean success;
        private String errorMessage;
        private List<StuInfo> stuInfoList;
        private List<String> nullRowErrors;
        private int totalRows;
        private int validRows;
        private int nullRows;
        
        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        
        public List<StuInfo> getStuInfoList() {
            return stuInfoList;
        }
        
        public void setStuInfoList(List<StuInfo> stuInfoList) {
            this.stuInfoList = stuInfoList;
        }
        
        public List<String> getNullRowErrors() {
            return nullRowErrors;
        }
        
        public void setNullRowErrors(List<String> nullRowErrors) {
            this.nullRowErrors = nullRowErrors;
        }
        
        public int getTotalRows() {
            return totalRows;
        }
        
        public void setTotalRows(int totalRows) {
            this.totalRows = totalRows;
        }
        
        public int getValidRows() {
            return validRows;
        }
        
        public void setValidRows(int validRows) {
            this.validRows = validRows;
        }
        
        public int getNullRows() {
            return nullRows;
        }
        
        public void setNullRows(int nullRows) {
            this.nullRows = nullRows;
        }
    }
}
