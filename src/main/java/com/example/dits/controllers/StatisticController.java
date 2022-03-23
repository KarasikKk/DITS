package com.example.dits.controllers;

import com.example.dits.dto.UserStatistics;
import com.example.dits.entity.User;
import com.example.dits.service.impl.StatisticServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class StatisticController {
    private final StatisticServiceImpl statisticService;

    @GetMapping("/personalStatistic")
    public String personalStatistic(ModelMap model, HttpSession session) {
//        User user = (User) session.getAttribute("user");
//        UserStatistics userStatistics = statisticService.getUserStatistics(user);
//        model.addAttribute("title","Statistic");
//        if (userStatistics.getTestStatisticList().size() > 0){
//            model.addAttribute("statisticList",userStatistics.getTestStatisticList());
//            return "user/personalStatistic";
//        }else {
//            return "user/emptyPersonalStatistic";
//        }
        return "user/personalStatistic";
    }
}
