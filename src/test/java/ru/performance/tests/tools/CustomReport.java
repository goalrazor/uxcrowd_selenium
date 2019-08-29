package ru.performance.tests.tools;

import com.google.gson.Gson;
import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;
import org.testng.xml.XmlSuite;
import ru.performance.config.UxCrowdConfig;

import java.io.*;
import java.util.*;


public class CustomReport implements IReporter {

    protected static final Logger log = LogManager.getLogger();
    private static UxCrowdConfig uxCrowdConfig = ConfigFactory.create(UxCrowdConfig.class);
    final private String customReportFileName = uxCrowdConfig.customReportFileName();
    private PrintWriter writer;

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {

        JsonReport jsonReport = new JsonReport();
        Set<JsonTest> tests = new HashSet<>();
        int suiteTotalPassedTests = 0;
        int suiteTotalFailedTests = 0;
        int suiteTotalSkippedTests = 0;

        // Создать директорию, если ещё её нет
        File reportDirectory = new File(outputDirectory);
        if (!reportDirectory.exists()) {
            if (!reportDirectory.mkdirs()) {
                log.error(String.format("Directory '%s' failed to create\n", outputDirectory));
            }
        }

        try {
            writer = new PrintWriter(
                    new BufferedWriter(new FileWriter(new File(outputDirectory, customReportFileName)))
            );
        } catch (IOException ex) {
            log.error("Error in creating writer: " + ex);
        }

        // Бежать по всем сюитам (пока одна сюита)
        for (ISuite suite : suites) {

            // Имя сюиты
            jsonReport.setSuiteName(suite.getName());

            // Результаты для сюиты
            Map<String, ISuiteResult> suiteResults = suite.getResults();

            // Бежим по результатам (по тестам - Smoke, LoginFormAuthorization и т.д.)
            for (ISuiteResult sr : suiteResults.values()) {

                JsonTest jsonTest = new JsonTest();
                ITestContext tc = sr.getTestContext();  // The testing context
                jsonTest.setTestClassName(tc.getName());

                // Количество удачных
                int passedTests = tc.getPassedTests().getAllResults().size();
                jsonTest.setPassedTestsNum(passedTests);
                suiteTotalPassedTests += passedTests;

                // Количество упавших
                int failedTests = tc.getFailedTests().getAllResults().size();
                jsonTest.setFailedTestsNum(failedTests);
                suiteTotalFailedTests += failedTests;

                // Количество пропущенных
                int skippedTests = tc.getSkippedTests().getAllResults().size();
                jsonTest.setSkippedTestsNum(skippedTests);
                suiteTotalSkippedTests += skippedTests;

                // Получить список удачных тестов из TestNG
                Set<Test> passedTestsList = new HashSet<>();
                getTestsList(passedTestsList, tc.getPassedTests());
                jsonTest.setPassedTests(passedTestsList);

                // Получить список упавших тестов из TestNG
                Set<Test> failedTestsList = new HashSet<>();
                getTestsList(failedTestsList, tc.getFailedTests());
                jsonTest.setFailedTests(failedTestsList);

                // Получить список пропущенных тестов из TestNG
                Set<Test> skippedTestsList = new HashSet<>();
                getTestsList(skippedTestsList, tc.getSkippedTests());
                jsonTest.setSkippedTests(skippedTestsList);


                tests.add(jsonTest);    // Добавить тест в список тестов сюиты

            }
            jsonReport.setTests(tests);     // Добавить список тестов в JSON рапорт

        }

        // Общее количество
        jsonReport.totalPassedTestsNum = suiteTotalPassedTests;
        jsonReport.totalFailedTestsNum = suiteTotalFailedTests;
        jsonReport.totalSkippedTestsNum = suiteTotalSkippedTests;
        jsonReport.totalTestsNum = suiteTotalPassedTests + suiteTotalFailedTests + suiteTotalSkippedTests;

        // Сериализовать в JSON
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonReport);
        log.debug(String.format("JSON:\n%s", jsonString));
        writer.println(jsonString);

        writer.flush();
        writer.close();

    }


    /**
     * Возвращает список тестов с именем метода, описанием метода, массивом имён групп
     *
     * @param testsList       Список тестов для JSON отчёта
     * @param testsListTestNG Список требуемых (прошедших, упавших или пропущенных) тестов в TestNG
     */
    private void getTestsList(Set<Test> testsList, IResultMap testsListTestNG) {

        Collection<ITestNGMethod> testsListTestNg = testsListTestNG.getAllMethods();
        for (ITestNGMethod method : testsListTestNg) {
            Test test = new Test();
            test.setTestGroups(method.getGroups());             // Массив имён групп теста
            test.setTestMethodName(method.getMethodName());           // Имя теста (его метода)

//            method.setDescription("Просто описание");
            test.setTestDescription(method.getDescription());   // Описание теста
            testsList.add(test);
        }
    }

}

/**
 * Кастомный JSON отчёт
 */
class JsonReport {
    int totalTestsNum;
    int totalPassedTestsNum;
    int totalFailedTestsNum;
    int totalSkippedTestsNum;
    private String suiteName;
    private Set<JsonTest> tests;

    void setSuiteName(String suiteName) {
        this.suiteName = suiteName;
    }

    public Set<JsonTest> getTests() {
        return tests;
    }

    public void setTests(Set<JsonTest> tests) {
        this.tests = tests;
    }
}

/**
 * Тест в JSON отчёте
 */
class JsonTest {
    private String testClassName;
    private int passedTestsNum;
    private int failedTestsNum;
    private int skippedTestsNum;

    private Set<Test> passedTests;
    private Set<Test> failedTests;
    private Set<Test> skippedTests;

    void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    void setPassedTestsNum(int passedTestsNum) {
        this.passedTestsNum = passedTestsNum;
    }

    void setFailedTestsNum(int failedTestsNum) {
        this.failedTestsNum = failedTestsNum;
    }

    void setSkippedTestsNum(int skippedTestsNum) {
        this.skippedTestsNum = skippedTestsNum;
    }

    void setFailedTests(Set<Test> failedTests) {
        this.failedTests = failedTests;
    }

    void setPassedTests(Set<Test> passedTests) {
        this.passedTests = passedTests;
    }

    void setSkippedTests(Set<Test> skippedTests) {
        this.skippedTests = skippedTests;
    }
}

/**
 * Тест
 */
class Test {
    private String[] testGroups;
    private String testMethodName;
    private String testDescription;

    void setTestGroups(String[] testGroups) {
        this.testGroups = testGroups;
    }

    void setTestMethodName(String testMethodName) {
        this.testMethodName = testMethodName;
    }

    void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }
}
