package ru.performance.tests.pageobject.modalwindows;


import org.openqa.selenium.By;
import ru.performance.tests.pageobject.BasePage;

import static com.codeborne.selenide.Selenide.$;


public class PasswordRecoveryModalWindowPage extends BasePage {


    /**
     * Проверить отображение модального окна
     */
    @Override
    public void checkPageShow() {
        checkAnyElementShow(
                "Модальное окно 'Восстановление пароля'",
                "//div[@class='modal-lk']//h3[text()='Восстановление пароля']");
    }


    /**
     * Отправить E-mail
     */
    public void sendEmail(String eMail) {

        $(By.xpath("//div[@class='modal-lk']//input[@id='email_valid']")).setValue(eMail);
        log.debug("===> E-mail введён'");

        $(By.xpath("//div[@class='modal-lk']//button[text()='Восстановить']")).click();
        log.debug("===> Кнопка 'Восстановить' нажата");
    }


}
