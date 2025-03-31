package com.ruoyi.system.service;

import java.util.List;

public interface IStuAbilityScoreService {
    void calculateAndStoreGPAScores();
    void calculateAndStoreGPAScores(List<String> studentIds);
}
