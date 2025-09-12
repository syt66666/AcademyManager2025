package com.ruoyi.web.controller.common;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.framework.web.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Autowired
    private RedisCache redisCache;
    
    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    /**
     * 获取文件内容（支持认证）
     */
    @GetMapping("/access")
    public void accessFile(@RequestParam String path, 
                          @RequestParam(required = false) String token,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        try {
            System.out.println("文件访问请求 - path: " + path + ", token: " + (token != null ? "已提供" : "未提供"));
            
            // 验证token（如果提供）
            if (token != null && !token.isEmpty()) {
                try {
                    // 手动验证token - 解析JWT并获取用户信息
                    com.ruoyi.common.core.domain.model.LoginUser loginUser = validateTokenManually(token);
                    System.out.println("Token验证结果 - loginUser: " + (loginUser != null ? "有效" : "无效"));
                    if (loginUser == null) {
                        System.out.println("Token验证失败 - 用户信息为空");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                    System.out.println("Token验证成功 - 用户: " + loginUser.getUsername());
                } catch (Exception e) {
                    System.out.println("Token验证异常: " + e.getMessage());
                    e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }
            
            // 构建完整文件路径
            // 处理path参数，移除开头的/profile前缀（如果存在）
            String cleanPath = path;
            if (cleanPath.startsWith("/profile")) {
                cleanPath = cleanPath.substring("/profile".length());
            }
            if (cleanPath.startsWith("/")) {
                cleanPath = cleanPath.substring(1);
            }
            
            String fullPath = RuoYiConfig.getProfile() + File.separator + cleanPath;
            File file = new File(fullPath);
            System.out.println("原始path: " + path);
            System.out.println("清理后path: " + cleanPath);
            System.out.println("完整文件路径: " + fullPath);
            System.out.println("文件存在: " + file.exists());
            
            if (!file.exists()) {
                System.out.println("文件不存在: " + fullPath);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            // 设置响应头
            String fileName = file.getName();
            String contentType = getContentType(fileName);
            System.out.println("文件信息 - 文件名: " + fileName + ", Content-Type: " + contentType);
            
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline; filename=" + fileName);
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // 输出文件内容
            System.out.println("开始输出文件内容...");
            FileUtils.writeBytes(fullPath, response.getOutputStream());
            System.out.println("文件内容输出完成");
        } catch (Exception e) {
            System.out.println("文件访问异常: " + e.getMessage());
            e.printStackTrace();
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
    
    /**
     * 手动验证token
     */
    private LoginUser validateTokenManually(String token) {
        try {
            // 解析JWT token
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            
            // 获取用户UUID
            String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
            if (uuid == null || uuid.isEmpty()) {
                System.out.println("Token中未找到用户UUID");
                return null;
            }
            
            // 从Redis获取用户信息
            String userKey = CacheConstants.LOGIN_TOKEN_KEY + uuid;
            LoginUser user = redisCache.getCacheObject(userKey);
            
            if (user == null) {
                System.out.println("Redis中未找到用户信息，userKey: " + userKey);
            }
            
            return user;
        } catch (Exception e) {
            System.out.println("Token解析异常: " + e.getMessage());
            return null;
        }
    }
}
