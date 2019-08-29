package ru.performance.tests.flash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

import java.util.concurrent.TimeUnit;


public class ListenerThatHighlightsElements extends AbstractWebDriverEventListener {

    private static final Logger log = LogManager.getLogger();
    final private long interval;
    private final int count;
    private final String color;

    public ListenerThatHighlightsElements(String color, int count, long interval, TimeUnit unit) {
        this.color = color;
        this.count = count;
        this.interval = TimeUnit.MILLISECONDS.convert(Math.max(0, interval), unit);
    }

    @Override
    public void afterFindBy(By by, WebElement element, WebDriver driver) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        if (element != null) {        // Исключение для $$ в Selenide
            ((Locatable) element).getCoordinates().inViewPort();        // Скролить страницу до элемента
            flash(element, driver);
        }
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        flash(element, driver);
    }

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        flash(element, driver);
    }

    private void flash(WebElement element, WebDriver driver) {
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        String bgcolor = element.getCssValue("backgroundColor");
        for (int i = 0; i < count; i++) {
            changeColor(color, element, js);
            changeColor(bgcolor, element, js);
        }
    }

    private void changeColor(String color, WebElement element, JavascriptExecutor js) {
        js.executeScript("arguments[0].style.backgroundColor = '" + color + "'", element);
        try {
            Thread.sleep(interval);
        } catch (InterruptedException ex) {
            log.error(String.format("Exception: %s", ex));
        }
    }

}