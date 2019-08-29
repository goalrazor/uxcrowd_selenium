package ru.performance.tests.pageobject.cabinetspages;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.UIAssertionError;
import com.epam.reportportal.testng.ReportPortalTestNGListener;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.internal.collections.Pair;
import ru.performance.config.UxCrowdConfig;
import ru.performance.tests.pageobject.pagefragment.LeftMenuPageFragment;
import ru.performance.tests.tools.ScreenShotOnFailListener;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static ru.performance.tests.BaseTest.getClientAccountPair;
import static ru.performance.tests.BaseTest.screenShot;


@Listeners({ScreenShotOnFailListener.class, ReportPortalTestNGListener.class})
public class ClientCabinetPage extends CabinetPage {

    final public LeftMenuPageFragment leftMenu = new LeftMenuPageFragment();
    private static UxCrowdConfig uxCrowdConfig = ConfigFactory.create(UxCrowdConfig.class);

    /**
     * Проверить отображение страницы кабинета клиента
     */

    @Override
    public void checkPageShow() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        try {
            String checkInCabinetXpath = "//a[text()='Настройки профиля']";
            SelenideElement checkElement = $(By.xpath(checkInCabinetXpath));

            checkElement.shouldBe(visible);
            log.debug("===> Страница кабинета клиента удачно открылась");
        } catch (NoSuchElementException ex) {
            log.error(String.format("===> Страница кабинета клиента не открылась: '%s'", ex.getMessage()));
            assert false;
        }

