package ru.performance.tests.pageobject.pagefragment;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.By;
import ru.performance.tests.pageobject.BasePage;

import java.util.Set;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class HeaderPageFragment extends BasePage {

    // Список пунктов главного меню
    private final Set<String> navbarItems = Set.of("О продукте", "Цены", "FAQ", "Хочу тестировать сайты");


    /**
     * Проверить отображение основных пунктов в навигационном меню
     */
    public void checkNavbarItemsShow() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        String navbarItemXpath = "//ul[@id=\"nl-header-links\"]//a[text()='%s']";

        for (String item : navbarItems) {
            String itemXpath = String.format(navbarItemXpath, item);
            SelenideElement navbarItemsElement = $(By.xpath(itemXpath));
            try {
                navbarItemsElement.shouldBe(visible);
                log.info(String.format("Пункт навигационного меню '%s' успешно отобразился", item));
            } catch (ElementNotFound ex) {
                log.error(String.format("Не отображается пункт навигационного меню '%s': '%s'", item, ex.getMessage()));
                assert false;
            }
        }
    }


    /**
     * Проверить отображение Хидера
     */
    private void checkHeaderShow() {
        checkAnyElementShow("Хидер", "//div[@class=\"nl-header ng-scope\"]");
    }


    /**
     * Проверить отображение фрагмента страницы (Хидера)
     */
    @Override
    public void checkPageShow() {
        checkHeaderShow();
    }


    /**
     * Нажать кнопку "Войти в кабинет"
     */
    public void loginButtonPress() {
        String loginButtonXpath = "//a[@id=\"header-lk-button\"]";
        $(By.xpath(loginButtonXpath)).click();
        log.debug("===> Нажата кнопка 'Войти в кабинет'");
    }

}
