package ru.performance.tests.pageobject.cabinetspages;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class TesterCabinetPage extends CabinetPage {


    /**
     * Проверить отображение страницы кабинета тестировщика
     */
    @Override
    public void checkPageShow() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        try {
            SelenideElement checkElement = $(By.xpath("//div[text()='Привет!']"));
            checkElement.shouldBe(visible);
            log.info("===> Страница кабинета тестера удачно открылась");
        } catch (NoSuchElementException ex) {
            log.error(String.format("===> Страница кабинета тестера не открылась: '%s'", ex.getMessage()));
            assert false;
        }
    }


    /**
     * Выйти из кабинета тестировщика
     */
    @Override
    public void cabinetLogout() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        // Нажать кнопку "Выйти"
        String goOutButtonXpath = "//a[@id='logout']";
        $(By.xpath(goOutButtonXpath)).click();
        log.info("Кнопка 'Выйти' в кабинете тестера удачно нажата");
    }

}
