package ru.performance.tests.pageobject.modalwindows;


import org.openqa.selenium.By;
import ru.performance.tests.pageobject.BasePage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class MagicLinkRecoveryModalWindowPage extends BasePage {


    /**
     * Проверить отображение модального окна
     */
    @Override
    public void checkPageShow() {
        checkAnyElementShow(
                "Модальное окно 'Волшебная ссылка'",
                "//div[@class='modal-lk']//section[text()='Волшебная ссылка']");
    }


    /**
     * Ввести заданную строку в поле для E-mail
     *
     * @param emailString Строка для ввода
     */
    public void inputEmail(String emailString) {
        $(By.xpath("//div[@class='modal-lk']//input[@id='email']")).setValue(emailString);
    }


    /**
     * Проверить отображение об ошибке
     *
     * @param errorMessage Сообщение об ошибке
     */
    public void checkErrorMessageShow(String errorMessage) {
        $(By.xpath(
                String.format("//div[contains(@class,'modal-lk-magicLinkLk opened')]//p[text()='%s']", errorMessage)))
                .shouldBe(visible);
    }


    /**
     * Отправить E-mail
     */
    public void sendEmail(String eMail) {

        inputEmail(eMail);
        log.debug("===> E-mail введён'");

        $(By.xpath("//div[@class='modal-lk']//button[text()='Получить']")).click();
        log.debug("===> Кнопка 'Получить' нажата");
    }

}
