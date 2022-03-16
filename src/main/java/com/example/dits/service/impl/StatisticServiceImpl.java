package com.example.dits.service.impl;

import com.example.dits.DAO.StatisticRepository;
import com.example.dits.dto.*;
import com.example.dits.entity.*;
import com.example.dits.service.StatisticService;
import com.example.dits.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository repository;
    private final TopicService topicService;
    private static final int initValue = 0;

    @Transactional
    public void saveMapOfStat(Map<String, Statistic> map, String endTest){
        for (Statistic st : map.values()){
            st.setDate(new Date());
        }
    }

    @Transactional
    @Override
    public List<Statistic> getStatisticsByUser(User user){
        return repository.getStatisticsByUser(user);
    }

    @Transactional
    @Override
    public List<Statistic> getStatisticByQuestion(Question question) {
        return repository.getStatisticByQuestion(question);
    }

    @Override
    public void saveStatisticsToDB(List<Statistic> statistics) {
        Date date = new Date();
        for (Statistic statistic : statistics){
            statistic.setDate(date);
            save(statistic);
        }
    }

    @Override
    public int calculateRightAnswers(List<Statistic> statistics) {
        return (int) statistics.stream().filter(Statistic::isCorrect).count();
    }

    @Transactional
    public void create(Statistic statistic) {
        repository.save(statistic);
    }

    @Transactional
    public void update(Statistic statistic, int id) {
        Optional<Statistic> st = repository.findById(id);
        if(st.isEmpty())
            return;
        else
            repository.save(statistic);
    }

    @Transactional
    public void delete(Statistic statistic) {
        repository.delete(statistic);
    }

    @Transactional
    public void save(Statistic statistic) {
        repository.save(statistic);
    }

    @Transactional
    public List<Statistic> findAll() {
        return repository.findAll();
    }

    @Transactional
    public UserStatistics getUserStatistics(User user){
        List<TestStatistic> testStatisticList = getTestStatisticsByUser(user);
        return new UserStatistics(user.getFirstName(),user.getLastName(),user.getLogin(),testStatisticList);
    }
    @Transactional
    @Override
    public void removeStatisticByUserId(int userId){
        repository.removeStatisticByUser_UserId(userId);
    }

    @Transactional
    @Override
    public void deleteAll(){
        repository.deleteAll();
    }

    private List<TestStatistic> getTestStatisticsByUser(User user) {
        List<Statistic> statistics = getStatisticsByUser(user);
        Map<Date, ArrayList<Statistic>> statisticByDate  = getStatisticsByDate(statistics);

        List<TestStatisticByDate> testStatisticsByDate = getTestStatisticByDate(statisticByDate);
        Map<String, TestStatistic> statisticByTestName = getMapTestStatisticsByTestName(testStatisticsByDate);
        List<TestStatistic> statisticList = new ArrayList<>(statisticByTestName.values());
        Comparator<TestStatistic> comp = Comparator.comparingInt(TestStatistic::getAvgProc);
        statisticList.sort(comp);
        return statisticList;
    }

    @Transactional
    public List<TestStatistic> getListOfTestsWithStatisticsByTopic(int  topicId){
        Topic topic = topicService.getTopicByTopicId(topicId);
        return getTestStatistics(topic);
    }

    private List<TestStatistic> getTestStatistics(Topic topic) {
        List<Test> testLists = topic.getTestList();
        List<TestStatistic> testStatistics = new ArrayList<>();

        for (Test test : testLists) {

            List<Question> questionList = test.getQuestions();
            List<QuestionStatistic> questionStatistics = new ArrayList<>();
            int numberOfAttempts = 0;
            int questionAvg = 0;
            int testSumAvg = 0;
            for (Question question : questionList) {

                List<Statistic> statisticList = getStatisticByQuestion(question);
                numberOfAttempts = statisticList.size();
                int rightAnswers = numberOfRightAnswers(statisticList);

                if (numberOfAttempts != 0)
                    questionAvg = calculateAvg(numberOfAttempts, rightAnswers);

                testSumAvg += questionAvg;
                //questionStatistics.add(new QuestionStatistic(numberOfAttempts, questionAvg));
                questionStatistics.add(new QuestionStatistic(question.getDescription(), numberOfAttempts, questionAvg));
            }
            Collections.sort(questionStatistics);

            int testAverage = calculateTestAverage(testSumAvg, questionStatistics.size());
            testStatistics.add(new TestStatistic(test.getName(), numberOfAttempts, testAverage, questionStatistics));
        }
        Collections.sort(testStatistics);
        return testStatistics;
    }

    private int numberOfRightAnswers(List<Statistic> statisticList){
        int rightAnswer = 0;
        for (Statistic statistic : statisticList) {
            if (statistic.isCorrect())
                rightAnswer++;
        }
        return rightAnswer;
    }

