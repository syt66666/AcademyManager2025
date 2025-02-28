package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.StuCourseInfo;
import com.ruoyi.system.service.IStuCourseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stu/courseInfo")
public class StuCourseInfoController extends BaseController {

    @Autowired
    private IStuCourseInfoService stuCourseInfoService;

    @GetMapping("/list")
    public TableDataInfo list(StuCourseInfo stuCourseInfo) {
        startPage();
        List<StuCourseInfo> list = stuCourseInfoService.getCourseList(stuCourseInfo);
        return getDataTable(list);
    }

    @GetMapping("/get/{courseCode}")
    public AjaxResult getInfo(@PathVariable String courseCode) {
        return success(stuCourseInfoService.getCourseByCode(courseCode));
    }

    @PostMapping("/add")
    public AjaxResult add(@RequestBody StuCourseInfo stuCourseInfo) {
        return toAjax(stuCourseInfoService.addCourse(stuCourseInfo));
    }

    @PutMapping("/update")
    public AjaxResult edit(@RequestBody StuCourseInfo stuCourseInfo) {
        return toAjax(stuCourseInfoService.updateCourse(stuCourseInfo));
    }

    @DeleteMapping("/delete/{courseCode}")
    public AjaxResult remove(@PathVariable String courseCode) {
        return toAjax(stuCourseInfoService.deleteCourseByCode(courseCode));
    }
}
