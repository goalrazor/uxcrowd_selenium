package ru.performance.tests.pageobject.pagefragment;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.By;
import ru.performance.tests.pageobject.BasePage;

import java.util.Set;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class FooterPageFragment extends BasePage {

    // Список основных пунктов меню футера
    private final Set<String> footerMenuItems = Set.of("О продукте", "Цены", "FAQ", "Хочу тестировать сайты", "Связаться с нами");


    /**
     * Проверить отображение основных пунктов меню футера
     */
    public void checkFooterMenuItemsShow() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        String footerMenuItemXpath =
                "//div[@class=\"nl-footer-wrapper\"]/ul/li/a[text()='%s']";

        for (String item : footerMenuItems) {
            String itemXpath = String.format(footerMenuItemXpath, item);
            SelenideElement navbarItemsElement = $(By.xpath(itemXpath));
            try {
                navbarItemsElement.shouldBe(visible);
                log.info(String.format("===> Пункт меню '%s' футера успешно отобразился", item));
            } catch (ElementNotFound ex) {
                log.error(String.format("===> Не отображается пункт меню '%s' футера: '%s'", item, ex.getMessage()));
                assert false;
            }
        }
    }


    /**
     * Проверить отображение фрагмента страницы (Футера)
     */
    @Override
    public void checkPageShow() {
        checkAnyElementShow("Футер", "//div[@class=\"nl-footer ng-scope\"]");
    }

}
