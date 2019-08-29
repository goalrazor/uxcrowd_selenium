package ru.performance.tests.pageobject;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.openqa.selenium.By;
import ru.performance.tests.BaseTest;
import ru.performance.tests.pageobject.pagefragment.FooterPageFragment;
import ru.performance.tests.pageobject.pagefragment.HeaderPageFragment;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;


public class StartPage extends BasePage {

    // Page fragments
    public HeaderPageFragment header = new HeaderPageFragment();
    private FooterPageFragment footer = new FooterPageFragment();


    /**
     * Проверить отображение стартовой страницы по наличию логотипа
     */
    @Override
    public void checkPageShow() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        SelenideElement logoElement =
//                    $(By.xpath("//div[@class='uxc-logo']/a[@href='/']")); //старый лендос
                $(By.xpath("//a[@class=\"nl-header-logo\"]")); //Новый лендос
        try {
            logoElement.waitUntil(visible, 10000);
            log.debug("Стартовая страница удачно отобразилась");
        } catch (ElementNotFound ex) {
            log.error(String.format("===> Стартовая страница не открылась: '%s'", ex.getMessage()));
            log.info("Пробуем еще раз перезагрузить страницу");
            try {
                BaseTest.goToWebsite();
                logoElement.waitUntil(visible, 10000);
                log.debug("Стартовая страница удачно отобразилась");
            } catch (ElementNotFound e) {
                log.error(String.format("===> Стартовая страница не открылась со второго раза: '%s'", ex.getMessage()));
                assert false;
            }
        }
    }


    /**
     * Проверить отображение хидера
     */
    public void checkHeaderShow() {
        header.checkPageShow();
        log.info("Хидер удачно отобразился");
    }


    /**
     * Проверить отображение футера
     */
    public void checkFooterShow() {
        footer.checkPageShow();
        log.info("Футер удачно отобразился");
    }


    /**
     * Проверить отображение кнопки Твиттера в футере
     */
    public void checkTwitterButtonShow() {
        footer.checkAnyElementShow(
                "Кнопка Твиттера",
                "//div[@class='new-footer-wrapper']//a[@class='twitter-follow-button']"
        );
        log.info("Кнопка Твиттера в футере успешно отобразилась");
    }


    /**
     * Проверить отображение кнопки Фейсбука в футере
     */
    public void checkFacebookButtonShow() {

        // Переключиться на фрейм
        SelenideElement footerFrameElement = $(By.xpath("//iframe[@class='facebook-footer']"));
        switchTo().frame(footerFrameElement);

        // Проверить отображение кнопки
        footer.checkAnyElementShow(
                "Кнопка FaceBook",
                "//button[contains(@title,'Facebook')]"
        );
        log.info("Кнопка Facebook в футере успешно отобразилась");

        // Вернуться из фрейма
        switchTo().defaultContent();
    }


    /**
     * Проверить отображение навигационного меню
     */
    public void checkNavbarShow() {
        header.checkAnyElementShow("Навигационное меню", "//ul[@id=\"nl-header-links\"]");
        log.info("Навигационное меню успешно отобразилось");
    }


    /**
     * Проверить отображение кнопки логина
     */
    public void checkLoginButtonShow() {
        header.checkAnyElementShow("Кнопка логина", "//a[@id=\"header-lk-button\"]");
        log.info("Кнопка логина успешно отобразилась");
    }


    /**
     * Проверить отображение основных пунктов в навигационном меню
     */
    public void checkNavbarItemsShow() {
        header.checkNavbarItemsShow();
        log.info("Основные пункты в навигационном меню успешно отобразились");
    }


    /**
     * Проверить отображение основных пунктов меню футера
     */
    public void checkFooterMenuItemsShow() {
        footer.checkFooterMenuItemsShow();
        log.info("Основные пункты меню футера успешно отобразились");
    }


    /**
     * Проверить отображение плавающего блока обратной связи
     */
    public void checkFloatingFeedbackShow() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        String elementName = "Плавающий блок обратной связи";
        String checkElementXpath = ("//div[contains(@class,'feedback_minimized_label_text')]");

        checkAnyElementShow(elementName, checkElementXpath);
        log.info("Плавающий блок обратной связи успешно отобразился");
    }


    /**
     * Проверить отображение плавающего блока поддержки
     */
    public void checkFloatingSupportShow() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        String elementName = "Плавающий блок поддержки";
        String checkElementXpath = ("//span[contains(@class,'support-icon')]");

        checkAnyElementShow(elementName, checkElementXpath);
        log.info("Плавающий блок поддержки успешно отобразился");
    }

}
