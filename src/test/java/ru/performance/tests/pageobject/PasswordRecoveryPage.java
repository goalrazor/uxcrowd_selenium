package ru.performance.tests.pageobject;


import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.By;
import ru.performance.config.UxCrowdConfig;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class PasswordRecoveryPage extends BasePage {

    private static UxCrowdConfig uxCrowdConfig = ConfigFactory.create(UxCrowdConfig.class);

    /**
     * Проверить отображение страницы восстановления пароля
     */
    @Override
    public void checkPageShow() {
        checkAnyElementShow("Страница восстановления пароля", "//h1[text()='Восстановление пароля']");
    }


    /**
     * Ввести новый пароль
     *
     * @param newPasswordLink Ссылка на страницу ввода нового пароля
     */
    public void newPasswordInput(String newPasswordLink) {

        // Перейти на страницу ввода нового пароля
        log.info(String.format("Переход по ссылке: %s", newPasswordLink));
        open(newPasswordLink);

        // Ввести пароль, нажать кнопку
        String newPassword = uxCrowdConfig.recoveryPasswordNewPassword();
        $(By.xpath("//input[@id='password']")).setValue(newPassword);
        $(By.xpath("//form/button[@type='submit']")).click();

        // Проверить успешность
        checkAnyElementShow("Лейбл 'Пароль изменен'", "//div[text()='Пароль изменен']");
    }
}
