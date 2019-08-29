package ru.performance.tests.pageobject.modalwindows;


import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.By;
import ru.performance.config.UxCrowdConfig;
import ru.performance.tests.pageobject.BasePage;

import static com.codeborne.selenide.Selenide.$;


public class VkAuthModalWindowPage extends BasePage {

    private static UxCrowdConfig uxCrowdConfig = ConfigFactory.create(UxCrowdConfig.class);


    /**
     * Проверить отображение модального окна
     */
    @Override
    public void checkPageShow() {
        checkAnyElementShow("Модальное окно 'Вконтакте. Разрешение доступа'", "//a[@href='https://vk.com']");
    }


    /**
     * Ввести логин
     */
    public void loginInput() {
        String vkUserNameInputXpath = "//input[@name='email']";
        $(By.xpath(vkUserNameInputXpath)).setValue(uxCrowdConfig.vkUserName());
    }


    /**
     * Ввести пароль
     */
    public void passwordInput() {
        String vkUserPasswordInputXpath = "//input[@name='pass']";
        $(By.xpath(vkUserPasswordInputXpath)).setValue(uxCrowdConfig.vkUserPassword());
    }


    /**
     * Нажать кнопку "Войти"
     */
    public void inputButtonClick() {
        String inputButtonXpath = "//button[@id='install_allow']";
        $(By.xpath(inputButtonXpath)).click();
    }

}
