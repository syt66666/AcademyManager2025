package com.ruoyi.system.utils;

import com.ruoyi.system.domain.StuMajor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MajorTreeUtils {

    public static List<StuMajor> buildTree(List<StuMajor> majors) {
        Map<Long, StuMajor> nodeMap = new HashMap<>();
        List<StuMajor> roots = new ArrayList<>();

        // 第一次遍历：创建所有节点
        majors.forEach(major -> {
            major.setChildren(new ArrayList<>());
            nodeMap.put(major.getMajorId(), major);
        });

        // 第二次遍历：建立父子关系
        majors.forEach(major -> {
            if (major.getParentId() != null) {
                StuMajor parent = nodeMap.get(major.getParentId());
                if (parent != null) {
                    parent.getChildren().add(major);
                }
            } else {
                roots.add(major);
            }
        });

        return roots;
    }
}