        try {
            $(By.xpath("//div[@class='block_list waiting-payment-test']//div[@class=\"item_row ng-scope\"]")).waitUntil(visible, 30000);
            log.info("Апишка 'Ожидающих оплаты' тестов загрузилась");
            $(By.xpath("//div[@class='block_list active-tests']//div[@class=\"item_row ng-scope\"]")).waitUntil(visible, 30000);
            log.info("Апишка тестов, которые выполняются загрузилась");
            $(By.xpath("//div[@class='block_list done-tests']//div[@class=\"item_row ng-scope\"]")).waitUntil(visible, 30000);
            log.info("Апишка завершенных тестов загрузилась");
        } catch (NoSuchElementException ex) {
            log.error("Одна или несколько апишек не загрузились");
            assert false;
        }
    }


    /**
     * Проверить отображение профиля клиента
     */
    public void checkProfileShow() {
        String checkProfileLabel = "//span[text()='Профиль']";

        try {
            SelenideElement checkElement = $(By.xpath(checkProfileLabel));
            checkElement.shouldBe(visible);
            log.debug("===> Профиль клиента удачно отобразился");
        } catch (NoSuchElementException ex) {
            log.error(String.format("===> Профиль клиента не отобразился: '%s'", ex.getMessage()));
            assert false;
        }
        log.info("Профиль клиента удачно отобразился");

    }


    /**
     * Авторизоваться клиентом в кабинете
     */
    public void login() {
        cabinetLogin(getClientAccountPair());
    }

    /**
     * Авторизоваться клиентом в кабинете
     */
    public void login(Pair accountPair) {
        cabinetLogin(accountPair);
    }


    /**
     * Выйти из кабинета клиента
     */
    @Override
    public void cabinetLogout() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        // Нажать кнопку "Выйти"
        String goOutButtonXpath = "//div[@id='logout']";
        clearBrowserLocalStorage();
        refresh();
        try {
            $(By.xpath(goOutButtonXpath)).shouldBe(visible).click();
        } catch (ElementNotFound ex) {
            screenShot("Log out not visible");
            log.error("Кнопка логаут НЕ отобразилась!");
        }

        log.info("Кнопка 'Выйти' в кабинете пользователя удачно нажата");
    }


    /**
     * Перейти в профиль клиента через меню в левом блоке
     */
    public void goProfileMenu() {
        leftMenu.goProfileItem();
        log.info("Кнопка 'Профиль' в кабинете пользователя удачно нажата");
    }


    /**
     * Ввести название сегмента целевой аудитории
     *
     * @param testSegmentName Название сегмента целевой аудитории
     */
    public void setSegmentName(Integer segmentNumber, String testSegmentName) {

        String segmentNameInputFieldXpath
                = "//input[@data-autotest-id=\"text_input_groupName_%s\"]";
        SelenideElement segment = $(By.xpath(String.format(segmentNameInputFieldXpath, (segmentNumber - 1))));
        segment.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        segment.sendKeys(Keys.BACK_SPACE);
        segment.setValue(testSegmentName);
        log.info(String.format("Название сегмента целевой аудитории '%s' введено успешно", testSegmentName));
    }


    /**
     * Выставить количество пользователей
     *
     * @param userNumber Количество пользователей
     */
    public void setUserNumber(Integer segmentNumber, Integer userNumber) {
        if (userNumber != 0) {
            String userNumberInputFieldXpath = "//input[@data-autotest-id=\"text_input_testerCount\"]";
            SelenideElement userNumberInputField = $$(By.xpath(userNumberInputFieldXpath)).get(segmentNumber - 1);
            userNumberInputField.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            userNumberInputField.sendKeys(userNumber.toString());
            log.info(String.format("Введено количество пользователей '%d' в сегменте '%d'", userNumber, segmentNumber));
        }
    }


    /**
     * Выставить значения дохода на вкладке "Доход"
     *
     * @param minProfit Минимальный доход
     * @param maxProfit Максимальный доход
     */
    public void setProfit(Integer segmentNumber, String minProfit, String maxProfit) {

        // Выставить значения
        setProfitValues(segmentNumber, Integer.parseInt(minProfit), Integer.parseInt(maxProfit));
        log.info(String.format(
                "Значения минимального (%s) и максимального (%s) доходов выставлены", minProfit, maxProfit));
    }


    /**
     * Выставить значения максимального и минимального количества лет на вкладке "Возраст"
     *
     * @param segmentNumber Название сегмента
     * @param minAge        Минимальный возраст
     * @param maxAge        Максимальный возраст
     */
    public void setAge(Integer segmentNumber, String minAge, String maxAge) {
        // Выставить значения
        setAgeValues(segmentNumber, Integer.parseInt(minAge), Integer.parseInt(maxAge));
        log.info(String.format(
                "Значения минимального (%s) и максимального (%s) возраста выставлены", minAge, maxAge));
    }


    /**
     * Получить селениум элемент дорожки слайдера
     *
     * @param segmentNumber номер сегмента
     * @return дорожка слайдера
     */
    private SelenideElement sliderTrack(Integer segmentNumber) {
        String sliderRoute = "//div[@class=\"input-range__track input-range__track--background\"]";
        return $$(By.xpath(sliderRoute + "[1]")).get(segmentNumber - 1);
    }

    /***
     * Получить элемент самого ползунка
     * @param segmentNumber номер сегмента
     * @return ползунок
     */
    private ElementsCollection sliderElement(Integer segmentNumber) {
        String sliderXpath = "//span[@class=\"input-range__slider-container\"]";
        return $$(By.xpath(sliderXpath));
    }


    /**
     * получить шаг ползунка для дохода по дорожке
     *
     * @param segmentNumber номер сегмента
     * @return шаг ползунка дохода
     */
    private Integer getSliderStep(Integer segmentNumber, int sliderNum) {
        int valueOfSteps;
        if (sliderNum <= 1 || (sliderNum >= 4 && sliderNum <= 5) || (sliderNum >= 8 && sliderNum <= 9) || (sliderNum >= 12 && sliderNum <= 13)) {
            valueOfSteps = 9;
        } else {
            valueOfSteps = 42;
        }

        return sliderTrack(segmentNumber).getSize().width / valueOfSteps;
    }

    /**
     * Получить текущую позицию слайдера дохода
     *
     * @param segmentNumber номер сегмента
     * @return значение от 1-го до 11-ти
     */
    private Integer getCurrentSliderPosition(Integer segmentNumber, int sliderNum) {
        //sliderNum = sliderNum - 1;
        int sliderCenterPx = (int) Double.parseDouble(sliderElement(segmentNumber).get(sliderNum).getCssValue("left").replaceAll("px", ""));
        return sliderCenterPx / getSliderStep(segmentNumber, sliderNum) + 1;
    }

    /**
     * Установить слайдер дохода в позицию
     *
     * @param segmentNumber номер сегмента
     * @param position      - значение от 1-го до 11-ти
     */
    private void setSliderPosition(Integer position, Integer segmentNumber, int sliderNum) {
        System.out.println("номер слайдера " + sliderNum);
        //sliderNum = sliderNum - 1;
        int xOffset = (position - getCurrentSliderPosition(segmentNumber, sliderNum)) * getSliderStep(segmentNumber, sliderNum);
        Actions actions = new Actions(WebDriverRunner.getWebDriver());
        actions.dragAndDropBy(sliderElement(segmentNumber).get(sliderNum), xOffset, 0).perform();

    }

    private int sliderNum = 0;

    /**
     * Выставить минимальное и максимальное значения зарплаты в двойном поле ввода
     *
     * @param segmentNumber номер сегмента
     * @param minValue      Минимальное значение
     * @param maxValue      Максимальное значение
     */
    private void setProfitValues(
            Integer segmentNumber, int minValue, int maxValue) {

        // Минимальное значение

        setSliderPosition(minValue / 5000, segmentNumber, sliderNum);
        sliderNum++;
        // Максимальное значение
        setSliderPosition(maxValue / 5000, segmentNumber, sliderNum);
        sliderNum++;


    }

    /**
     * Выставить минимальное и максимальное значения возраста в двойном поле ввода
     *
     * @param segmentNumber номер сегмента
     * @param minValue      Минимальное значение
     * @param maxValue      Максимальное значение
     */
    private void setAgeValues(
            Integer segmentNumber, int minValue, int maxValue) {

        setSliderPosition(minValue - 17, segmentNumber, sliderNum);
        sliderNum++;
        setSliderPosition(maxValue - 17, segmentNumber, sliderNum);
        sliderNum++;

    }

    /**
     * Выставить все чекбоксы на странице
     *
     * @param establishedCheckboxesList список всех чекбоксов которые должны быть активны
     */
    public void setCheckboxes(Set<String> establishedCheckboxesList, int segmentNum) {
        Set<String> allSocialStatusCheckboxes = new HashSet<>(Set.of(
                "Студенты",
                "Безработные",
                "Домохозяйки",
                "Фрилансеры",
                "Рабочие любой квалификации",
                "Специалисты с высшим образованием без подчиненных",
                "Руководители отделов",
                "Неполное общее (9 классов)",
                "Полное общее (среднее)",
                "Среднее профессиональное",
                "Неоконченное высшее",
                "Высшее"
        ));

        allSocialStatusCheckboxes.removeAll(establishedCheckboxesList);
        String labelXpath = "//span[text()=\"%s\"]";
        for (var label : allSocialStatusCheckboxes) {
            sleep(300);
            $$(By.xpath(String.format(labelXpath, label))).get(segmentNum - 1).click();
            log.info("'{}' выставлен в сегменте '{}'", label, segmentNum);
        }

    }

    /**
     * Выставить радиокнопки на странице
     *
     * @param establishedButtonLabelsList список всех радиокнопок
     */
    public void setButtonsInSection(Set<String> establishedButtonLabelsList, int segmentNumber) {
        String labelXpath = "//span[text()=\"%s\"]";
        String ancestorSexXpath = "//div[@data-autotest-id=\"radio_group_testerGender_%s\"]";
        String ancestorFamilyLeftXpath = "//div[@data-autotest-id=\"radio_group_testerFamilyChildren_%s\"]";
        String ancestorFamilyRightXpath = "//div[@data-autotest-id=\"radio_group_testerFamilyStatus_%s\"]";

        for (var label : establishedButtonLabelsList) {
            switch (label) {
                case "Мужской":
                case "Женский":
                    $(By.xpath(String.format(ancestorSexXpath + labelXpath, (segmentNumber - 1), label))).click();
                    break;
                case "С детьми":
                case "Без детей":
                    $(By.xpath(String.format(ancestorFamilyLeftXpath + labelXpath, (segmentNumber - 1), label))).click();
                    break;
                case "Не замужем / не женат":
                case "Замужем / женат":
                    $(By.xpath(String.format(ancestorFamilyRightXpath + labelXpath, (segmentNumber - 1), label))).click();
                    break;
            }
            sleep(300);
            String establishedLabelsXpath = String.format(labelXpath, label);
            $(By.xpath(establishedLabelsXpath)).click();
            log.info("'{}' выставлен в сегменте '{}'", label, segmentNumber);
        }

    }

    /**
     * Нажать кнопку "Сохранить и продолжить"
     */
    public void saveAndNextButtonClick() {
        String saveAndNextButtonXpath = "//button[text()=\"К заданиям\"]";
        $(By.xpath(saveAndNextButtonXpath)).click();
        log.info("Кнопка 'К заданиям' нажата");
    }


    /**
     * Нажать кнопку "Добавить сегмент"
     */
    public void addSegmentButtonClick() {
        String addSegmentButtonXpath = "//a[text()=\"Добавить еще один сегмент\"]";
        $(By.xpath(addSegmentButtonXpath)).click();
        sleep(1000);
        log.info("Кнопка 'Добавить сегмент' нажата");
    }

    public void ownAudienceClick() {
        String ownAudienceButtonXpath = "//div[text()=\"Найду пользователей сам\"]";
        $(By.xpath(ownAudienceButtonXpath)).click();
        log.info("Кнопка 'Найду пользователей сам' нажата");
    }

    /**
     * Ввести "Название" в описание теста
     *
     * @param testName Название теста
     */
    public void setTestName(String testName) {
        String testNameInputFieldXpath = "//input[@data-autotest-id=\"text_input_testNameInput\"]";
        $(By.xpath(testNameInputFieldXpath)).sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $(By.xpath(testNameInputFieldXpath)).sendKeys(Keys.BACK_SPACE);
        $(By.xpath(testNameInputFieldXpath)).clear();
        $(By.xpath(testNameInputFieldXpath)).setValue(testName);
        log.info("Название '{}' в описание теста введено", testName);
    }


    /**
     * На вкладке "Что тестируем?" указать тип теста и ссылку на тестируемое приложение
     *
     * @param testType            Тип теста
     * @param testApplicationLink Ссылка на тестируемое приложение
     */
    public void toggleTestTypeAndLink(String testType, String testApplicationLink) {
        String testTypeToggleButtonXpath =
                String.format("//div[text()=\"%s\"]", testType);
        $(By.xpath(testTypeToggleButtonXpath)).click();
        String linkInputFieldXpath = "//input[@data-autotest-id=\"text_input_testTargetURL\"]";
        $(By.xpath(linkInputFieldXpath)).sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $(By.xpath(linkInputFieldXpath)).sendKeys(Keys.BACK_SPACE);

        $(By.xpath(linkInputFieldXpath)).clear();
        $(By.xpath(linkInputFieldXpath)).setValue(testApplicationLink);
        log.info("Указан линк на тестируемое приложение '{} 'на вкладке 'Что тестируем?'", testApplicationLink);
    }

    /**
     * Ввести название приложения
     *
     * @param androidAppName название приложения
     */
    public void setAndroidAppName(String androidAppName) {
        String androidAppNameXpath = "//input[@data-autotest-id=\"text_input_testTargetName\"]";
        $(By.xpath(androidAppNameXpath)).sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $(By.xpath(androidAppNameXpath)).sendKeys(Keys.BACK_SPACE);
        $(By.xpath(androidAppNameXpath)).clear();
        $(By.xpath(androidAppNameXpath)).sendKeys(androidAppName);
        log.info("Название '{}' в описание теста введено", androidAppName);
    }

    /**
     * Нажать кнопку "К выбору аудитории"
     */
    public void nextButton() {
        sleep(5000);
        String audienceChoiceButtonXpath = "//button[text()=\"К выбору аудитории\"]";
        $(By.xpath(audienceChoiceButtonXpath)).click();
        log.info("Нажата кнопка 'К выбору аудитории'");
        String addSegmentButtonXpath = "//div[text()=\"Откуда пользователи\"]";
        $(By.xpath(addSegmentButtonXpath)).waitUntil(visible, 30000);
    }

    /**
     * Нажать кнопку "Проверка и запуск"
     */
    public void addAndStartButtonClick() {
        sleep(2000);
        $(By.xpath("//button[text()=\"Проверка и запуск\"]")).click();
        log.info("Нажата кнопка 'Проверка и запуск'");
    }

    /**
     * Ввести "Введение для пользователей"
     *
     * @param userIntroduction Текст введения для пользователей
     */
    public void setUserIntroduction(String userIntroduction) {
        String userIntroductionTextareaXpath =
                "//textarea";
        $(By.xpath(userIntroductionTextareaXpath)).sendKeys(Keys.chord(Keys.CONTROL, "a"));
        $(By.xpath(userIntroductionTextareaXpath)).sendKeys(Keys.BACK_SPACE);
        $(By.xpath(userIntroductionTextareaXpath)).clear();
        $(By.xpath(userIntroductionTextareaXpath)).setValue(userIntroduction);
        log.info("Введено 'Введение для пользователей'");
    }


    /**
     * Ввести тип задания
     *
     * @param taskType Тип задания
     */
    public void setTaskType(String taskType, int numOfTask) {
        String typeTaskTabXpath
                = String.format("//div[text()=\"%s\"]", taskType);
        $$(By.xpath(typeTaskTabXpath)).get(numOfTask).click();
        log.info(String.format("Указан тип задания '%s'", taskType));
    }


    /**
     * Добавить задание
     *
     * @param taskText Текст задания
     */
    public void addTask(String taskText) {
        String taskTextareaXpath = "//textarea";
        SelenideElement taskTextArea = $(By.xpath(taskTextareaXpath));
        taskTextArea.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        taskTextArea.sendKeys(Keys.BACK_SPACE);
        taskTextArea.setValue(taskText);
        log.info(String.format("Добавлено задание '%s'", taskText));

    }

    /**
     * клик по кнопке "добавить"
     */
    public void clickAddButton() {
        String taskAddButtonXpath = "//button[@data-autotest-id=\"tasks_submit_task\"]";
        sleep(1500);
        $(By.xpath(taskAddButtonXpath)).click();
        log.info("Добавлено задание");
    }

    /**
     * выставление текста к рейтинговому заданию
     *
     * @param negativeText текст негативной границы
     * @param positiveText текст позитивной границы
     */
    public void addRatingText(String negativeText, String positiveText) {
        SelenideElement ratingNegativeFieldElement = $(By.xpath("//input[@data-autotest-id=\"text_input_tasks_rating_variant_0\"]"));
        SelenideElement ratingPositiveFieldElement = $(By.xpath("//input[@data-autotest-id=\"text_input_tasks_rating_variant_1\"]"));
        ratingNegativeFieldElement.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        ratingNegativeFieldElement.sendKeys(Keys.BACK_SPACE);
        ratingNegativeFieldElement.sendKeys(negativeText);
        ratingPositiveFieldElement.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        ratingPositiveFieldElement.sendKeys(Keys.BACK_SPACE);
        ratingPositiveFieldElement.sendKeys(positiveText);
    }

    /**
     * добавление вариантов ответа
     *
     * @param chooseOneText список вариантов
     */
    public void chooseStrings(Set<String> chooseOneText) {
        String fieldXpath = "//input[@data-autotest-id=\"text_input_tasks_variant_%s\"]";
        int i = 0;
        for (String text : chooseOneText) {
            SelenideElement element = $(By.xpath(String.format(fieldXpath, i++)));
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.BACK_SPACE);
            element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            element.sendKeys(Keys.BACK_SPACE);
            element.setValue(text);
            log.info("Добавлен вариант ответа '{}' для выбора", text);
        }

    }


    /**
     * Проверить имя и ссылку на тестируемое приложение
     *
     * @param testLink Имя и линк - они одинаковые
     */
    public void checkLink(String testLink) {
        String hostNameXpath = String.format("//p[text()='%s']", testLink);
        $(By.xpath(hostNameXpath)).shouldBe(visible);
        log.info("Имя и ссылка на тестируемое приложение соответствуют данным, указанным клиентом");
    }

    /**
     * проверить двойные значения
     *
     * @param minvalue минимальное
     * @param maxValue максимальное
     */
    public void checkDoubleValues(String minvalue, String maxValue) {
        String xpath = "//div[text()=\"%s\"][text()=\"%s\"]";
        $(By.xpath(String.format(xpath, minvalue, maxValue))).shouldBe(visible);
        log.info("Значения от '{}' до '{}' соответствуют значениям, указанным клиентом", minvalue, maxValue);
    }

    /**
     * проверить значения
     *
     * @param establishedBoxes значения для проверки
     */
    public void checkOtherValues(Set<String> establishedBoxes) {
        String xpath = "//div[contains(text(), \"%s\")] | //div[text()=\"%s\"]";
        for (String label : establishedBoxes) {
            $(By.xpath(String.format(xpath, label, label))).shouldBe(visible);
            log.info("Проверено присутствие '{}' на странице", label);
        }
    }

    /**
     * Проверить Описание для тестировщика
     *
     * @param userIntroduction Текст описания для тестировщика
     */
    public void checkUserIntroduction(String userIntroduction) {
        String descriptionForTesterXpath = "//p[text()=\"%s\"]";
        $(By.xpath(String.format(descriptionForTesterXpath, userIntroduction))).shouldBe(visible);
        log.info("'Описание для тестировщика' соответствует 'Введению для пользователя'");
    }


    /**
     * Проверить соответствие текста задания тексту, введённому клиентом
     *
     * @param taskText Текст задания
     */
    public void checkTextTask(String taskText) {
        String checkTasksXpath = "//p[text()=\"%s\"]";
        $(By.xpath(String.format(checkTasksXpath, taskText))).shouldBe(visible);
        log.info(String.format("Название '%s' задания соответствует введённому клиентом", taskText));
    }

    /**
     * Проверка рейтингового задания
     *
     * @param taskText текст описания задания
     * @param minValue значение негативного рейтинга
     * @param maxValue значение позитивного рейтинга
     */
    public void checkRatingTask(String taskText, String minValue, String maxValue) {
        checkTextTask(taskText);
        String valueXpath = "//span[text()=\"%s\"]";
        $(By.xpath(String.format(valueXpath, minValue))).shouldBe(visible);
        $(By.xpath(String.format(valueXpath, maxValue))).shouldBe(visible);
        log.info(String.format("Минимальное '%s' и максимальное '%s' значения рейтинга соответствуют заданию", minValue, maxValue));
    }

    /**
     * Проверка полей с множеством строк
     *
     * @param taskText         текст описания задания
     * @param establishedBoxes список значений, которые должны отображаться
     */
    public void checkManyBoxes(String taskText, Set<String> establishedBoxes) {
        checkTextTask(taskText);
        String boxesXpath = "//span[text()=\"%s\"]";
        for (String boxes : establishedBoxes) {
            $(By.xpath(String.format(boxesXpath, boxes))).shouldBe(visible);
            log.info("Вариант выбора '{}' соответствует заданию", boxes);
        }
    }


    /**
     * Проверит соответствие стоимости той величине, что ввёл пользователь
     *
     * @param price Стоимость
     */
    public void checkPrice(String price) {
        String priceXpath = "//div[text()=\"%s\"]";
        $(By.xpath(String.format(priceXpath, price))).shouldBe(visible);
        log.info(String.format("Отобразилась верная стоимость: '%s'", price));
    }

    /**
     * клик по кнопке "Есть промокод?" с проверкой отображения поля для ввода
     */
    public void promoCodeClick() {
        String promoCodeButtonXpath = "//a[text()=\"Есть промокод?\"]";
        $(By.xpath(promoCodeButtonXpath)).click();
        log.info("Клик по ссылке 'Есть промокод?' совершен");
        String promoCodeFieldXpath = "//p[text()=\"Введите промокод\"]";
        $(By.xpath(promoCodeFieldXpath)).shouldBe(visible);
        log.info("Поле для ввода промокода отобразилось");
    }

    /**
     * ввести промокод в поле для промокода
     *
     * @param promoCode проиокод
     */
    public void enterPromoCode(String promoCode) {
        String promoCodeButtonXpath = "//a[text()=\"Есть промокод?\"]";
        $(By.xpath(promoCodeButtonXpath)).click();
        $(By.xpath("//input[@data-autotest-id=\"text_input_undefined\"]")).sendKeys(promoCode);
        log.info("Введен промкод '{}'", promoCode);
        $(By.xpath("//button[text()=\"Применить\"]")).click();
        log.info("Нажата кнопка 'Применить'");

    }

    /**
     * Нажать кнопку Оплатить и запустить
     */
    public void payAndStartButton(ClientCabinetPage clientCabinetPage) {
        String payAndStartButtonXpath = "//button[contains(text(), \"рублей и запустить тест\")]";
        try {
            $(By.xpath(payAndStartButtonXpath)).waitUntil(enabled, 10000).click();
            log.info("Клик по 'Оплатить и запустить тест' совершен");
            clientCabinetPage.insertCardRequisites(uxCrowdConfig.cardNumber(), uxCrowdConfig.cardExpiredDate(), uxCrowdConfig.cardCVCCode());
        } catch (ElementNotFound e) {
            log.info("Не нашел кнопку с запуском. Возможно бесплатный тест? Проверяю...");
            try {
                String noNeedToPayButtonXpath = "//button[text()=\"Запустить бесплатный тест\"]";
                $(By.xpath(noNeedToPayButtonXpath)).waitUntil(enabled, 10000).click();
                log.info("Да! Бесплатный тест! Клик совершен.");
            } catch (ElementNotFound ex) {
                log.error("Нет. Кнопка с бесплатным запуском не найдена. Тест не бесплатный. А значит - ERROR");
                ex.getStackTrace();
                throw new NoSuchElementException("Элемент не найден");
            }
        }

    }

    /**
     * Выставляет роль в профиле: Юридическое или Физическое лицо
     *
     * @param roleType Юридическое лицо/Физическое лицо
     */
    public void setRoleType(String roleType) {
        String roleSelectorXpath = "//span[@class=\"ui-selectmenu-icon ui-icon ui-icon-triangle-1-s\"]";
        String roleXpath = "//div[text()='%s']";
        $(By.xpath(roleSelectorXpath)).click();
        $(By.xpath(String.format(roleXpath, roleType))).click();
        log.info("Роль профиля выставлена - '{}'", roleType);

    }


    public String companyNameXpath = "//input[@ng-model=\"customerProfile.companyName\"]";
    public String bioEntityXpath = "//form[@name=\"OOO\"]//input[@ng-model=\"customerProfile.fio\"]";
    public String bioIndividualXpath = "//form[@name=\"FL\"]//input[@ng-model=\"customerProfile.fio\"]";

    public String documentXpath = "//input[@ng-model=\"customerProfile.document\"]";
    public String taxNumberEntityXpath = "//form[@name=\"OOO\"]//input[@ng-model=\"customerProfile.inn\"]";
    public String taxNumberIndividualXpath = "//form[@name=\"FL\"]//input[@ng-model=\"customerProfile.inn\"]";

    public String kppXpath = "//input[@ng-model=\"customerProfile.kpp\"]";
    public String ogrnXpath = "//input[@ng-model=\"customerProfile.ogrn\"]";
    public String okpoXpath = "//input[@ng-model=\"customerProfile.okpo\"]";
    public String lawAddressEntityXpath = "//form[@name=\"OOO\"]//input[@ng-model=\"customerProfile.lawAddress\"]";
    public String lawAddressIndividualXpath = "//form[@name=\"FL\"]//input[@ng-model=\"customerProfile.lawAddress\"]";

    public String postAddressXpath = "//input[@ng-model=\"customerProfile.postAddress\"]";
    public String accountXpath = "//input[@ng-model=\"customerProfile.account\"]";
    public String bankXpath = "//input[@ng-model=\"customerProfile.bank\"]";
    public String corrXpath = "//input[@ng-model=\"customerProfile.corrAccount\"]";
    public String bicXpath = "//input[@ng-model=\"customerProfile.bic\"]";
    public String phoneXpath = "//input[@ng-model=\"customerProfile.phone\"]";
    public String snilsXpath = "//input[@ng-model=\"customerProfile.snils\"]";
    public String passSeriesNumXpath = "//input[@ng-model=\"customerProfile.passSeriesNum\"]";
    public String passGetNumXpath = "//input[@name=\"get\"]";


    public String errorCompanyFieldMessageXpath = "//p[text()=\"Введите Название организации\"]";
    public String errorBioFieldXpath = "//p[contains(text(), \"Введите фамилию имя\")]";
    public String errorDocumentFieldXpath = "//p[text()=\"Введите номер доверенности\"]";
    public String errorTaxNumberFieldXpath = "//form[@name=\"OOO\"]//p[contains(text(), \"Введите ИНН\")]";
    public String errorTaxNumberIndividualXpath = "//form[@name=\"FL\"]//p[contains(text(), \"Введите ИНН\")]";
    public String errorKppFieldXpath = "//p[contains(text(), \"Введите КПП\")]";
    public String errorOgrnFieldXpath = "//p[contains(text(), \"Введите ОГРН\")]";
    public String errorOkpoFieldXpath = "//p[contains(text(), \"Введите ОКПО\")]";
    public String errorLawAddressFieldXpath = "//p[text()=\"Введите Юридический адрес\"]";
    public String errorRegistrationAddressXpath = "//p[text()=\"Введите адрес регистрации\"]";
    public String errorPostAddressFieldXpath = "//p[text()=\"Введите Фактический адрес\"]";
    public String errorAccountFieldXpath = "//p[contains(text(), \"Введите Расчетный счет\")]";
    public String errorBankFieldXpath = "//p[text()=\"Введите Банк\"]";
    public String errorCorrXpath = "//p[contains(text(), \"Введите кор.счет\")]";
    private String emptyCorrXpath = "//p[contains(text(), \"Введите Кор. счет\")]";
    public String errorBicXpath = "//p[contains(text(), \"Введите БИК\")]";
    public String errorPhoneXpath = "//p[text()=\"Введите Телефон\"]";
    public String errorSnilsXpath = "//p[contains(text(), \"Введите СНИЛС\")]";
    public String errorPassSeriesXpath = "//p[contains(text(), \"Введите серию и номер паспорта\")]";
    public String errorPassGetXpath = "//p[contains(text(), \"Введите кем и когда выдан\")]";


    public boolean isPassedFlag = true;

    /**
     * Проверка видимости ошибки ввода некоректных данных
     *
     * @param errorFieldXpath локатор поля с собщением об ошибке
     */
    public void errorFieldMessageVisibility(String errorFieldXpath) {
        try {
            $(By.xpath(errorFieldXpath)).shouldBe(visible);
            log.info("Ошибка отображена по XPATH '{}'", errorFieldXpath);
        } catch (UIAssertionError error) {
            log.error("Сообщение об ошибке не высветилось по XPATH '{}'", errorFieldXpath);
            isPassedFlag = false;
            error.printStackTrace();
        }
    }

    /**
     * Проверка, что ошибка ввода некоректных данных не появилась
     *
     * @param errorFieldXpath локатор поля с собщением об ошибке
     */
    public void errorFieldMessageNotVisibility(String errorFieldXpath) {
        try {
            $(By.xpath(errorFieldXpath)).shouldNotBe(visible);
            log.info("Ошибка по XPATH '{}' не отобразилась", errorFieldXpath);
        } catch (UIAssertionError error) {
            log.error("Сообщение об ошибке высветилось по XPATH '{}'", errorFieldXpath);
            isPassedFlag = false;
            error.printStackTrace();
        }
    }

    /**
     * Очищает все поля профиля Юридического лица и проверяет, что они окрасились красным
     */
    public void clearAllFieldsEntity() {
        //поле "Название организации"
        companyNameClear();
        errorFieldMessageVisibility(errorCompanyFieldMessageXpath);

        //поле "ФИО"
        bioEntityClear();
        errorFieldMessageVisibility(errorBioFieldXpath);

        //поле "Действует на основании доверенности №"
        documentClear();
        errorFieldMessageVisibility(errorDocumentFieldXpath);

        //поле "ИНН"
        innEntityClear();
        errorFieldMessageVisibility(errorTaxNumberFieldXpath);

        //поле "КПП"
        kppClear();
        errorFieldMessageVisibility(errorKppFieldXpath);

        //поле "ОГРН"
        ogrnClear();
        errorFieldMessageVisibility(errorOgrnFieldXpath);

        //поле "ОКПО"
        okpoClear();
        errorFieldMessageVisibility(errorOkpoFieldXpath);

        //поле "Юридический адрес"
        lawAddressClear();
        errorFieldMessageVisibility(errorLawAddressFieldXpath);

        //поле "Фактический адрес" не должно гореть красным и может быть пустым
        postAddressClear();
        errorFieldMessageNotVisibility(errorPostAddressFieldXpath);

        //поле "Расчетный счет"
        accountClear();
        errorFieldMessageVisibility(errorAccountFieldXpath);

        //поле "Банк" не должно гореть красным и может быть пустым
        bankClear();
        errorFieldMessageNotVisibility(errorBankFieldXpath);

        //поле "Корреспондентский счет"
        corrClear();
        errorFieldMessageVisibility(emptyCorrXpath);

        //поле "БИК"
        bicClear();
        errorFieldMessageVisibility(errorBicXpath);

        //поле "Телефон"
        phoneClear();
        errorFieldMessageNotVisibility(errorPhoneXpath);
    }

    /**
     * Очищает все поля профиля Физического лица и проверяет, что они окрасились красным
     */
    public void clearAllFieldsIndividual() {
        //поле "ФИО"
        bioIndividualClear();
        errorFieldMessageVisibility(errorBioFieldXpath);

        //поле "Адрес регистрации"
        registerAddressClear();
        errorFieldMessageVisibility(errorRegistrationAddressXpath);

        //поле "ИНН"
        innIndividualClear();
        errorFieldMessageVisibility(errorTaxNumberIndividualXpath);

        //поле "СНИЛС"
        snilsClear();
        errorFieldMessageVisibility(errorSnilsXpath);

        //поле "Номер и серия паспорта"
        passSeriesClear();
        errorFieldMessageVisibility(errorPassSeriesXpath);

        //поле "Номер и серия паспорта"
        passGetClear();
        errorFieldMessageVisibility(errorPassGetXpath);
    }

    /**
     * Очищение поля компания
     */
    private void companyNameClear() {
        $(By.xpath(companyNameXpath)).clear();
        log.info("'Название организации' удалено");
    }

    /**
     * Очищение поля фио юр лица
     */
    private void bioEntityClear() {
        $(By.xpath(bioEntityXpath)).clear();
        log.info("'ФИО' удалено");
    }

    /**
     * Очищение поля фио физ лица
     */
    private void bioIndividualClear() {
        $(By.xpath(bioIndividualXpath)).clear();
        log.info("'ФИО' удалено");
    }

    /**
     * Очищение поля Доверенность
     */
    private void documentClear() {
        $(By.xpath(documentXpath)).clear();
        log.info("'Действует на основании доверенности №' удалено");
    }

    /**
     * Очищение поля кпп
     */
    private void kppClear() {
        $(By.xpath(kppXpath)).clear();
        log.info("'КПП' удалено");
    }

    /**
     * Очищение поля ИНН юр лица
     */
    private void innEntityClear() {
        $(By.xpath(taxNumberEntityXpath)).clear();
        log.info("'ИНН' удалено");
    }

    /**
     * Очищение поля ИНН физ лицо
     */
    private void innIndividualClear() {
        $(By.xpath(taxNumberIndividualXpath)).clear();
        log.info("'ИНН' удалено");
    }

    /**
     * Очищение поля ОГРН
     */
    private void ogrnClear() {
        $(By.xpath(ogrnXpath)).clear();
        log.info("'ОГРН' удалено");
    }

    /**
     * Очищение поля ОКПО
     */
    private void okpoClear() {
        $(By.xpath(okpoXpath)).clear();
        log.info("'ОКПО' удалено");
    }

    /**
     * Очищение поля Юридический адрес
     */
    private void lawAddressClear() {
        $(By.xpath(lawAddressEntityXpath)).clear();
        log.info("'Юридический адрес' удалено");
    }

    /**
     * Очищение поля Адрес регистрации
     */
    private void registerAddressClear() {
        $(By.xpath(lawAddressIndividualXpath)).clear();
        log.info("'Адрес регистрации' удалено");
    }

    /**
     * Очищение поля Фактический адрес
     */
    private void postAddressClear() {
        $(By.xpath(postAddressXpath)).clear();
        log.info("'Фактический адрес' удалено");
    }

    /**
     * Очищение поля Расчетный счет
     */
    private void accountClear() {
        $(By.xpath(accountXpath)).clear();
        log.info("'Расчетный счет' удалено");
    }

    /**
     * Очищение поля Банк
     */
    private void bankClear() {
        $(By.xpath(bankXpath)).clear();
        log.info("'Банк' удалено");
    }

    /**
     * Очищение поля Кор счет
     */
    private void corrClear() {
        $(By.xpath(corrXpath)).clear();
        log.info("'Корреспондентский счет' удалено");
    }

    /**
     * Очищение поля БИК
     */
    private void bicClear() {
        $(By.xpath(bicXpath)).clear();
        log.info("'БИК' удалено");
    }

    /**
     * Очищение поля Телефон
     */
    private void phoneClear() {
        $(By.xpath(phoneXpath)).clear();
        log.info("'Телефон' удалено");
    }

    /**
     * Очищение поля СНИЛС
     */
    private void snilsClear() {
        $(By.xpath(snilsXpath)).clear();
        log.info("'СНИЛС' удалено");
    }

    /**
     * Очищение поля Серия и номер пасспорта
     */
    private void passSeriesClear() {
        $(By.xpath(passSeriesNumXpath)).clear();
        log.info("'Серия и номер паспорта' удалено");
    }

    /**
     * Очищение поля Кем и когда выдан
     */
    private void passGetClear() {
        $(By.xpath(passGetNumXpath)).clear();
        log.info("'Серия и номер паспорта' удалено");
    }


    /**
     * Выствление чек-бокса в юр профиле
     */
    public void setCheckBoxEntityAgree() {
        String agreeCheckBoxsXpath = "//input[@type=\"checkbox\"]";
        $$(By.xpath(agreeCheckBoxsXpath)).get(1).click();
        log.info("Чек бокс 'Я принимаю Соглашение о конфидициальности...' выбран");
    }

    /**
     * Выствление чек-бокса в физ профиле
     */
    public void setCheckBoxIndividualAgree() {
        String agreeCheckBoxsXpath = "//input[@type=\"checkbox\"]";
        $$(By.xpath(agreeCheckBoxsXpath)).get(3).click();
        log.info("Чек бокс 'Я принимаю Соглашение о конфидициальности...' выбран");
    }

    /**
     * Нажать сохранить в профиле юр лица
     */
    public void saveProfileEntityButtonClick() {

        String saveButtonEntityXpath = "//form[@name=\"OOO\"]//button[text()=\"Сохранить\"]";
        $(By.xpath(saveButtonEntityXpath)).click();
        log.info("Кнопка 'сохранить' нажата");
    }

    /**
     * Нажать сохранить в профиле физ лица
     */
    public void saveProfileIndividualButtonClick() {

        String saveButtonIndividualXpath = "//form[@name=\"FL\"]//button[text()=\"Сохранить\"]";
        $(By.xpath(saveButtonIndividualXpath)).click();
        log.info("Кнопка 'сохранить' нажата");
    }

    /**
     * Проверка всх обязательных полей формы юр лица
     */
    public void allFieldsRedAssertEntity() {
        try {
            $(By.xpath(errorCompanyFieldMessageXpath)).shouldBe(visible);
            $(By.xpath(errorBioFieldXpath)).shouldBe(visible);
            $(By.xpath(errorDocumentFieldXpath)).shouldBe(visible);
            $(By.xpath(errorTaxNumberFieldXpath)).shouldBe(visible);
            $(By.xpath(emptyCorrXpath)).shouldBe(visible);
            $(By.xpath(errorOgrnFieldXpath)).shouldBe(visible);
            $(By.xpath(errorOkpoFieldXpath)).shouldBe(visible);
            $(By.xpath(errorLawAddressFieldXpath)).shouldBe(visible);
            $(By.xpath(errorAccountFieldXpath)).shouldBe(visible);
            $(By.xpath(errorBicXpath)).shouldBe(visible);
            log.info("Все обязательные поля горят красным с соответствующими надписями");
        } catch (UIAssertionError error) {
            screenShot("Нет сообщения об ошибке ввода");
            log.error("После нажатия кнопки сохранить сообщение об ошибке в одном из полей не высветилось");
            isPassedFlag = false;
            error.printStackTrace();
        }
    }

    /**
     * Проверка всх обязательных полей формы физ лица
     */
    public void allFieldsRedAssertIndividual() {
        try {
            $(By.xpath(errorBioFieldXpath)).shouldBe(visible);
            $(By.xpath(errorTaxNumberIndividualXpath)).shouldBe(visible);
            $(By.xpath(errorSnilsXpath)).shouldBe(visible);
            $(By.xpath(errorPassSeriesXpath)).shouldBe(visible);
            $(By.xpath(errorPassGetXpath)).shouldBe(visible);
            log.info("Все обязательные поля горят красным с соответствующими надписями");
        } catch (UIAssertionError error) {
            screenShot("Нет сообщения об ошибке ввода");
            log.error("После нажатия кнопки сохранить сообщение об ошибке в одном из полей не высветилось");
            isPassedFlag = false;
            error.printStackTrace();
        }
    }


    /**
     * Перейти во все тесты через меню в левом блоке
     */
    public void allTestsMenu() {
        leftMenu.goAllTestsItem();
        log.info("Кнопка 'Все тесты' в кабинете пользователя удачно нажата");
    }

    /**
     * Перейти в черновики через меню в левом блоке
     */
    public void draftsMenu() {
        leftMenu.goDraftsItem();
        log.info("Кнопка 'Черновики' в кабинете пользователя удачно нажата");
    }

    /**
     * Списки для проверки копирования
     */
    private ArrayList<String> testInfoList;
    private ArrayList<String> audienceInfoList;
    private ArrayList<String> tasksInfoList;
    private ArrayList<String> sliderInfoList;
    private ArrayList<String> checkboxInfoList;
    private String testNum;
    private boolean isUxCrowdAudience;

    /**
     * добавить инфомацию о тесте
     *
     * @param xpath
     * @param list  список для добавления
     */
    public void addTestInfo(String xpath, ArrayList<String> list) {
        $(By.xpath(xpath)).waitUntil(visible, 10000);
        String testInfoString = $(By.xpath(xpath)).text().trim();
        list.add(testInfoString);
        log.info("Информация о тесте успешно добавлена: '{}'", testInfoString);
    }

    /**
     * Получение информации о респондентах
     *
     * @param infoType тип информации
     */
    private void getSliderInfo(String infoType) {

        String audienceFieldXpath = "//div[@class=\"header_right_group_body fourth-title open\"]";
        String blockXpath = "//div[text()=\"%s\"]//following-sibling::div";

        String[] infoSplit;
        infoSplit = $(By.xpath(audienceFieldXpath + String.format(blockXpath, infoType))).text().replace(" ", "").trim().split("-");
        sliderInfoList.add(infoSplit[0]);
        log.info("Добавлен минимальный {} '{}'", infoType, infoSplit[0]);
        if (!infoSplit[0].equalsIgnoreCase("любой")) {
            sliderInfoList.add(infoSplit[1].replace("+", ""));
            log.info("Добавлен максимальный {} '{}'", infoType, infoSplit[1]);
        }
    }

    /**
     * замена текста на атррибуты
     */
    private void getCheckBoxAttr() {
        for (int i = 0; i < checkboxInfoList.size(); i++) {
            if (checkboxInfoList.get(i).contains("Неоконченное высшее")) {
                checkboxInfoList.add(i, "HIGH_NOT_FULL");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Высшее")) {
                checkboxInfoList.add(i, "HIGH");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Среднее профессиональное")) {
                checkboxInfoList.add(i, "MIDDLE_SPEC");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Полное общее (среднее)")) {
                checkboxInfoList.add(i, "MIDDLE_FULL");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Студенты")) {
                checkboxInfoList.add(i, "STUDENT");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Основное общее")) {
                checkboxInfoList.add(i, "COMMON");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Безработные")) {
                checkboxInfoList.add(i, "UNEMPLOYED");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Фрилансеры")) {
                checkboxInfoList.add(i, "FREELANCER");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Руководители отделов")) {
                checkboxInfoList.add(i, "CHIEF_DEPT");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Домохозяйки")) {
                checkboxInfoList.add(i, "HOUSEWIFE");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Рабочие любой квалификации")) {
                checkboxInfoList.add(i, "WORKER");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Специалисты с высшим образованием без подчиненных")) {
                checkboxInfoList.add(i, "SPECIALIST");
                checkboxInfoList.remove(i + 1);

            } else if (checkboxInfoList.get(i).contains("Любой")) {
                checkboxInfoList.add(i, "ANY");
                checkboxInfoList.remove(i + 1);
            }
        }
    }

    /**
     * Получение информации об аудитории UXCrowd
     */
    private void getAudienceInfo() {
        String audienceInfoXpath = "//div[@ng-click=\"openGroup($event)\"][@ng-if=\"rightOrder.testerType=='OUR'\"]";
        String audienceName = $(By.xpath(audienceInfoXpath)).text();
        audienceName = audienceName.trim().substring(0, audienceName.indexOf("."));
        audienceInfoList.add(audienceName);
        $(By.xpath(audienceInfoXpath)).click();
        log.info("Тест с аудиторией UxCrowd откыта информация об аудитории");

        //xpath поля с информацией об аудитории
        String audienceFieldXpath = "//div[@class=\"header_right_group_body fourth-title open\"]";
        $(By.xpath(audienceFieldXpath)).shouldBe(visible);

        //Сохранение данных о возрасте
        getSliderInfo("Возраст");

        //Сохранение данных о зарплате
        getSliderInfo("Доход");

        //Сохранение данных о семейном положении
        String audience = $(By.xpath("//div[text()=\"Состав\"]/ancestor::div[@class=\"target-audience-item-wrapper\"]/span/span")).attr("ng-if");
        String value;
        if (!$$(By.xpath("//span[text()=\"Любой\"]")).get(0).isDisplayed()) {
            for (int i = 0; i < 3; i++) {
                value = audience.substring(audience.indexOf("'") + 1,
                        audience.indexOf("'", audience.indexOf("'") + 1));
                checkboxInfoList.add(value);
                log.info("Добавлен социальный статус '{}'", value);
                audience = audience.substring(audience.indexOf(value) + value.length() + 1);
            }
        }

        //Сохранение данных об образовании респондентов
        String educationBlockXpath = "//div[text()='%s']/ancestor::div[@class=\"target-audience-item-wrapper\"]";
        String nameOfBlock = "Образование";
        for (int i = 0; i < 2; i++) {
            if (!$$(By.xpath("//span[text()=\"Любой\"]")).get(0).isDisplayed()) {
                nameOfBlock = "Социальный статус";
                continue;
            }

            String str = $(By.xpath(String.format(educationBlockXpath, nameOfBlock))).text();
            str = str.substring(str.indexOf("\n"));
            str = str.substring(1);
            String[] result = str.split("(?=\\p{Lu})");
            for (String trimming : result) {
                checkboxInfoList.add(trimming.trim());
                log.info("Добавлены параметры пользователей - '{}'", trimming.trim());
            }
            nameOfBlock = "Социальный статус";
        }

        //Замена текста на аттрибуты чекбоксов
        getCheckBoxAttr();

    }

    private void getTaskInfo() {
        //Сохранение данных о вопросах
        String tasksBlock = "//div[@class=\"block_all_question --step4\"]";
        String str = $(By.xpath(tasksBlock)).text();
        String[] result = str.split("(?=\\p{Lu})"); //Разделение строки по заглавным буквам
        List<String> tempList = new ArrayList<>();
        for (String temStr : result) {
            String trimedStr = temStr.trim();
            if (trimedStr.contains("\n")) {
                trimedStr = trimedStr.substring(0, trimedStr.indexOf("\n") + 1);
            }
            tempList.add(trimedStr);

        }

        //Добавляем только вопросы, без вариантов ответа
        for (int i = 0; i < tempList.size() - 1; i++) {
            if (tempList.get(i).contains("\n") && !tempList.get(i + 1).contains("Выбор одного ответа")) {
                String question = tempList.get(i + 1).replace("\n", "");
                tasksInfoList.add(question);
                log.info("Добавлен текст задания '{}'", question);
            }
        }
    }

    /**
     * Открытие бокового меню с информацией о тесте
     *
     * @param testName    имя теста
     * @param isCompleted завершенный ли тест?
     */
    private void openTestInfo(String testName, boolean isCompleted) {
        String testNumXpath = "//div[contains(text(), '" + testName + "')]//preceding-sibling::div/span";
        if (isCompleted) {
            testNumXpath = "//div[@class=\"block_list done-tests\"]" + testNumXpath;
        }

        SelenideElement testNumElement = $$(By.xpath(testNumXpath)).get(0);
        testNum = testNumElement.text();
        log.info("Номер теста копируемого '{}' ", testNumElement.text().trim());
        testNumElement.click();
        log.info("Клик по номеру выполнен. Открылось боковое меню с информацией о тесте");
    }

    /**
     * Собрать тестовые данные из теста
     *
     * @param testName    название теста для будущего копирования
     * @param isCompleted заверешнный ли тест?
     */
    public void getTestInfo(String testName, boolean isCompleted) {

        testInfoList = new ArrayList<>();
        audienceInfoList = new ArrayList<>();
        tasksInfoList = new ArrayList<>();
        sliderInfoList = new ArrayList<>();
        checkboxInfoList = new ArrayList<>();


        //Открытие информации о тесте
        openTestInfo(testName, isCompleted);

        //Сохранение адреса тестируемого сайта

        addTestInfo("//a[@class=\"link fifth-title ng-binding\"][@ng-href]", testInfoList);

        //Сохранение описания для респондентов
        addTestInfo("//span[text()='Описание для респондентов:']//ancestor::section//following-sibling::div", testInfoList);

        //Сохранение названия аудитории
        try {
            isUxCrowdAudience = true;
            //информация об аудитории UXCrowd
            getAudienceInfo();
        } catch (ElementNotFound ex) {
            String ownRespLinkXpath = "//span[text()=\"Ссылка на выполнение теста:\"]";
            $(By.xpath(ownRespLinkXpath)).shouldBe(visible);
            log.info("Тест с собственной аудиторией");
            isUxCrowdAudience = false;
        }

        //Сохранение количества респондентов
        addTestInfo("//span[text()='Количество респондентов: ']//following-sibling::span", audienceInfoList);

        //Сохранение данных о вопросах
        getTaskInfo();

        //Закрыть боковое меню с информацией о тесте
        $(By.xpath("//span[@ng-click=\"closeRightMenu();\"]")).click();

        //Дебажная информация о содержании списков
        log.info("Список с информацией о тесте testInfoList: '{}'", testInfoList.toString());
        log.info("Список с информацией о тесте audienceInfoList: '{}'", audienceInfoList.toString());
        log.info("Список с информацией о тесте sliderInfoList: '{}'", sliderInfoList.toString());
        log.info("Список с информацией о тесте checkboxInfoList: '{}'", checkboxInfoList.toString());
        log.info("Список с информацией о тесте tasksInfoList: '{}'", tasksInfoList.toString());
    }

    /**
     * Щелкнуть по кнопке "Скопировать"
     */
    public void copyTestClick() {
        //Xpath
        String copyTestButtonXpath = "//span[text()='" + testNum + "']//ancestor::div[@class=\"all_info orders-list\"]//span[@title=\"Копировать тест\"]";
        String modalTextXpath = "//div[contains(text(), '" + testNum + "')]";
        String copyConfirm = "//span[text()=\"Да\"]";

        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        $(By.xpath(copyTestButtonXpath)).click();
        log.info("Номер теста для копирования '{}'", testNum);
        log.info("Успешный клик по элементу {}", copyTestButtonXpath);
        $(By.xpath(modalTextXpath)).is(visible);
        log.info("Текст с предупреждением успешно отображен {}", modalTextXpath);
        $(By.xpath(copyConfirm)).click();
        log.info("Успешный клик по элементу {}", copyConfirm);
    }


    /**
     * проверка полей описания теста
     */
    public void fieldsOfDescriptionCheck() {
        String testUrlXpath = "//input[@value=\"%s\"]";
        String descriptionXpath = "//textarea[contains(text(), \"%s\")]";
        $(By.xpath("//div[text()=\"Название теста\"]")).waitUntil(visible, 20000);
        String testUrl = testInfoList.get(0);
        $(By.xpath(String.format(testUrlXpath, testUrl))).shouldBe(visible);
        log.info("Тестовый сайт {} отображен верно", testUrl);
        String description = testInfoList.get(1);
        $(By.xpath(String.format(descriptionXpath, description))).shouldBe(visible);
        log.info("Введение для пользователя - {} отображено верно", description);
        nextButton();
    }

    /**
     * проверить поля аудитории
     */
    public void fieldOfAudienceCheck() {
        String slider = "//p[text()='%s']";
        String checkbox = "//input[@value=\"%s\"]";

        String audienceName = audienceInfoList.get(0);
        $(By.xpath(String.format(checkbox, audienceName))).shouldBe(visible);
        log.info("Название сегмента аудитории - '{}' проверено", audienceName);
        if (isUxCrowdAudience) {
            String numOfResps = audienceInfoList.get(1);
            $(By.xpath(String.format(checkbox, numOfResps))).shouldBe(visible);
            log.info("Количество тестировщиков - '{}' проверено", numOfResps);
        }

        for (String value : sliderInfoList) {
            if (!value.equalsIgnoreCase("Любой")) {
                $(By.xpath(String.format(slider, value))).shouldBe(visible);
                log.info("Значение '{}' проверено", value);
            }
        }


        for (String value : checkboxInfoList) {
            $(By.xpath(String.format(checkbox, value))).shouldBe(Condition.checked);
            log.info("Значение '{}' проверено", value);
        }

        saveAndNextButtonClick();
    }

    /**
     * Проверка выведения ошибки промокода
     */
    public void checkWrongPromoCodeErrorMessage() {
        String promoCodeErrorXpath = "//div[text()='%s']";
        SelenideElement errorElement;
        try {
            errorElement = $(By.xpath(String.format(promoCodeErrorXpath, "Неверный Промо-код")));
            errorElement.shouldBe(visible);
            log.info("Ошибка о неправильном промокоде '{}' выведена", errorElement.text());
        } catch (ElementNotFound ex) {
            errorElement = $(By.xpath(String.format(promoCodeErrorXpath, "Промо-код уже был использован")));
            errorElement.shouldBe(visible);
            log.info("Ошибка об использованном промокоде '{}' выведена", errorElement.text());
        }

    }

    /**
     * Изменить имя теста после его создания
     *
     * @param newName
     */
    public void changeNameOfTest(String newName) {
        String testNameFieldXpath = "//input[@data-autotest-id=\"text_input_testNameInput\"]";
        $(By.xpath("//div[text()=\"Название теста\"]")).waitUntil(visible, 20000);
        SelenideElement testNameElement = $(By.xpath(testNameFieldXpath));
        testNameElement.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        testNameElement.sendKeys(newName);
        log.info("Новое имя '{}' введено в поле 'Название теста'", newName);
        nextButton();
        saveAndNextButtonClick();
        addAndStartButtonClick();
    }

    /**
     * Проверка заданий
     */
    public void taskCheck() {
        String xpath = "//p[contains(text(), \"%s\")]";
        for (String value : tasksInfoList) {
            if (!value.contains("Выбор нескольких ответов")) {
                if ($$(By.xpath(String.format(xpath, value))).isEmpty()) {
                    log.error("Значение '{}' не найдено", value);
                    throw new AssertionError();
                }
            }
            log.info("Значение '{}' проверено", value);
        }
        addAndStartButtonClick();
    }

    /**
     * Щелкнуть по кнопке "Редактировать и запустить"
     *
     * @param testNumber номер теста для запуска
     */
    public void editAndRunClick(String testNumber) {
        //Xpath
        String editAndRunXpath = "//div[contains(text(), '" + testNumber + "')]//ancestor::div[@class=\"all_info orders-list\"]//span[text()=\"Редактировать и запустить\"]";

        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        $(By.xpath(editAndRunXpath)).click();
        log.info("Номер теста для копирования '{}'", testNumber);
        log.info("Успешный клик по элементу {}", editAndRunXpath);

    }


    /**
     * Заполнение полей оплаты картой
     *
     * @param cardNumber номер карты
     * @param cardDate   дата истечения срока действия карты
     * @param cardCVC    CVC код
     */
    public void insertCardRequisites(String cardNumber, String cardDate, String cardCVC) {
        String cardRequisiteXpath = "//div/input[@autocomplete=\"%s\"]";
        sleep(10000); //TODO sleep
        SelenideElement tinkoffIframe = $(By.xpath("//iframe[contains(@src, \"https://securepay.tinkoff.ru/new/\")]"));
        switchTo().frame(tinkoffIframe);
        $(By.xpath(String.format(cardRequisiteXpath, "cc-number"))).sendKeys(cardNumber);
        log.info("Введен номер карты '{}'", cardNumber);
        $(By.xpath(String.format(cardRequisiteXpath, "cc-exp"))).sendKeys(cardDate);
        log.info("Введена дата истечения карты '{}'", cardDate);
        $(By.xpath(String.format(cardRequisiteXpath, "cc-csc"))).sendKeys(cardCVC);
        log.info("Введен CVC код '{}'", cardCVC);
        String subminButtonXpath = "//button[@type=\"submit\"]";
        $(By.xpath(subminButtonXpath)).click();
        log.info("Кнопка 'Оплатить' нажата");
        String succesTextXpath = "//p[text()=\"Платеж выполнен успешно\"]";
        $(By.xpath(succesTextXpath)).shouldBe(visible);
        log.info("Оплата успешно совершена");
        String goToStartXpath = "//button[@class=\"green_button btn btn-primary center-block\"]";
        $(By.xpath(goToStartXpath)).click();

    }

    /**
     * Изменение тарифа
     */
    public void tariffChanging() {
        String tariffXpath = "//div[text()=\"Оптимальный\"]";
        $(By.xpath(tariffXpath)).click();
        log.info("Осуществлён клик по тарфному плану 'Оптимальный'");
        String confirmButtonXpath = "//button[@class=\"new-green-btn\"]";
        $(By.xpath(confirmButtonXpath)).click();
        log.info("Осуществлён клик по кнопке 'Изменить подписку'");
        String goToPaymentXpath = "//button[@ng-click=\"sendRegistration();\"]";
        $(By.xpath(goToPaymentXpath)).click();
        log.info("Осуществлён клик по кнопке 'Перейти к оплате'");
    }

    /**
     * Скачивание оферты
     */
    public void offerDownload(String tariff) {
        String tariffXpath = "//div[text()=\"" + tariff + "\"]";
        $(By.xpath(tariffXpath)).click();
        log.info("Осуществлён клик по тарфному плану 'Оптимальный'");


        $(By.xpath("//button[@ng-click=\"changePlan()\"]")).click();
        log.info("Осуществлён клик по кнопке 'Изменить тариф'");
        String goToPaymentXpath = "//button[@ng-click='initTariffOferta()'][@class='inline-btn']";
        $(By.xpath(goToPaymentXpath)).click();
        log.info("Осуществлён клик по кнопке 'Отправить запрос'");
        String confirmButtonXpath = "//button[@ng-click='close()'][@class='inline-btn']";
        $(By.xpath("//h4[text()='Запрос на смену тарифа отправлен']")).shouldBe(visible);
        log.info("Модальное окно с удачной отправкой отобразилось, значит API успешно отработало");
        $(By.xpath(confirmButtonXpath)).click();
        log.info("Осуществлён клик по кнопке 'ОК' для закрытия модального окна");
    }


    /**
     * Заказать исследование
     *
     * @param phoneNumber номер телефона
     */
    public void researchChanging(String phoneNumber) {
        String researchXpath = "//div[text()=\"Заказать исследование\"]";
        $(By.xpath(researchXpath)).click();
        log.info("Осуществлён клик 'Заказать исследование'");
        String confirmButtonXpath = "//button[@class=\"new-green-btn new-green-btn-business\"]";
        $(By.xpath(confirmButtonXpath)).click();
        log.info("Осуществлён клик по кнопке 'Изменить подписку'");
        String phoneNumberFeildXpath = "//input[@data-type-title=\"phone\"]";
        $(By.xpath(phoneNumberFeildXpath)).sendKeys(phoneNumber);
        log.info("В поле 'Телефонный номер' введен номер '{}'", phoneNumber);
        confirmButtonXpath = "//span[text()=\"Отправить заявку\"]";
        $(By.xpath(confirmButtonXpath)).click();
        log.info("Нажата кнопка 'Отправить заявку'");

    }

    /**
     * метод для мучительно долгого удаления черновиков из личного кабинета.
     * Использовать пока нет апи на удаление, если необходимо немного почистить базу.
     */
    public void deleteAllDrafts() {
        leftMenu.goDraftsItem();
        sleep(50000);
        ElementsCollection collection = $$(By.xpath("//span[@ng-click=\"delDraft(order.id)\"]"));
        for (SelenideElement deleteElement : collection) {
            deleteElement.click();

            $(By.xpath("//button[@class=\"btn btn-success\"]")).click();

            sleep(50000);


        }
    }

    /**
     * Получить все элементы для скачивания файлов
     *
     * @return коллекция элементов
     */
    private ElementsCollection getDownloadButtons() {
        String downloadButtonsXpath = "//a[text()=\"Скачать результаты\"]";
        return $$(By.xpath(downloadButtonsXpath));
    }

    /**
     * Получение токена и ID
     *
     * @return строка с токеном и ID
     */
    private String getTokenAndId() {
        String fullCookies = WebDriverRunner.getWebDriver().manage().getCookies().toString();
        String JSESSIONID = "";
        String XSRFTOKEN = "";
        String[] cookies = fullCookies.split(";");
        for (String cookie : cookies) {
            if (cookie.contains("JSESSIONID"))
                JSESSIONID = cookie.substring(cookie.indexOf("JSESSIONID"));
            if (cookie.contains("XSRF-TOKEN"))
                XSRFTOKEN = cookie.substring(cookie.indexOf("XSRF-TOKEN"));
        }
        return XSRFTOKEN + ";" + JSESSIONID;
    }

    /**
     * Проверка валидности скачивания
     */
    public void checkAllDownloads() {
        ElementsCollection allButtons = getDownloadButtons();
        ArrayList<String> fuckedTestNums = new ArrayList<>();

        for (SelenideElement button : allButtons) {
            String testText = button.parent().parent().text();
            String testNum = testText.substring(0, testText.indexOf("\n"));

            try {
                downloadFile(testNum, getTokenAndId());
                log.info("Скачивание файла выгрузки с теста № '{}' успешно завершено", testNum);
            } catch (IOException e) {
                e.printStackTrace();
                fuckedTestNums.add(testNum);
                log.error("Скачивание файла выгрузки с теста с № '{}' не выполнено", testNum);
            }

        }
        log.info("Всего неудачных скачиваний '{}'. Номера неудавшихся скачиваний '{}'", fuckedTestNums.size(), fuckedTestNums.toString());
        Assert.assertTrue(fuckedTestNums.isEmpty(), "В кейсе найдены неудавшиеся попытки скачивания выгрузки");

    }


    /**
     * Скачивание файлов
     *
     * @param testNumber номер теста
     * @param tokenAndId токен и ID для верификации загрузки
     * @throws IOException ошибка скачивания
     */
    private void downloadFile(String testNumber, String tokenAndId) throws IOException {


        String apiName = uxCrowdConfig.testUrl() + "/api/v2/customer/orderReport?id=";
        URL url = new URL(apiName + testNumber);
        HttpURLConnection connection = getHttpURLConnection(url, tokenAndId, "GET");

        Reader inputStreamReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

        StringBuilder response = new StringBuilder();
        for (int c; (c = inputStreamReader.read()) >= 0; ) {
            response.append((char) c);
        }

        byte[] result = org.apache.commons.codec.binary.Base64.decodeBase64(response.toString());

        File file = new File("build/reports/tests/A2/" + testNumber + ".xlsx");
        try {
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Создание HTTP запроса к сервису
     *
     * @param url     ссылка на скачивание
     * @param cookies куки для верификации
     * @return соединение
     */
    private HttpURLConnection getHttpURLConnection(URL url, String cookies, String requestType)
            throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(requestType);
        connection.setRequestProperty("Cookie", cookies);
        connection.setUseCaches(false);
        return connection;
    }

    /**
     * Получить реквизиты из HTML страницы
     *
     * @param testNum номер теста
     * @return Список ревизитов теста, которые возможно стащить из DOM дерева
     */
    public List<String> getTestRequisites(String testNum) {
        String testXpath = "//span[text()=\"" + testNum + "\"]";
        String xpathAddon = "/ancestor::div[@class=\"order-num-table\"]//following-sibling::div";
        ElementsCollection requisites = $$(By.xpath(testXpath + xpathAddon));
        String url = requisites.get(1).text().trim();
        log.info("Для проверки будет добавлен'{}'", url);
        String respondentTempSource = requisites.get(3).text().trim();
        String respondentSource = "";
        if (respondentTempSource.equals("Свои")) {
            respondentSource = "Тестирование по ссылке";
        } else if (respondentTempSource.equals("UXCrowd")) {
            respondentSource = "База UXCrowd";
        }
        log.info("Для проверки будет добавлен'{}'", respondentSource);
        return List.of(testNum, url, respondentSource);
    }

    /**
     * Проверить отправку оферты на почту клиента
     */
    public void checkSendingOffer() throws IOException {
        String apiName = uxCrowdConfig.testUrl() + "/api/v2/customer/tariffOfertaInit";
        URL url = new URL(apiName);
        String tokenAndId = getTokenAndId();
        HttpURLConnection connection = getHttpURLConnection(url, tokenAndId, "POST");
        int responseCode = connection.getResponseCode();

        log.info("Ответ сервера '{}'", responseCode);
        log.info(connection.getContent());
        if (responseCode != 200) {
            log.error("Ошибка соединения. Отправка офера не осуществлена. Код ошибки '{}'", responseCode);
            throw new ConnectException();
        }
    }

}