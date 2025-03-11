package com.ruoyi.system.controller;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.StuInfo;
import com.ruoyi.system.service.IStuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/system/student")
public class StuInfoController extends BaseController {
    @Autowired
    private IStuInfoService studentInfoService;
    @GetMapping("/{studentId}")
    public Map<String, Object> getStudentInfoById(@PathVariable String studentId) {
        StuInfo stuInfo = studentInfoService.getStudentInfoById(studentId);
        Map<String, Object> response = new HashMap<>();
        if (stuInfo != null) {
            response.put("studentInfo", stuInfo);
        } else {
            response.put("message", "学生未找到");
        }
        return response;
    }
    @GetMapping("/nickName")
    public AjaxResult getNickName() {
        System.out.println(getUserNick());
        AjaxResult success = AjaxResult.success(getUserNick());
        return AjaxResult.success(getUserNick());
    }
}
