package ru.performance.tests.tools;


import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.WebDriverRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import rp.com.google.common.io.BaseEncoding;


/**
 * Сделайте аннотацию для вашего класса вида <code>@Listeners({ ScreenShotOnFailListener.class})</code>
 */
public class ScreenShotOnFailListener implements ITestListener {

    protected static final Logger log = LogManager.getLogger();

    /**
     * Сделать скриншот для RP отчёта после падения теста
     */
    private static void screenshotToReportPortal() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        byte[] screenshot_image = new byte[0];
        String screenshotsLabel = "Скриншот после падения теста";

        try {
            screenshot_image = ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
        } catch (Exception ex) {
            log.error(String.format("===> Ошибка при создании скриншота после падения теста. Exception: '%s'", ex));
        }

        log.info("RP_MESSAGE#BASE64#{}#{}", BaseEncoding.base64().encode(screenshot_image), screenshotsLabel);
    }

    @Override
    public void onTestStart(ITestResult iTestResult) {

        String className = iTestResult.getMethod().getTestClass().getName();
        String methodName = iTestResult.getMethod().getMethodName();
        Screenshots.startContext(className, methodName);
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        screenshotToReportPortal();
        Screenshots.finishContext();
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        Screenshots.finishContext();
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        screenshotToReportPortal();
        Screenshots.finishContext();

    }

    @Override
    public void onStart(ITestContext iTestContext) {

    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        screenshotToReportPortal();
        Screenshots.finishContext();
    }

}
