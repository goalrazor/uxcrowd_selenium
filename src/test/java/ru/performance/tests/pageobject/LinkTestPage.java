package ru.performance.tests.pageobject;

public class LinkTestPage extends BasePage {

    /**
     * Проверка отображения страницы захода на тест по ссылке
     */
    @Override
    public void checkPageShow() {
        checkAnyElementShow("Страница 'Привет! Прежде, чем начать:'", "//div[text()='Привет! Прежде, чем начать:']");
    }

    public String errorXpath = "//span[@class=\"sc-EHOje byWtYL\"]";
    public String enterEmailFieldXpath = "//input[@type=\"email\"]";
    public String greenButtonReadyXpath = "//button[text()=\"Далее\"]";
}
