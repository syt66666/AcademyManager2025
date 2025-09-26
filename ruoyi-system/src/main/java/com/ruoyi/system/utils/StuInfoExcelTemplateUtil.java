package com.ruoyi.system.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

/**
 * 学生信息Excel模板生成工具
 * 生成带有数据验证和填写说明的Excel模板
 *
 * @author ruoyi
 */
@Component
public class StuInfoExcelTemplateUtil {

    /**
     * 生成学生信息导入模板
     */
    public void generateTemplate(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("学生信息导入模板");
        
        // 设置列宽
        setColumnWidths(sheet);
        
        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle instructionStyle = createInstructionStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        // 创建标题行
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("学生信息导入模板 - 填写说明");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        
        // 创建说明行
        createInstructionRows(sheet, rowNum, instructionStyle);
        rowNum += 9; // 跳过说明行（9行说明）
        
        // 创建表头
        createHeaders(sheet, rowNum, headerStyle);
        rowNum++;
        
        // 创建示例数据行
        createSampleData(sheet, rowNum, dataStyle);
        rowNum++;
        
        // 添加数据验证
        addDataValidation(sheet, rowNum);
        
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("学生信息导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        
        // 写入响应
        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    /**
     * 设置列宽
     */
    private void setColumnWidths(Sheet sheet) {
        sheet.setColumnWidth(0, 15 * 256);  // 学生学号
        sheet.setColumnWidth(1, 12 * 256);  // 学生姓名
        sheet.setColumnWidth(2, 12 * 256);  // 入学年份
        sheet.setColumnWidth(3, 15 * 256);  // 所属书院
        sheet.setColumnWidth(4, 25 * 256);  // 系统内专业
        sheet.setColumnWidth(5, 20 * 256);  // 行政班
        sheet.setColumnWidth(6, 30 * 256);  // 分流形式
        sheet.setColumnWidth(7, 20 * 256);  // 是否创新班或拔尖班
    }
    
    /**
     * 创建表头样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    /**
     * 创建说明样式
     */
    private CellStyle createInstructionStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setWrapText(true);
        return style;
    }
    
    /**
     * 创建数据样式
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    /**
     * 创建说明行
     */
    private void createInstructionRows(Sheet sheet, int startRow, CellStyle style) {
        String[] instructions = {
            "填写说明：",
            "1. 以下字段为必填项，不能为空：学生学号、学生姓名、入学年份、所属书院、系统内专业、行政班、分流形式、是否创新班或拔尖班",
            "2. 学生学号：11位数字，如：20250000001",
            "3. 入学年份：4位数字，如：2025",
            "4. 所属书院：请从下拉列表选择（大煜书院、伯川书院、笃学书院、令希书院、厚德书院、知行书院、求实书院）",
            "5. 系统内专业：请从下拉列表选择（物质创造学域、智能制造学域、理科强基学域、智能建造学域、人文社科学域、信息技术学域（一）、信息技术学域（二））",
            "6. 分流形式：请从下拉列表选择（不可变更专业、仅可转专业、可类内任选，并转专业、可类内任选，不可转专业、可域内任选，并转专业）",
            "7. 是否创新班或拔尖班：输入 是 或 否",
            "8. 行政班：必填，格式：书院名+年级+班级，如：求实2501班、大煜2502班"
        };
        
        for (int i = 0; i < instructions.length; i++) {
            Row row = sheet.createRow(startRow + i);
            Cell cell = row.createCell(0);
            cell.setCellValue(instructions[i]);
            cell.setCellStyle(style);
            sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + i, 0, 7));
        }
    }
    
    /**
     * 创建表头
     */
    private void createHeaders(Sheet sheet, int rowNum, CellStyle style) {
        // 先清除可能影响表头行的合并区域
        clearMergedRegionsForRow(sheet, rowNum);
        
        Row headerRow = sheet.createRow(rowNum);
        String[] headers = {
            "学生学号", "学生姓名", "入学年份", "所属书院", "系统内专业", 
            "行政班", "分流形式", "是否创新班或拔尖班"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }
    
    /**
     * 清除指定行的合并区域
     */
    private void clearMergedRegionsForRow(Sheet sheet, int rowNum) {
        for (int i = sheet.getNumMergedRegions() - 1; i >= 0; i--) {
            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
            if (mergedRegion.getFirstRow() <= rowNum && mergedRegion.getLastRow() >= rowNum) {
                sheet.removeMergedRegion(i);
            }
        }
    }
    
    /**
     * 创建示例数据
     */
    private void createSampleData(Sheet sheet, int rowNum, CellStyle style) {
        Row dataRow = sheet.createRow(rowNum);
        String[] sampleData = {
            "20250000001", "张三", "2025", "大煜书院", "物质创造学域", 
            "大煜2501班", "不可变更专业", "否"
        };
        
        for (int i = 0; i < sampleData.length; i++) {
            Cell cell = dataRow.createCell(i);
            cell.setCellValue(sampleData[i]);
            cell.setCellStyle(style);
        }
    }
    
    /**
     * 添加数据验证
     */
    private void addDataValidation(Sheet sheet, int startRow) {
        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        
        // 所属书院下拉列表
        List<String> academyList = Arrays.asList("大煜书院", "伯川书院", "笃学书院", "令希书院", "厚德书院", "知行书院", "求实书院");
        addDropdownValidation(sheet, validationHelper, 3, startRow, 1000, academyList);
        
        // 系统内专业下拉列表
        List<String> majorList = Arrays.asList("物质创造学域", "智能制造学域", "理科强基学域", "智能建造学域", "人文社科学域", "信息技术学域（一）", "信息技术学域（二）");
        addDropdownValidation(sheet, validationHelper, 4, startRow, 1000, majorList);
        
        // 分流形式下拉列表
        List<String> divertFormList = Arrays.asList("不可变更专业", "仅可转专业", "可类内任选，并转专业", "可类内任选，不可转专业", "可域内任选，并转专业");
        addDropdownValidation(sheet, validationHelper, 6, startRow, 1000, divertFormList);
        
        // 是否创新班或拔尖班下拉列表
        List<String> innovationList = Arrays.asList("是", "否");
        addDropdownValidation(sheet, validationHelper, 7, startRow, 1000, innovationList);
    }
    
    /**
     * 添加下拉列表验证
     */
    private void addDropdownValidation(Sheet sheet, DataValidationHelper validationHelper, 
                                     int columnIndex, int startRow, int endRow, List<String> options) {
        String constraint = String.join(",", options);
        DataValidationConstraint constraint1 = validationHelper.createExplicitListConstraint(constraint.split(","));
        CellRangeAddressList regions = new CellRangeAddressList(startRow, endRow, columnIndex, columnIndex);
        DataValidation validation = validationHelper.createValidation(constraint1, regions);
        validation.setShowErrorBox(true);
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("输入错误", "请从下拉列表中选择正确的值");
        validation.setShowPromptBox(true);
        validation.createPromptBox("提示", "请从下拉列表中选择");
        sheet.addValidationData(validation);
    }
}
