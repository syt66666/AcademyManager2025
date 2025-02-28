package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.StuScore;
import com.ruoyi.system.service.IStuScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/stu/score")
public class StuScoreController extends BaseController {

    @Autowired
    private IStuScoreService stuScoreService;

    @PostMapping
    public TableDataInfo add(@RequestBody StuScore stuScore) {
        stuScoreService.addScore(stuScore);
        return getDataTable(stuScoreService.listScores(new StuScore()));
    }

    @DeleteMapping("/{id}")
    public TableDataInfo remove(@PathVariable Long id) {
        stuScoreService.deleteScore(id);
        return getDataTable(stuScoreService.listScores(new StuScore()));
    }

    @PutMapping
    public TableDataInfo edit(@RequestBody StuScore stuScore) {
        stuScoreService.updateScore(stuScore);
        return getDataTable(stuScoreService.listScores(new StuScore()));
    }

    @GetMapping("/list")
    public TableDataInfo list(StuScore condition) {
        startPage();
        List<StuScore> list = stuScoreService.listScores(condition);
        return getDataTable(list);
    }

    @GetMapping("/{id}")
    public TableDataInfo detail(@PathVariable Long id) {
        return getDataTable(Collections.singletonList(stuScoreService.getById(id)));
    }
}
