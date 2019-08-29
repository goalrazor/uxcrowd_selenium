package ru.performance.tests.pageobject.modalwindows;


import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.By;
import ru.performance.config.UxCrowdConfig;
import ru.performance.tests.pageobject.BasePage;

import static com.codeborne.selenide.Selenide.$;


public class GoogleAuthModalWindowPage extends BasePage {

    private String nextButtonXpath = "//span[text()='Далее']";
    private static UxCrowdConfig uxCrowdConfig = ConfigFactory.create(UxCrowdConfig.class);


    /**
     * Проверить отображение модального окна
     */
    @Override
    public void checkPageShow() {
        checkAnyElementShow("Модальное окно 'Google Authorization'", "//div[@class='Fmgc2c']");
    }


    /**
     * Ввести логин
     */
    public void loginInput() {
        String googleUserNameInputXpath = "//input[@id='identifierId']";
        $(By.xpath(googleUserNameInputXpath)).setValue(uxCrowdConfig.googleUserName());
        $(By.xpath(nextButtonXpath)).click();
    }


    /**
     * Ввести пароль
     */
    public void passwordInput() {
        String googleUserPasswordInputXpath = "//input[@name='password']";
        $(By.xpath(googleUserPasswordInputXpath)).setValue(uxCrowdConfig.googleUserPassword());
        $(By.xpath(nextButtonXpath)).click();
    }

}
