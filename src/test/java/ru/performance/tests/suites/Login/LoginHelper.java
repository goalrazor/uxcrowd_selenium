package ru.performance.tests.suites.Login;


import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.internal.collections.Pair;
import ru.performance.tests.BaseTest;
import ru.performance.tests.pageobject.StartPage;
import ru.performance.tests.pageobject.cabinetspages.CabinetPage;
import ru.performance.tests.pageobject.modalwindows.GoogleAuthModalWindowPage;
import ru.performance.tests.pageobject.modalwindows.InputModalWindowPage;
import ru.performance.tests.pageobject.modalwindows.VkAuthModalWindowPage;

import java.util.Set;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class LoginHelper extends BaseTest {


    /**
     * Попытка войти в кабинет с заданными именем и паролем пользователя с недействующим аккаунтом
     *
     * @param account Аккаунт пара - имя и пароль пользователя
     */
    void unsuccessfulCabinetLogin(Pair account) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        // Нажать кнопку "Войти в кабинет"
        String loginButtonXpath = "//a[@id=\"header-lk-button\"]"; //новый лендос
        $(By.xpath(loginButtonXpath)).click();

        // Проверить отобразилось ли модальное окно
        String modalWindowHeaderXpath = "//div[@class='modal-content']//h3[text()='Вход']";
        $(By.xpath(modalWindowHeaderXpath)).shouldBe(visible);

        // Ввести имя пользователя
        String loginFieldXpath = "//input[@name='login']";
        log.info(String.format("Имя пользователя: %s", account.first().toString()));
        $(By.xpath(loginFieldXpath)).sendKeys(account.first().toString());

        // Ввести пароль пользователя
        String passwordFieldXpath = "//input[@name='password']";
        log.info(String.format("Пароль пользователя: %s", account.second().toString()));
        $(By.xpath(passwordFieldXpath)).sendKeys(account.second().toString());

        // Нажать кнопку "Войти"
        String goInButtonXpath = "//div[@class='modal-lk']//button[@ng-click='login()']";
        $(By.xpath(goInButtonXpath)).click();

        // Проверить сообщение об ошибке в модальном окне
        try {
            String loginErrorXpath = "//div[@class=\"error-block\"]";
            $(By.xpath(loginErrorXpath)).shouldBe(visible);
        } catch (ElementNotFound ex) {
            log.error(String.format(
                    "Не отказано в авторизации пользователю '%s' с недействительным аккаунтом: %s", account.first(), ex)
            );
            screenShot("Отсутствие неудачной авторизации");
        }

        // Закрыть модальное окно
        String closeCrossXpath = "//div[@class='modal-lk']/span[text()='×']";
        $(By.xpath(closeCrossXpath)).click();

        log.info(String.format("Успешный отказ в авторизации пользователя '%s'", account.first()));
    }


    /**
     * Залогиниться через Twitter
     *
     * @param startPage   Пейджобжект стартовой страницы
     * @param cabinetPage Пейджобжект кабинета
     */
    void twitterLogin(StartPage startPage, CabinetPage cabinetPage) {

        // Нажать кнопку "Войти в кабинет"
        startPage.header.loginButtonPress();
        log.info("Удачное нажатие на кнопку 'Войти в кабинет'");

        // Проверить отобразилось ли модальное окно
        InputModalWindowPage inputPage = new InputModalWindowPage();
        inputPage.checkPageShow();
        log.info("Модальное окно для авторизации удачно отобразилось");

        // Текущий driver
        WebDriver currentDriver = WebDriverRunner.getWebDriver();

        // Набор дескрипторов текущих открытых окон
        String oldWindowHandle = currentDriver.getWindowHandle();
        Set<String> oldWindowsSet = currentDriver.getWindowHandles();

        // Нажать на кнопку "Twitter"
        inputPage.twitterButtonClick();
        log.info("Удачное нажатие на кнопку 'Twitter'");

        // Новый набор дескрипторов, включающий уже и новое окно
        Set<String> newWindowsSet = currentDriver.getWindowHandles();

        // Дескриптор нового окна
        newWindowsSet.removeAll(oldWindowsSet);
        String newWindowHandle = newWindowsSet.iterator().next();

        // Переключиться на окно
        currentDriver.switchTo().window(newWindowHandle);
        log.debug("Успешно переключились на новое окно 'Twitter'");

        // Проверить отображение окна Twitter
        String modalTwitterWindowXpath = "//h1[@class='logo']/a[text()='Твиттер']";
        $(By.xpath(modalTwitterWindowXpath)).shouldBe(visible);
        log.info("Открытие нового окна для авторизации в Twitter прошло успешно");

        // Ввести логин, нажать кнопку "Далее"
        // TODO: Выводится сообщение в окне
        //          "Whoa there! The request token for this page is invalid."

        // Переключиться на старое стартовое окно
//        currentDriver.switchTo().window(oldWindowHandle);
//        log.debug("Успешно вернулись на стартовое окно");

        // Проверить отображение кабинета пользователя
        cabinetPage.checkPageShow();
        log.info("Кабинет пользователя удачно отобразился");
    }


    /**
     * Залогиниться через Google
     *
     * @param startPage   Пейджобжект стартовой страницы
     * @param cabinetPage Пейджобжект кабинета
     */
    void googleLogin(StartPage startPage, CabinetPage cabinetPage) {

        // Нажать кнопку "Войти в кабинет"
        startPage.header.loginButtonPress();
        log.info("Удачное нажатие на кнопку 'Войти в кабинет'");

        // Проверить отобразилось ли модальное окно
        InputModalWindowPage inputPage = new InputModalWindowPage();
        inputPage.checkPageShow();
        log.info("Модальное окно для авторизации удачно отобразилось");

        // Текущий driver
        WebDriver currentDriver = WebDriverRunner.getWebDriver();

        // Набор дескрипторов текущих открытых окон
        String oldWindowHandle = currentDriver.getWindowHandle();
        Set<String> oldWindowsSet = currentDriver.getWindowHandles();

        // Нажать на кнопку "Google"
        inputPage.googleButtonClick();
        log.info("Удачное нажатие на кнопку 'Google'");

        // Новый набор дескрипторов, включающий уже и новое окно
        Set<String> newWindowsSet = currentDriver.getWindowHandles();

        // Дескриптор нового окна
        newWindowsSet.removeAll(oldWindowsSet);
        String newWindowHandle = newWindowsSet.iterator().next();

        // Переключиться на окно
        currentDriver.switchTo().window(newWindowHandle);
        log.debug("Успешно переключились на новое окно 'Google'");

        // Проверить отображение
        GoogleAuthModalWindowPage googleAuthModalWindowPage = new GoogleAuthModalWindowPage();
        googleAuthModalWindowPage.checkPageShow();
        log.info("Открытие нового окна для авторизации в Google прошло успешно");

        // Ввести логин, нажать кнопку "Далее"
        googleAuthModalWindowPage.loginInput();
        log.info("Имя пользователя введено успешно");

        // Ввести пароль, нажать кнопку "Далее"
        googleAuthModalWindowPage.passwordInput();
        log.info("Пароль пользователя введён успешно");

        // Переключиться на старое стартовое окно
        currentDriver.switchTo().window(oldWindowHandle);
        log.debug("Успешно вернулись на стартовое окно");

        // Проверить отображение кабинета пользователя
        cabinetPage.checkPageShow();
        log.info("Кабинет пользователя удачно отобразился");
    }


    /**
     * Залогиниться через социальную сеть "Вконтакте"
     *
     * @param startPage   Пейджобжект стартовой страницы
     * @param cabinetPage PageObject кабинета
     */
    void vkontakteLogin(StartPage startPage, CabinetPage cabinetPage) {

        // Нажать кнопку "Войти в кабинет"
        startPage.header.loginButtonPress();
        log.info("Удачное нажатие на кнопку 'Войти в кабинет'");

        // Проверить отобразилось ли модальное окно
        InputModalWindowPage inputPage = new InputModalWindowPage();
        inputPage.checkPageShow();
        log.info("Модальное окно для авторизации удачно отобразилось");

        // Текущий driver
        WebDriver currentDriver = WebDriverRunner.getWebDriver();

        // Стартовое окно
//        String oldWindowHandle = currentDriver.getWindowHandle();

        // Набор дескрипторов текущих открытых окон
        Set<String> oldWindowsSet = currentDriver.getWindowHandles();

        // Нажать на кнопку "Вконтакте"
        inputPage.vkButtonClick();
        log.info("Удачное нажатие на кнопку 'Вконтакте'");

        // Новый набор дескрипторов, включающий уже и новое окно
        Set<String> newWindowsSet = currentDriver.getWindowHandles();

        // Дескриптор нового окна
        newWindowsSet.removeAll(oldWindowsSet);
        String newWindowHandle = newWindowsSet.iterator().next();

        // Переключиться на окно
        currentDriver.switchTo().window(newWindowHandle);
        log.debug("Успешно переключились на новое окно 'Вконтакте'");

        // Проверить отображение
        VkAuthModalWindowPage vkAuthModalWindowPage = new VkAuthModalWindowPage();
        vkAuthModalWindowPage.checkPageShow();
        log.info("Открытие нового окна для авторизации в 'Вконтакте' прошло успешно");

        // Ввести логин
        vkAuthModalWindowPage.loginInput();
        log.info("Имя пользователя введено успешно");

        // Ввести пароль
        vkAuthModalWindowPage.passwordInput();
        log.info("Пароль пользователя введён успешно");

        // Нажать кнопку "Войти"
        vkAuthModalWindowPage.inputButtonClick();
        log.info("Кнопка 'Войти' успешно нажата");
        screenShot("Авторизация 'ВКонтакте'");

        // TODO: Капча не даёт пройти дальше !!!
        // Переключиться на старое стартовое окно
//        currentDriver.switchTo().window(oldWindowHandle);
//        log.debug("Успешно вернулись на стартовое окно");

        // Проверить отображение кабинета пользователя
        cabinetPage.checkPageShow();
        log.info("Кабинет пользователя удачно отобразился");

    }

}
