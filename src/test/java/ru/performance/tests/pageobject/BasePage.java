package ru.performance.tests.pageobject;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.internal.collections.Pair;
import ru.performance.tests.BaseTest;

import java.util.Set;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static ru.performance.tests.BaseTest.screenShot;


public abstract class BasePage {

    protected static final Logger log = LogManager.getLogger();

    public abstract void checkPageShow();


    /**
     * Проверить отображение WEB-элемента
     *
     * @param elementName       Имя WEB-элемента в логе или отчёте
     * @param checkElementXpath XPath WEB-элемента
     */
    public void checkAnyElementShow(String elementName, String checkElementXpath) {
        log.debug(String.format(
                "===> Start method: %s. Check XPath: '%s'",
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                checkElementXpath)
        );

        try {
            SelenideElement checkElement = $(By.xpath(checkElementXpath));
            checkElement.shouldBe(visible);
            log.debug(String.format("===> Элемент '%s' успешно отобразился", elementName));
        } catch (ElementNotFound ex) {
            log.error(String.format("Элемент '%s' не отобразился: '%s'", elementName, ex.getMessage()));
            screenShot(String.format("Элемент '%s' не отобразился", elementName));
            assert false;
        }
    }


    /**
     * Войти в кабинет с заданными именем и паролем пользователя
     *
     * @param account Аккаунт пара - имя и пароль пользователя
     */
    public void cabinetLogin(Pair account) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        String loginButtonXpath =
//                    "//a[@class=\"uxc_lk mobile-lk-hidden\"]";  //старый лэндос
                "//a[@id=\"header-lk-button\"]"; //новый лендос
        String modalWindowHeaderXpath = "//div[@class='modal-content']//h3[text()='Вход']";
        try {
            // Нажать кнопку "Войти в кабинет"
            $(By.xpath(loginButtonXpath)).click();


            // Проверить отобразилось ли модальное окно
            $(By.xpath(modalWindowHeaderXpath)).shouldBe(visible);
        } catch (ElementNotFound ex) {
            log.error(String.format("Modal window not found: %s", ex));
            screenShot("Не отобразилось модальное окно для авторизации");

            log.error("===> trying to fix damned 663");
            BaseTest.goToWebsite();
            try {
                log.info("Пробуем еще раз войти в кабинет");
                // Нажать кнопку "Войти в кабинет"
                $(By.xpath(loginButtonXpath)).click();
                // Проверить отобразилось ли модальное окно
                $(By.xpath(modalWindowHeaderXpath)).shouldBe(visible);
            } catch (ElementNotFound e) {
                log.error(String.format("Modal window not found again: %s", e));
                screenShot("Не отобразилось модальное окно для авторизации");
            }
        }
        // Ввести имя пользователя
        String loginFieldXpath = "//input[@name='login']";
        log.info(String.format("Имя пользователя: %s", account.first().toString()));
        $(By.xpath(loginFieldXpath)).sendKeys(account.first().toString());

        // Ввести пароль пользователя
        String passwordFieldXpath = "//input[@name='password']";
        log.info(String.format("Пароль пользователя: %s", account.second().toString()));
        $(By.xpath(passwordFieldXpath)).sendKeys(account.second().toString());


        // Нажать кнопку "Войти"
        $(By.xpath(passwordFieldXpath)).pressEnter();


        // Проверить удачность логина
        try {
            checkPageShow();
        } catch (ElementNotFound ex) {
            log.error(String.format("===> Authorization failed: %s", ex));
            String loginErrorXpath = "//div[@class=\"error-block\"]";
            String closeCrossXpath = "//div[@class='modal-lk']/span[text()='×']";
            // Сообщение об ошибке
            $(By.xpath(loginErrorXpath)).shouldBe(visible);
            // Закрыть модальное окно
            $(By.xpath(closeCrossXpath)).click();
            screenShot("Неудачная авторизация");
            assert false;
        }

        log.info("Авторизация прошла успешно");
        if (log.getLevel() == Level.DEBUG) {
            screenShot("Отображение кабинета - удачная авторизация");
        }

    }


    /**
     * Выставить заданные чекбоксы в 'ON'
     *
     * @param checkboxesXpath       XPath чекбоксов
     * @param establishedCheckboxes Коллекция с лейблами чекбоксов
     *                              <p>
     *                              Сами чекбоксы всегда невидимы, поэтому .setSelected() и .getValue() для них не срабатывают
     */
    protected void setCheckboxes(String checkboxesXpath, Set<String> establishedCheckboxes) {
        for (String checkboxLabel : establishedCheckboxes) {
            String checkboxXpath = String.format(checkboxesXpath, checkboxLabel);
            String checkboxClassName = $(By.xpath(checkboxXpath)).attr("class");
            if (checkboxClassName.contains("ng-empty")) {
                $(By.xpath(checkboxXpath)).click();
                log.info(String.format("Чекбокс '%s' выставлен в 'ON'", checkboxLabel));
            }
        }
    }


    /**
     * Выставить заданные радиокнопки в 'ON'
     *
     * @param establishedRadiobuttonList Коллекция, содержащая ID (!!!) радиокнопок
     */
    protected void setRadiobuttons(Set<String> establishedRadiobuttonList) {

        for (String radiobuttonId : establishedRadiobuttonList) {

            String radiobuttonXpath = String.format("//input[@id='%s']", radiobuttonId);

            // Выставить радиокнопку
            $(By.xpath(radiobuttonXpath + "/following-sibling::label")).click();

            // Проверить, что выставились
            // !!! У кнопки "Не важно" после клика в Selenide не добавляется класс "ng-valid-parse" !!!
            String inputClassName = $(By.xpath(radiobuttonXpath)).getAttribute("class");
            log.debug(String.format("Класс радиокнопки c ID='%s': %s", radiobuttonId, inputClassName));
        }
    }

}
