package ru.performance.tests.suites.Login;


import com.epam.reportportal.testng.ReportPortalTestNGListener;
import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import ru.performance.config.UxCrowdConfig;
import ru.performance.tests.BaseTest;
import ru.performance.tests.pageobject.cabinetspages.*;
import ru.performance.tests.tools.ScreenShotOnFailListener;


/*
 * Дорогой друг!
 * Не нужно здесь применять DataProvider - с ним не удаётся передать в пользовательский отчёт из TestNG
 * различающиеся "description" для каждой группы данных, только для последней. Рефлексия не помогла.
 */
@Listeners({ScreenShotOnFailListener.class, ReportPortalTestNGListener.class})
public class LoginFormAuthorization extends LoginHelper {

    private static UxCrowdConfig uxCrowdConfig = ConfigFactory.create(UxCrowdConfig.class);

    @BeforeMethod(alwaysRun = true)
    public void reopenStartPage() {
        BaseTest.hostAccessibility(); //сброс кеша и кукисов
        log.info("Страница перезагружена");
    }

    @Test(description = "3.3.1 Авторизация через форму 'Войти' (Клиент)", groups = {"Prod", "A1"})
    public void clientFormLoginAuthorization() {

        // Кабинет пользователя
        CabinetPage cabinetPage = new ClientCabinetPage();

        // Войти в кабинет
        cabinetPage.cabinetLogin(getAccountPair(uxCrowdConfig.clientName(), uxCrowdConfig.clientPassword()));

        // Выйти из кабинета
        cabinetPage.cabinetLogout();
    }

    @Test(description = "3.3.2 Авторизация через форму 'Войти' (Тестировщик)", groups = {"Prod", "A1"})
    public void testerFormLoginAuthorization() {
        CabinetPage cabinetPage = new TesterCabinetPage();
        cabinetPage.cabinetLogin(getAccountPair(uxCrowdConfig.testerName(), uxCrowdConfig.testerPassword()));
        cabinetPage.cabinetLogout();
    }

    @Test(description = "3.3.3 Авторизация через форму 'Войти' (Модератор)", groups = {"Prod", "A1"})
    public void moderatorFormLoginAuthorization() {
        CabinetPage cabinetPage = new ModeratorCabinetPage();
        cabinetPage.cabinetLogin(getAccountPair(uxCrowdConfig.moderatorName(), uxCrowdConfig.moderatorPassword()));
        cabinetPage.cabinetLogout();
    }

    @Test(description = "3.3.4 Авторизация через форму 'Войти' (Администратор)", groups = {"Prod", "A1"})
    public void adminFormLoginAuthorization() {
        CabinetPage cabinetPage = new AdminCabinetPage();
        cabinetPage.cabinetLogin(getAccountPair(uxCrowdConfig.adminName(), uxCrowdConfig.adminPassword()));
        cabinetPage.cabinetLogout();
    }

    @Test(description = "3.3.5 Авторизация через форму \"Войти\" с неверным паролем", groups = {"Prod", "A1"})
    public void badPasswordFormLoginAuthorization() {
        // Попытка войти в кабинет
        unsuccessfulCabinetLogin(getAccountPair(uxCrowdConfig.clientName(), uxCrowdConfig.badClientPassword()));
    }

    @Test(description = "3.3.6 Авторизация через форму \"Войти\" с неверным логином", groups = {"Prod", "A1"})
    public void badNameFormLoginAuthorization() {
        unsuccessfulCabinetLogin(
                getAccountPair(uxCrowdConfig.badClientName(), uxCrowdConfig.clientPassword()));
    }

}
