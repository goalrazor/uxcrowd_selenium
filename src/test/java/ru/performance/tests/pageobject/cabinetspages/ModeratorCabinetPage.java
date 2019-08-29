package ru.performance.tests.pageobject.cabinetspages;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import ru.performance.tests.pageobject.pagefragment.NavCabinetMenuPageFragment;

import java.time.LocalDate;
import java.util.Map;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static ru.performance.tests.BaseTest.screenShot;


public class ModeratorCabinetPage extends CabinetPage {

    private boolean expectedValueExistFlag;
    private String forwardButtonExist;
    private String forwardButtonXpath = "//button[@title='Page forward']";
    private NavCabinetMenuPageFragment navMenu = new NavCabinetMenuPageFragment();


    /**
     * Проверить отображение страницы кабинета модератора
     */
    @Override
    public void checkPageShow() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        try {
            SelenideElement checkElement = $(By.xpath("//a[text()='Жалобы']"));
            checkElement.shouldBe(visible);
            log.debug("===> Страница кабинета модератора удачно открылась");
        } catch (NoSuchElementException ex) {
            log.error(String.format("Страница кабинета модератора не открылась: '%s'", ex.getMessage()));
            assert false;
        }
    }


    /**
     * Выйти из кабинета модератора
     */
    @Override
    public void cabinetLogout() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        // Нажать кнопку "Выйти"
        String goOutButtonXpath = "//a[@id='logout']";
        $(By.xpath(goOutButtonXpath)).click();
        log.info("Кнопка 'Выйти' в кабинете модератора удачно нажата");
    }


    /**
     * Перейти в пункт меню "Промо-коды"
     */
    public void goPromoCodeMenuItem() {
        navMenu.goPromoCodeMenuItem();
        $(By.xpath("//h2[text()='Список всех промо-кодов']")).shouldBe(visible);
        log.info("Таблица промо-кодов успешно открыта");
    }


    /**
     * Кликнуть кнопку "Сгенерировать промо-код для клиента"
     */
    public void generatePromoCode() {
        $(By.xpath("//span[@class='green_cycle']")).click();
        log.info("Нажата кнопка 'Сгенерировать промо-код для клиента'");

        // Проверить отображение полей ввода и кнопок
        $(By.xpath("//label[text()='Код']")).shouldBe(visible);
        $(By.xpath("//button[text()='Сохранить']")).shouldBe(visible);
        $(By.xpath("//button[text()='Отмена']")).shouldBe(visible);
        log.info("Поля ввода параметров промо-кода, кнопки 'Сохранить' и 'Отмена' удачно отобразились");
    }

    /**
     * изменить тип промокода на процентный
     *
     * @param isTester промокод на тестировщиков?
     */
    public void changePromoCodeType(boolean isTester) {
        String typeFieldXpath = "//select[@name='type']";
        String typeOptionXpath = "//option[@value='%s']";
        $(By.xpath(typeFieldXpath)).click();
        log.info("Клик по полю тип, для выбора опций совершен");
        SelenideElement typeOption;
        if (isTester) {
            typeOption = $(By.xpath(String.format(typeOptionXpath, "TEST")));
            typeOption.click();
            log.info("Выбран тип 'Тестировщики'");
        } else {
            typeOption = $(By.xpath(String.format(typeOptionXpath, "PERCENT")));
            typeOption.click();
            log.info("Выбран тип 'Проценты'");
        }
    }

    /**
     * Ввести данные в поле параметров промо-кода
     *
     * @param inputId    ID поля ввода
     * @param inputValue Значение, вводимое в поле
     */
    public void inputPromoCodeParameter(String inputId, String inputValue) {
        $(String.format("#%s", inputId)).setValue(inputValue);
        log.info(String.format("В поле параметра c ID '%s' введено значение '%s'", inputId, inputValue));
    }


    /**
     * Ввести дату в поле для даты промо-кода
     *
     * @param inputId ID поля ввода
     * @param date    Дата
     */
    public void setDate(String inputId, LocalDate date) {
        $(String.format("#%s", inputId))
                .setValue(Integer.toString(date.getMonthValue()))
                .setValue(Integer.toString(date.getDayOfMonth()))
                .setValue((Integer.toString(date.getYear())));
        log.info(String.format("В поле ввода даты с ID '%s' введена дата '%s'", inputId, date.toString()));
    }


    /**
     * Нажать кнопку "Сохранить"
     */
    public void saveButton() {
        $(By.xpath("//button[text()='Сохранить']")).click();
        log.info("Нажата кнопка 'Сохранить'");
    }


    /**
     * Проверить внесёные изменения в промокод
     *
     * @param newPromoCodeParameters     Новые параметры промо-кода для обычных полей
     * @param newPromoCodeDateParameters Новые параметры полей промо-кода для полей с датами
     */
    public void checkPromoCodeIntroduceChanges(
            Map<String, String> newPromoCodeParameters,
            Map<String, LocalDate> newPromoCodeDateParameters) {

        parametersCompare(
                "Значение",
                "preceding-sibling::div[contains(@id, 'uiGrid-0007-cell')]",
                newPromoCodeParameters.get("value"),
                newPromoCodeParameters.get("code")
        );

        parametersCompare(
                "Количество тестировщиков",
                "following-sibling::div[contains(@id, 'uiGrid-000A-cell')]",
                newPromoCodeParameters.get("testersCountMin"),
                newPromoCodeParameters.get("code")
        );

        parametersCompare(
                "Начальная дата",
                "following-sibling::div[contains(@id, 'uiGrid-000B-cell')]",
                String.format("%02d", newPromoCodeDateParameters.get("startDate").getDayOfMonth()) + "." +
                        String.format("%02d", newPromoCodeDateParameters.get("startDate").getMonthValue()) + "." +
                        newPromoCodeDateParameters.get("startDate").getYear(),
                newPromoCodeParameters.get("code")
        );

        parametersCompare(
                "Конечная дата",
                "following-sibling::div[contains(@id, 'uiGrid-000C-cell')]",
                String.format("%02d", newPromoCodeDateParameters.get("endDate").getDayOfMonth()) + "." +
                        String.format("%02d", newPromoCodeDateParameters.get("endDate").getMonthValue()) + "." +
                        newPromoCodeDateParameters.get("endDate").getYear(),
                newPromoCodeParameters.get("code")
        );

    }


    /**
     * Сравнить ожидаемое параметров с актуальными в таблице промо-кодов
     *
     * @param parameterName Название параметра промо-кода
     * @param additionXpath Дополнительный XPath параметра промо-кода
     * @param expectedValue Ожидаемое значение параметра промо кода
     * @param codeValue     Значение параметра "Код" промо-кода
     */
    private void parametersCompare(String parameterName, String additionXpath, String expectedValue, String codeValue) {

        // Собрать актуальные данные из таблицы промо-кодов
        String actualParamsXpath = String.format("//div[contains(text(),'%s')]/../" + additionXpath, codeValue);
        String actualValue = $(By.xpath(actualParamsXpath)).text();

        // Сравнить актуальные данные с ожидаемыми
        if (actualValue.equals(expectedValue)) {
            log.info(String.format(
                    "Параметр '%s': актуальное значение '%s' совпадает с ожидаемым '%s'",
                    parameterName, actualValue, expectedValue));
        } else {
            log.error(String.format(
                    "Параметр '%s': актуальное значение '%s' НЕ совпадает с ожидаемым '%s'",
                    parameterName, actualValue, expectedValue));
            assert false;
        }

    }


    /**
     * Проверить наличие нового промо-кода в таблице по значению параметра "Код"
     *
     * @param expectedValue Значение параметра "Код" промо-кода
     */
    public void checkPromoCodeInTable(String expectedValue) {

        expectedValueExistFlag = false;
        ElementsCollection allActualValues = $$(By.xpath("//div[contains(@id,'uiGrid-0008-cell')]"));

        boolean isForwardButtonExist;

        do {
            isForwardButtonExist = $(By.xpath(forwardButtonXpath + "[@disabled='disabled']")).exists();
            if (isForwardButtonExist)
                break;
            allActualValues = $$(By.xpath("//div[contains(@id,'uiGrid-0008-cell')]"));

            for (SelenideElement element : allActualValues) {
                log.debug("'Код' в таблице: " + element.text());
                if (element.text().equals(expectedValue)) {
                    String msg = String.format("Ожидаемое значение '%s' в таблице промо-кодов найдено", expectedValue);
                    log.info(msg);
                    screenShot(msg);
                    break;
                }
            }

            // На следующую страницу таблицы, если не нашлось ничего
            $(By.xpath(forwardButtonXpath)).click();

        } while (!isForwardButtonExist);

    }


    /**
     * Перейти на следующую страницу таблицы промо-кодов
     *
     * @param expectedValueExistFlag Флаг наличия ожидаемого значения
     * @param forwardButtonXpath     XPath кнопки "впекрёд" таблицы
     */
    private void promoCodeTableNextPage(boolean expectedValueExistFlag, String forwardButtonXpath) {
        String lastPageForwardButtonXpath =
                forwardButtonXpath.replace("]", " and @disabled='disabled']");

        if (!expectedValueExistFlag) {
            if ($$(By.xpath(lastPageForwardButtonXpath)).size() == 0) {
                $(By.xpath(forwardButtonXpath)).click();
                forwardButtonExist = "false";       // Страница после пагинации другая, значение обнуляется!
            } else {
                log.info("Последняя страница таблицы промо-кодов - листать не получается");
            }
        }
    }


    /**
     * Удалить все промо-коды, сделанные автотестами, по значению параметра "Код"
     *
     * @param codeParameter Значение параметра "Код" промо-кода
     */
    public void deleteAllAutotestPromoCodes(String codeParameter) {

        expectedValueExistFlag = false;
        ElementsCollection allActualValues;

        // Выбрать "Промо-коды" в меню
        goPromoCodeMenuItem();

        boolean isForwardButtonExist;
        do {

            isForwardButtonExist = $(By.xpath(forwardButtonXpath + "[@disabled='disabled']")).exists();

            allActualValues = $$(By.xpath("//div[contains(@id,'uiGrid-0008-cell')]"));
            for (SelenideElement element : allActualValues) {
                if (element.text().contains(codeParameter)) {
                    log.info(String.format(
                            "Удаляемый промо-код, имеющий значение '%s' параметра 'Код', найден", codeParameter)
                    );

                    // Удалить промо-код
                    deleteAutotestPromoCode(codeParameter);

                    break;
                }
                log.info("Промо-код для удаления не найден");

            }

            // На следующую страницу таблицы, если не нашлось ничего
            if (!isForwardButtonExist)
                $(By.xpath(forwardButtonXpath)).click();

        } while (!isForwardButtonExist);

    }


    /**
     * Рекурсивно удаляет из таблицы промо-код, заданный параметром
     *
     * @param codeParameter Строка, входящая в параметр промо-кода
     */
    private void deleteAutotestPromoCode(String codeParameter) {

        String deleteButtonXpath =
                String.format("//div[contains(text(),'%s')]/../following-sibling::div/button[@title='Удалить']",
                        codeParameter);

        // Кликать на первой, может быть несколько промо-кодов, оставшихся от автотестов
        $$(By.xpath(deleteButtonXpath)).first().click();
        log.info(String.format("Удалён промо-код с '%s' в параметре", codeParameter));

        // Зарекурсить
        if ($$(By.xpath(deleteButtonXpath)).size() != 1) {
            deleteAutotestPromoCode(codeParameter);
        }
    }


    /**
     * Найти в таблице промо-код, созданный автотестом, и начать процедуру его изменения
     *
     * @param codeParameter Значение параметра "Код" промо-кода
     */
    public void beginEditAutotestPromoCode(String codeParameter) {

        String editButtonXpath = String.format(
                "//div[contains(text(),'%s')]/../following-sibling::div/button[@title='Изменить']",
                codeParameter);
        expectedValueExistFlag = false;

        do {
            forwardButtonExist = $(By.xpath(forwardButtonXpath)).attr("disabled");

            if ($$(By.xpath(editButtonXpath)).size() != 0) {
                $$(By.xpath(editButtonXpath)).first().click();
                log.info(String.format("Нажата кнопка 'Изменить' для промо-кода, содержащего '%s'", codeParameter));
                break;
            } else {
                // На следующую страницу таблицы, если не нашлось ничего
                promoCodeTableNextPage(expectedValueExistFlag, forwardButtonXpath);
            }

        } while ((!forwardButtonExist.equals("true")));
    }
}