//    @Transactional
//    public List<TopicStatisticByTests> getListTopicStaticByTests(){
//
//        List<Topic> topics = topicService.findAll();
//        List<TopicStatisticByTests> topicStatisticByTests = new ArrayList<>();
//
//        for (Topic topic : topics) {
//            List<TestStatistic> testStatistics = getTestStatistics(topic);
//
//            topicStatisticByTests.add(new TopicStatisticByTests(topic.getName(),
//                    topic.getDescription(),testStatistics));
//        }
//
//        return topicStatisticByTests;
//    }

    private int calculateTestAverage(int testSumAvg, int questionStatisticsSize) {
        if (questionStatisticsSize != 0)
            return testSumAvg / questionStatisticsSize;
        else
            return testSumAvg;
    }

    private int calculateAvg(int count, double rightAnswer) {
        return (int) (rightAnswer / count * 100);
    }

    private Map<String, TestStatistic> getMapTestStatisticsByTestName(List<TestStatisticByDate> testStatisticsByDate) {
        Map<String, TestStatistic> statisticByName = new HashMap<>();
        for (TestStatisticByDate st : testStatisticsByDate){
            if (!statisticByName.containsKey(st.getTestName())) {
                TestStatistic testStatistic = new TestStatistic(st.getTestName(),initValue + 1,st.getAvg());
                statisticByName.put(st.getTestName(), testStatistic);
            }
            else{
                TestStatistic testStatistic = statisticByName.get(st.getTestName());
                double sumOfAvg = testStatistic.getAvgProc() * testStatistic.getCount();
                testStatistic.setCount(testStatistic.getCount() + 1);
                int finishAvg =(int)((sumOfAvg + st.getAvg()) / testStatistic.getCount());
                testStatistic.setAvgProc(finishAvg);
            }
        }
        return statisticByName;
    }


    @Transactional
    List<TestStatisticByDate> getTestStatisticByDate(Map<Date, ArrayList<Statistic>> statisticByDate) {
        List<TestStatisticByDate> testStatisticsByDate = new ArrayList<>();

        for (ArrayList<Statistic> values : statisticByDate.values()){
            double countOfRightAnswers = 0;
            TestStatisticByDate testStatisticByDate = new TestStatisticByDate();
            testStatisticByDate.setTestName(values.get(0).getQuestion().getTest().getName());
            for (Statistic st : values){
                if(st.isCorrect()){
                    countOfRightAnswers++;
                }
            }

            int avg = (int) ((countOfRightAnswers / values.size()) * 100);
            testStatisticByDate.setAvg(avg);
            testStatisticsByDate.add(testStatisticByDate);
        }
        return testStatisticsByDate;
    }

    private Map<Date, ArrayList<Statistic>> getStatisticsByDate(List<Statistic> statistics) {
        Map<Date, ArrayList<Statistic>> statisticsByDate = new HashMap<>();
        for (Statistic st : statistics){
            if(!statisticsByDate.containsKey(st.getDate())){
                ArrayList<Statistic> statisticList = new ArrayList<>();
                statisticsByDate.put(st.getDate(),statisticList);
                statisticList.add(st);
            } else{
                statisticsByDate.get(st.getDate()).add(st);
            }
        }
        return statisticsByDate;
    }
}
