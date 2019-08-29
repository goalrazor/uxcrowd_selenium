package ru.performance.tests.pageobject.modalwindows;


import org.openqa.selenium.By;
import ru.performance.tests.pageobject.BasePage;

import static com.codeborne.selenide.Selenide.$;


public class InputModalWindowPage extends BasePage {


    /**
     * Проверить отображение модального окна
     */
    @Override
    public void checkPageShow() {
        checkAnyElementShow("Модальное окно 'Вход'", "//div[@class='modal-content']//h3[text()='Вход']");
    }


    /**
     * Нажать ссылку "Забыли пароль?"
     */
    public void forgetPasswordLinkClick() {
        String forgetPasswordLinkXpath =
                "//div[@class='modal-lk']//section[@class='switch-login-type']/label[text()='Забыли пароль?']";
        $(By.xpath(forgetPasswordLinkXpath)).click();
        log.debug("===> Удачно нажата ссылка 'Забыли пароль?'");
    }


    /**
     * Нажать ссылку "Вход по волшебной ссылке"
     */
    public void magicLinkInputLinkClick() {
        String magicLinkInputLinkXpath = "//div[@class='modal-lk']//label[@for='magicLinkLk']";
        $(By.xpath(magicLinkInputLinkXpath)).click();
        log.debug("===> Удачно нажата ссылка 'Вход по волшебной ссылке'");
    }


    /**
     * Нажать на кнопку "Twitter"
     */
    public void twitterButtonClick() {
        String twitterButtonXpath =
                "//div[@class='modal-lk-wrapper modal-lk-enterlk opened']//img[@data-uloginbutton='twitter']";
        $(By.xpath(twitterButtonXpath)).click();
        log.debug("===> Удачно нажата кнопка 'Twitter'");
    }


    /**
     * Нажать на кнопку "Google"
     */
    public void googleButtonClick() {
        String googleButtonXpath =
                "//div[@class='modal-lk-wrapper modal-lk-enterlk opened']" +
                        "//img[@data-uloginbutton='google']";
        $(By.xpath(googleButtonXpath)).click();
        log.debug("===> Удачно нажата кнопка 'Google'");
    }


    /**
     * Нажать кнопку "Вконтакте"
     */
    public void vkButtonClick() {
        String vkButtonXpath =
                "//div[@class='modal-lk-wrapper modal-lk-enterlk opened']" +
                        "//img[@data-uloginbutton='vkontakte']";
        $(By.xpath(vkButtonXpath)).click();
        log.debug("===> Удачно нажата кнопка 'Вконтакте'");
    }
}
