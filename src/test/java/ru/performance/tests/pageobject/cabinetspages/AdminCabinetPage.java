package ru.performance.tests.pageobject.cabinetspages;


import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class AdminCabinetPage extends CabinetPage {


    /**
     * Проверить отображение страницы кабинета администратора
     */
    @Override
    public void checkPageShow() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        try {
            SelenideElement checkElement = $(By.xpath("//div[@role='button' and text()='Тестировщики']"));
            checkElement.shouldBe(visible);
            log.debug("===> Страница кабинета администратора удачно открылась");
        } catch (NoSuchElementException ex) {
            log.error(String.format("===> Страница кабинета администратора не открылась: '%s'", ex.getMessage()));
            assert false;
        }
    }


    /**
     * Выйти из кабинета администратора
     */
    @Override
    public void cabinetLogout() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        // Нажать кнопку "Выйти"
        String goOutButtonXpath = "//a[@id='logout']";
        $(By.xpath(goOutButtonXpath)).click();
        log.info("Кнопка 'Выйти' в кабинете администратора удачно нажата");
    }

}
