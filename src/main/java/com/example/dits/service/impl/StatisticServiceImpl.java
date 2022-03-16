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

        setTestLists(testLists, testStatistics);
        Collections.sort(testStatistics);
        return testStatistics;
    }

    private void setTestLists(List<Test> testLists, List<TestStatistic> testStatistics) {
        for (Test test : testLists) {

            List<Question> questionList = test.getQuestions();
            List<QuestionStatistic> questionStatistics = new ArrayList<>();
            QuestionStatisticAttempts statisticAttempts = new QuestionStatisticAttempts(0,0,0);
            setQuestionStatistics(questionList, questionStatistics,statisticAttempts);
            Collections.sort(questionStatistics);

            int testAverage = calculateTestAverage(statisticAttempts.getTestSumAvg(), questionStatistics.size());
            testStatistics.add(new TestStatistic(test.getName(), statisticAttempts.getNumberOfAttempts(),
                    testAverage, questionStatistics));
        }
    }

    private void setQuestionStatistics(List<Question> questionList, List<QuestionStatistic> questionStatistics,
                                   QuestionStatisticAttempts statisticAttempts) {
        for (Question question : questionList) {

            List<Statistic> statisticList = getStatisticByQuestion(question);
            statisticAttempts.setNumberOfAttempts(statisticList.size());
            int rightAnswers = numberOfRightAnswers(statisticList);
            if (statisticAttempts.getNumberOfAttempts() != 0)
                statisticAttempts.setQuestionAvg(calculateAvg(statisticAttempts.getNumberOfAttempts(), rightAnswers));

            statisticAttempts.setTestSumAvg(statisticAttempts.getTestSumAvg()+statisticAttempts.getQuestionAvg());
            questionStatistics.add(new QuestionStatistic(question.getDescription(),
                    statisticAttempts.getNumberOfAttempts(), statisticAttempts.getQuestionAvg()));
        }
    }

    private int numberOfRightAnswers(List<Statistic> statisticList){
        int rightAnswer = 0;
        rightAnswer = getRightAnswer(statisticList, rightAnswer);
        return rightAnswer;
    }

    private int getRightAnswer(List<Statistic> statisticList, int rightAnswer) {
        for (Statistic statistic : statisticList) {
            if (statistic.isCorrect())
                rightAnswer++;
        }
        return rightAnswer;
    }

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
            countOfRightAnswers = getCountOfRightAnswers(values, countOfRightAnswers);

            int avg = (int) ((countOfRightAnswers / values.size()) * 100);
            testStatisticByDate.setAvg(avg);
            testStatisticsByDate.add(testStatisticByDate);
        }
        return testStatisticsByDate;
    }

    private double getCountOfRightAnswers(ArrayList<Statistic> values, double countOfRightAnswers) {
        for (Statistic st : values){
            if(st.isCorrect()){
                countOfRightAnswers++;
            }
        }
        return countOfRightAnswers;
    }

    private Map<Date, ArrayList<Statistic>> getStatisticsByDate(List<Statistic> statistics) {
        Map<Date, ArrayList<Statistic>> statisticsByDate = new HashMap<>();
        setStatisticsByDate(statistics, statisticsByDate);
        return statisticsByDate;
    }

    private void setStatisticsByDate(List<Statistic> statistics, Map<Date, ArrayList<Statistic>> statisticsByDate) {
        for (Statistic st : statistics){
            if(!statisticsByDate.containsKey(st.getDate())){
                ArrayList<Statistic> statisticList = new ArrayList<>();
                statisticsByDate.put(st.getDate(),statisticList);
                statisticList.add(st);
            } else{
                statisticsByDate.get(st.getDate()).add(st);
            }
        }
    }
}
