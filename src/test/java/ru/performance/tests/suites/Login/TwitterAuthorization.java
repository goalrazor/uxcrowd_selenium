package ru.performance.tests.suites.Login;


import com.epam.reportportal.testng.ReportPortalTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import ru.performance.tests.pageobject.StartPage;
import ru.performance.tests.pageobject.cabinetspages.CabinetPage;
import ru.performance.tests.pageobject.cabinetspages.ClientCabinetPage;
import ru.performance.tests.tools.ScreenShotOnFailListener;


@Listeners({ScreenShotOnFailListener.class, ReportPortalTestNGListener.class})
public class TwitterAuthorization extends LoginHelper {

    @Test(description = "3.2.3 Авторизация через Twitter", groups = "A1")
    public void twitterAuthorization() {

        CabinetPage cabinetPage = new ClientCabinetPage();
        StartPage startPage = new StartPage();

        goToWebsite();
        twitterLogin(startPage, cabinetPage);
        cabinetPage.cabinetLogout();
        startPage.checkPageShow();
    }

}
