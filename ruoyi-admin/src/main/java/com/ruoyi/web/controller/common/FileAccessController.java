package com.ruoyi.web.controller.common;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.framework.web.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 文件访问控制器
 * 用于解决服务器部署时文件预览认证问题
 */
@RestController
@RequestMapping("/file")
public class FileAccessController {

    @Autowired
    private TokenService tokenService;

    /**
     * 获取文件内容（支持认证）
     */
    @GetMapping("/access")
    public void accessFile(@RequestParam String path, 
                          @RequestParam(required = false) String token,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        try {
            // 验证token（如果提供）
            if (token != null && !token.isEmpty()) {
                try {
                    // 使用现有的token验证方法
                    String username = tokenService.getUsernameFromToken(token);
                    if (username == null || username.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
            
            // 构建完整文件路径
            String fullPath = RuoYiConfig.getProfile() + File.separator + path;
            File file = new File(fullPath);
            
            if (!file.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 设置响应头
            String fileName = file.getName();
            String contentType = getContentType(fileName);
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline; filename=" + fileName);
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // 输出文件内容
            FileUtils.writeBytes(fullPath, response.getOutputStream());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 根据文件名获取Content-Type
     */
    private String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "pdf":
                return "application/pdf";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "doc":
                return "application/msword";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }
}
