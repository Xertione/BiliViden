package com.jodio.biliagent.profile.service;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    public String summarize(List<String> signals) {
        int signalCount = signals == null ? 0 : signals.size();
        return "\u8f7b\u753b\u50cf\u6458\u8981\uff1a\u7528\u6237\u8fd1\u671f\u504f\u597d\u4ee5\u7a33\u5b9a\u6d4f\u89c8\u4e3a\u4e3b\uff0c\u5df2\u8bb0\u5f55 "
            + signalCount
            + " \u6761\u8f7b\u53cd\u9988\u4fe1\u53f7\u3002";
    }
}
