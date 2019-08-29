package ru.performance.tests.suites.Smoke;


import com.epam.reportportal.testng.ReportPortalTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import ru.performance.tests.BaseTest;
import ru.performance.tests.pageobject.StartPage;
import ru.performance.tests.tools.ScreenShotOnFailListener;


@Listeners({ScreenShotOnFailListener.class, ReportPortalTestNGListener.class})
public class Smoke extends BaseTest {

    @Test(description = "12.1.1 Проверка отображения \"шапки\" сайта", groups = {"Prod", "B3"})
    public void headerDisplaying() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        StartPage startPage = new StartPage();
        startPage.checkHeaderShow();
    }

    @Test(description = "12.1.2 Проверка отображения навигационного меню", groups = {"Prod", "A3"})
    public void navbarDisplaying() {
        StartPage startPage = new StartPage();
        startPage.checkNavbarShow();
    }

    @Test(description = "12.1.3 Проверка отображения основных пунктов в навигационном меню", groups = {"Prod", "A3"})
    public void navbarItemsDisplaying() {
        StartPage startPage = new StartPage();
        startPage.checkNavbarItemsShow();
    }

    @Test(description = "12.1.4 Проверка отображения кнопки логина", groups = {"Prod", "A3"})
    public void loginButtonDisplaying() {
        StartPage startPage = new StartPage();
        startPage.checkLoginButtonShow();
    }

    @Test(description = "12.1.7 Проверка отображения \"подвала\" сайта", groups = {"Prod", "C3"})
    public void footerDisplaying() {
        StartPage startPage = new StartPage();
        startPage.checkFooterShow();
    }

    @Test(description = "12.1.8 Проверка отображения основных пунктов меню \"подвала\"", groups = {"Prod", "C3"})
    public void footerMenuItemsDisplaying() {
        StartPage startPage = new StartPage();
        startPage.checkFooterMenuItemsShow();
    }

    @Test(description = "12.1.9 Проверка отображения кнопки Твиттера в \"подвале\"", groups = {"Prod", "C3"})
    public void twitterButtonDisplaying() {
        StartPage startPage = new StartPage();
        startPage.checkTwitterButtonShow();
    }

    @Test(description = "12.1.10 Проверка отображения кнопки Фейсбука в \"подвале\"", groups = {"Prod", "C3"})
    public void facebookButtonDisplaying() {
        StartPage startPage = new StartPage();
        startPage.checkFacebookButtonShow();
    }

}
