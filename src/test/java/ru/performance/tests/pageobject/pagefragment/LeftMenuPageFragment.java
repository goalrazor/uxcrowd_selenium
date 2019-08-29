package ru.performance.tests.pageobject.pagefragment;


import org.openqa.selenium.By;
import ru.performance.tests.pageobject.BasePage;
import ru.performance.tests.pageobject.cabinetspages.ClientCabinetPage;

import static com.codeborne.selenide.Selenide.$;


public class LeftMenuPageFragment extends BasePage {


    /**
     * Перейти в пункт меню "Новый тест"
     */
    public void goNewTestItem() {
        $(By.xpath("//p[text()='Новый тест']/ancestor::section")).click();
        log.info("Удачно перешли в левом блоке по пункту меню 'Новый тест'");
    }

    /**
     * Перейти в пункт меню "Все тесты"
     */
    public void goAllTestsItem() {
        ClientCabinetPage clientCabinetPage = new ClientCabinetPage();
        $(By.xpath("//p[text()='Все тесты']/ancestor::section")).click();
        clientCabinetPage.checkPageShow();
    }

    /**
     * Перейти в пункт меню "Черновики"
     */
    public void goDraftsItem() {
        $(By.xpath("//p[text()='Черновики']/ancestor::section")).click();
    }

    /**
     * Перейти в пункт меню "Инсайты"
     */
    public void goInsightsItem() {
        $(By.xpath("//p[text()='Инсайты']/ancestor::section")).click();
    }

    /**
     * Перейти в пункт меню "Профиль"
     */
    public void goProfileItem() {
        $(By.xpath("//section[@ui-sref='customer-profile']")).click();
    }

    /**
     * Перейти в пункт меню "Тариф"
     */
    public void goTariffItem() {
        $(By.xpath("//p[text()='Тариф']/ancestor::section")).click();
    }


    /**
     * Проверить отображение левого бокового меню
     */
    private void checkLeftMenuShow() {
        checkAnyElementShow("Левое боковое меню", "//aside[@class='left_menu']");
    }

    /**
     * Проверить отображение фрагмента страницы (Левого бокового меню)
     */
    @Override
    public void checkPageShow() {
        checkLeftMenuShow();
    }

}
