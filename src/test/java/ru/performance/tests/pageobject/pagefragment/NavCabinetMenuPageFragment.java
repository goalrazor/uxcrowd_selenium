package ru.performance.tests.pageobject.pagefragment;


import org.openqa.selenium.By;
import ru.performance.tests.pageobject.BasePage;

import static com.codeborne.selenide.Selenide.$;


public class NavCabinetMenuPageFragment extends BasePage {


    /**
     * Перейти в пункт меню "Промо-коды"
     */
    public void goPromoCodeMenuItem() {
        $(By.xpath("//a[@href='/app-moderator-home/promo-code']")).click();
        log.info("Удачно выбран пункт навигационного  меню 'Промо-коды'");
    }


    @Override
    public void checkPageShow() {
        // TODO: Пока не реализовано
    }
}
