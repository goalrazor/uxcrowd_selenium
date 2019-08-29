package ru.performance.tests.flash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import java.util.concurrent.TimeUnit;


public class ListenerThatWaitsBeforeAnyAction extends AbstractWebDriverEventListener {

    private static final Logger log = LogManager.getLogger();
    private final long timeout;

    public ListenerThatWaitsBeforeAnyAction(long timeout, TimeUnit unit) {
        this.timeout = TimeUnit.MILLISECONDS.convert(Math.max(0, timeout), unit);
    }

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        sleep();
    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        sleep();
    }

    @Override
    public void beforeNavigateForward(WebDriver driver) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        sleep();
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        sleep();
    }

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        sleep();
    }

    @Override
    public void beforeGetText(WebElement element, WebDriver driver) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        sleep();
    }

    @Override
    public void beforeScript(String script, WebDriver driver) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        sleep();
    }

    private void sleep() {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {
            log.error(String.format("Exception: %s", ex));
        }
    }

}
