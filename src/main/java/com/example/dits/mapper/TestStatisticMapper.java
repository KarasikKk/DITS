package com.example.dits.mapper;

import com.example.dits.dto.TestStatistic;
import com.example.dits.dto.TestStatisticByUser;
import com.example.dits.dto.UserStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestStatisticMapper {

    public List<TestStatisticByUser> convertToUserStatisticDTO(UserStatistics userStatistics){
        List<TestStatistic> testStatistics = userStatistics.getTestStatisticList();
        List<TestStatisticByUser> testStatisticByUsers = new ArrayList<>();
        for (TestStatistic statistic:testStatistics){
            TestStatisticByUser testStat = TestStatisticByUser.builder().testName(statistic.getTestName())
                    .avgProc(statistic.getAvgProc()).count(statistic.getCount()).build();
            testStatisticByUsers.add(testStat);
        }
        Collections.sort(testStatisticByUsers);
        return testStatisticByUsers;
    }

}
