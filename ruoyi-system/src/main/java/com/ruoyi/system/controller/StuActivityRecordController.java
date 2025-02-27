package com.ruoyi.system.controller;

import com.ruoyi.system.domain.StuActivityRecord;
import com.ruoyi.system.service.IStuActivityRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stu/activityRecord")
public class StuActivityRecordController {

    @Autowired
    private IStuActivityRecordService stuActivityRecordService;

    @PostMapping("/add")
    public int addStuActivityRecord(@RequestBody StuActivityRecord record) {
        return stuActivityRecordService.addStuActivityRecord(record);
    }

    @DeleteMapping("/delete/{activityId}")
    public int deleteStuActivityRecord(@PathVariable Integer activityId) {
        return stuActivityRecordService.deleteStuActivityRecord(activityId);
    }

    @PutMapping("/update")
    public int updateStuActivityRecord(@RequestBody StuActivityRecord record) {
        return stuActivityRecordService.updateStuActivityRecord(record);
    }

    @GetMapping("/get/{activityId}")
    public StuActivityRecord getStuActivityRecordById(@PathVariable Integer activityId) {
        return stuActivityRecordService.getStuActivityRecordById(activityId);
    }

    @GetMapping("/getAll")
    public List<StuActivityRecord> getAllStuActivityRecords() {
        return stuActivityRecordService.getAllStuActivityRecords();
    }
}

