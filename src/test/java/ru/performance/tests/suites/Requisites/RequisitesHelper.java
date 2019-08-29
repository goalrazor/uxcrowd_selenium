package ru.performance.tests.suites.Requisites;

import com.codeborne.selenide.ex.UIAssertionError;
import org.openqa.selenium.By;
import org.testng.Assert;
import ru.performance.tests.BaseTest;
import ru.performance.tests.pageobject.cabinetspages.ClientCabinetPage;
import ru.performance.tests.pageobject.pagefragment.LeftMenuPageFragment;

import java.util.Random;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class RequisitesHelper extends BaseTest {

    private final LeftMenuPageFragment leftMenu = new LeftMenuPageFragment();
    private final ClientCabinetPage clientCabinetPage = new ClientCabinetPage();

    /**
     * Авторизаваться Клиентом
     */
    void clientLogin() {
        new ClientCabinetPage().cabinetLogin(getClientAccountPair());
    }

    /**
     * Перейти в профиль клиента через меню в левом блоке
     */
    void goProfileMenu() {
        leftMenu.goProfileItem();
        log.info("Кнопка 'Профиль' в кабинете пользователя удачно нажата");
    }

    /**
     * Очистить все поля профиля личного кабинета юридического лица
     */
    void clearProfileEntity() {
        clientCabinetPage.clearAllFieldsEntity();
    }

    /**
     * Очистить все поля профиля личного кабинета физического лица
     */
    void clearProfileIndividual() {
        clientCabinetPage.clearAllFieldsIndividual();
    }

    /**
     * Отметить чекбокс "согласен с соглашением" юр лицо
     */
    void setCheckBoxToAgreeEntity() {
        clientCabinetPage.setCheckBoxEntityAgree();
    }

    /**
     * Отметить чекбокс "согласен с соглашением" физ лицо
     */
    void setCheckBoxToAgreeIndividual() {
        clientCabinetPage.setCheckBoxIndividualAgree();
    }

    /**
     * Нажать кнопку "Сохранить" в профиле юр лица
     */
    void saveButtonEntityClick() {
        clientCabinetPage.saveProfileEntityButtonClick();
    }

    /**
     * Нажать кнопку "Сохранить" физ лицо
     */
    void saveButtonIndividualClick() {
        clientCabinetPage.saveProfileIndividualButtonClick();
    }

    /**
     * Проверка всх обязательных полей формы юрлицо
     */
    void negativeAssertAllFieldsEntity() {
        clientCabinetPage.allFieldsRedAssertEntity();
    }

    /**
     * Проверка всх обязательных полей формы физлицо
     */
    void negativeAssertAllFieldsIndividual() {
        clientCabinetPage.allFieldsRedAssertIndividual();
    }

    /**
     * Вводит неверные значения в поля профиля
     *
     * @param valueLength     длина вводимого значения
     * @param fieldXpath      Локатор поля
     * @param nameOfField     Имя поля для логирования
     * @param errorFieldXpath Локатор появления ошибки ввода значений
     * @param isNum           Флаг для смены числовых значений и буквенных
     * @param isWrong         флаг для верных и неверных значений
     */
    private void enterValuesToField(int valueLength, String fieldXpath, String nameOfField, String errorFieldXpath, boolean isNum, boolean isWrong) {
        StringBuilder randomString;
        if (isNum) {
            randomString = randomStringOfIntsGenerator(valueLength);
        } else {
            randomString = randomStringGenerator(valueLength).append("1");
        }
        $(By.xpath(fieldXpath)).sendKeys(randomString);
        sleep(1000);
        log.info("Поле '{}' заполнено строкой '{}' ", nameOfField, randomString);
        if (isWrong) {
            clientCabinetPage.errorFieldMessageVisibility(errorFieldXpath);
            String errorInnEntityText = "Введите ИНН юр.лица (10 цифр)";
            String errorInnIndividualText = "Введите ИНН (12 цифр)";
            String errorKppText = "Введите КПП (9 цифр)";
            String errorOgrnText = "Введите ОГРН (не более 13 цифр)";
            String errorOkpoText = "Введите ОКПО (не более 14 цифр)";
            String errorAccountText = "Введите Расчетный счет (20 цифр)";
            String errorCorrText = "Введите кор.счет (20 цифр)";
            String errorBicText = "Введите БИК (9 цифр)";
            String errorSnilsText = "Введите СНИЛС (11 чисел без пробелов и дефисов)";
            String errorPassSeriesText = "Введите серию и номер паспорта (10 цифр без пробелов и дефисов)";

            String viewingErrorText = $(By.xpath(errorFieldXpath)).text();
            log.info("Текст ошибки: '{}' ", viewingErrorText);
            switch (nameOfField) {
                case ("ИНН"):
                    try {
                        Assert.assertEquals($(By.xpath(errorFieldXpath)).text(), errorInnEntityText);
                        log.info("Для поля '{}' сообщение об ошибке верное", nameOfField);
                    } catch (AssertionError er) {
                        log.error("Для поля '{}' сообщение об ошибке: '{}' не соответствует эталону: '{}'", nameOfField, viewingErrorText, errorInnEntityText);
                        clientCabinetPage.isPassedFlag = false;
                    }
                    break;
                case ("КПП"):
                    try {
                        Assert.assertEquals($(By.xpath(errorFieldXpath)).text(), errorKppText);
                        log.info("Для поля '{}' сообщение об ошибке верное", nameOfField);
                    } catch (AssertionError er) {
                        log.error("Для поля '{}' сообщение об ошибке: '{}' не соответствует эталону: '{}'", nameOfField, viewingErrorText, errorKppText);
                        clientCabinetPage.isPassedFlag = false;
                    }
                    break;
                case ("ОГРН"):
                    try {
                        Assert.assertEquals($(By.xpath(errorFieldXpath)).text(), errorOgrnText);
                        log.info("Для поля '{}' сообщение об ошибке верное", nameOfField);
                    } catch (AssertionError er) {
                        log.error("Для поля '{}' сообщение об ошибке: '{}' не соответствует эталону: '{}'", nameOfField, viewingErrorText, errorOgrnText);
                        clientCabinetPage.isPassedFlag = false;
                    }
                    break;
                case ("ОКПО"):
                    try {
                        Assert.assertEquals($(By.xpath(errorFieldXpath)).text(), errorOkpoText);
                        log.info("Для поля '{}' сообщение об ошибке верное", nameOfField);
                    } catch (AssertionError er) {
                        log.error("Для поля '{}' сообщение об ошибке: '{}' не соответствует эталону: '{}'", nameOfField, viewingErrorText, errorOkpoText);
                        clientCabinetPage.isPassedFlag = false;
                    }
                    break;
                case ("Расчетный счет"):
                    try {
                        Assert.assertEquals($(By.xpath(errorFieldXpath)).text(), errorAccountText);
                        log.info("Для поля '{}' сообщение об ошибке верное", nameOfField);
                    } catch (AssertionError er) {
                        log.error("Для поля '{}' сообщение об ошибке: '{}' не соответствует эталону: '{}'", nameOfField, viewingErrorText, errorAccountText);
                        clientCabinetPage.isPassedFlag = false;
                    }
                    break;
                case ("Корреспондентский счет"):
                    try {
                        Assert.assertEquals($(By.xpath(errorFieldXpath)).text(), errorCorrText);
                        log.info("Для поля '{}' сообщение об ошибке верное", nameOfField);
                    } catch (AssertionError er) {
                        log.error("Для поля '{}' сообщение об ошибке: '{}' не соответствует эталону: '{}'", nameOfField, viewingErrorText, errorCorrText);
                        clientCabinetPage.isPassedFlag = false;
                    }
                    break;
                case ("БИК"):
                    try {
                        Assert.assertEquals($(By.xpath(errorFieldXpath)).text(), errorBicText);
                        log.info("Для поля '{}' сообщение об ошибке верное", nameOfField);
                    } catch (AssertionError er) {
                        log.error("Для поля '{}' сообщение об ошибке: '{}' не соответствует эталону: '{}'", nameOfField, viewingErrorText, errorBicText);
                        clientCabinetPage.isPassedFlag = false;
                    }
                    break;
                case ("ИНН физ. лица"):
                    try {
                        Assert.assertEquals($(By.xpath(errorFieldXpath)).text(), errorInnIndividualText);
                        log.info("Для поля '{}' сообщение об ошибке верное", nameOfField);
                    } catch (AssertionError er) {
                        log.error("Для поля '{}' сообщение об ошибке: '{}' не соответствует эталону: '{}'", nameOfField, viewingErrorText, errorInnIndividualText);
                        clientCabinetPage.isPassedFlag = false;
                    }
                    break;
                case ("СНИЛС"):
                    try {
                        Assert.assertEquals($(By.xpath(errorFieldXpath)).text(), errorSnilsText);
                        log.info("Для поля '{}' сообщение об ошибке верное", nameOfField);
                    } catch (AssertionError er) {
                        log.error("Для поля '{}' сообщение об ошибке: '{}' не соответствует эталону: '{}'", nameOfField, viewingErrorText, errorSnilsText);
                        clientCabinetPage.isPassedFlag = false;
                    }
                    break;
                case ("Серия и номер паспорта"):
                    try {
                        Assert.assertEquals($(By.xpath(errorFieldXpath)).text(), errorPassSeriesText);
                        log.info("Для поля '{}' сообщение об ошибке верное", nameOfField);
                    } catch (AssertionError er) {
                        log.error("Для поля '{}' сообщение об ошибке: '{}' не соответствует эталону: '{}'", nameOfField, viewingErrorText, errorPassSeriesText);
                        clientCabinetPage.isPassedFlag = false;
                    }
                    break;
            }
            $(By.xpath(fieldXpath)).clear();
        } else {
            log.info("Позитивный кейс");
            clientCabinetPage.errorFieldMessageNotVisibility(errorFieldXpath);
        }
    }

    void setRoleType(String roleType) {
        clientCabinetPage.setRoleType(roleType);
    }

    /**
     * Заполнение поля "Название организации"
     */
    private Random rnd = new Random();

    private void fillCompanyRandomValues() {
        enterValuesToField(rnd.nextInt(15) + 1, clientCabinetPage.companyNameXpath, "Название организации", clientCabinetPage.errorCompanyFieldMessageXpath, false, false);
    }

    /**
     * Заполнение поля "ФИО" Юр Лица
     */
    private void fillBioEntityRandomValues() {
        enterValuesToField(rnd.nextInt(15) + 1, clientCabinetPage.bioEntityXpath, "ФИО", clientCabinetPage.errorBioFieldXpath, false, false);
    }

    /**
     * Заполнение поля "ФИО" Физ Лица
     */
    private void fillBioIndividualRandomValues() {
        enterValuesToField(rnd.nextInt(15) + 1, clientCabinetPage.bioIndividualXpath, "ФИО", clientCabinetPage.errorBioFieldXpath, false, false);
    }

    /**
     * Заполнение поля "Доверенность"
     */
    private void fillDocumentRandomValues() {
        enterValuesToField(rnd.nextInt(15) + 1, clientCabinetPage.documentXpath, "Доверенность", clientCabinetPage.errorDocumentFieldXpath, false, false);
    }

    /**
     * Заполнение поля "Юридический адрес"
     */
    private void fillLawAddressRandomValues() {
        enterValuesToField(rnd.nextInt(15) + 1, clientCabinetPage.lawAddressEntityXpath, "Юридический адрес", clientCabinetPage.errorLawAddressFieldXpath, false, false);
    }

    /**
     * Заполнение поля "Адрес регистрации"
     */
    private void fillRegisterAddressRandomValues() {
        enterValuesToField(rnd.nextInt(15) + 1, clientCabinetPage.lawAddressIndividualXpath, "Адрес регистрации", clientCabinetPage.errorRegistrationAddressXpath, false, false);
    }


    /**
     * Заполнение поля "ИНН" юр лица c проверкой корректности ввода
     */
    private void fillTaxNumEntityRandomValues() {
        //введено больше 10 цифр
        enterValuesToField(11, clientCabinetPage.taxNumberEntityXpath, "ИНН", clientCabinetPage.errorTaxNumberFieldXpath, true, true);
        //введено меньше 10 цифр
        enterValuesToField(6, clientCabinetPage.taxNumberEntityXpath, "ИНН", clientCabinetPage.errorTaxNumberFieldXpath, true, true);
        //введены буквы
        enterValuesToField(11, clientCabinetPage.taxNumberEntityXpath, "ИНН", clientCabinetPage.errorTaxNumberFieldXpath, false, true);
        //введено 10 цифр
        enterValuesToField(10, clientCabinetPage.taxNumberEntityXpath, "ИНН", clientCabinetPage.errorTaxNumberFieldXpath, true, false);
    }

    /**
     * Заполнение поля "ИНН" физ лица c проверкой корректности ввода
     */
    private void fillTaxNumIndividualRandomValues() {
        //введено больше 12 цифр
        enterValuesToField(16, clientCabinetPage.taxNumberIndividualXpath, "ИНН физ. лица", clientCabinetPage.errorTaxNumberIndividualXpath, true, true);
        //введено меньше 10 цифр
        enterValuesToField(6, clientCabinetPage.taxNumberIndividualXpath, "ИНН физ. лица", clientCabinetPage.errorTaxNumberIndividualXpath, true, true);
        //введены буквы
        enterValuesToField(11, clientCabinetPage.taxNumberIndividualXpath, "ИНН физ. лица", clientCabinetPage.errorTaxNumberIndividualXpath, false, true);
        //введено 12 цифр
        enterValuesToField(12, clientCabinetPage.taxNumberIndividualXpath, "ИНН физ. лица", clientCabinetPage.errorTaxNumberIndividualXpath, true, false);
    }

    /**
     * Заполнение поля "КПП" c проверкой корректности ввода
     */
    private void fillKppRandomValues() {
        //введено больше 9 цифр
        enterValuesToField(11, clientCabinetPage.kppXpath, "КПП", clientCabinetPage.errorKppFieldXpath, true, true);
        //введено меньше 9 цифр
        enterValuesToField(6, clientCabinetPage.kppXpath, "КПП", clientCabinetPage.errorKppFieldXpath, true, true);
        //введены буквы
        enterValuesToField(11, clientCabinetPage.kppXpath, "КПП", clientCabinetPage.errorKppFieldXpath, false, true);
        //введено 9 цифр
        enterValuesToField(9, clientCabinetPage.kppXpath, "КПП", clientCabinetPage.errorKppFieldXpath, true, false);
    }

    /**
     * Заполнение поля "ОГРН" c проверкой корректности ввода
     */
    private void fillOgrnRandomValues() {
        //введено больше 13 цифр
        enterValuesToField(16, clientCabinetPage.ogrnXpath, "ОГРН", clientCabinetPage.errorOgrnFieldXpath, true, true);
        //введено меньше 13 цифр
        enterValuesToField(6, clientCabinetPage.ogrnXpath, "ОГРН", clientCabinetPage.errorOgrnFieldXpath, true, true);
        //введены буквы
        enterValuesToField(16, clientCabinetPage.ogrnXpath, "ОГРН", clientCabinetPage.errorOgrnFieldXpath, false, true);
        //введено 13 цифр
        enterValuesToField(13, clientCabinetPage.ogrnXpath, "ОГРН", clientCabinetPage.errorOgrnFieldXpath, true, false);
    }

    /**
     * Заполнение поля "ОКПО" c проверкой корректности ввода
     */
    private void fillOkpoRandomValues() {
        //введено больше 14 цифр
        enterValuesToField(16, clientCabinetPage.okpoXpath, "ОКПО", clientCabinetPage.errorOkpoFieldXpath, true, true);
        //введено меньше 14 цифр, но не 8 и не 10
        enterValuesToField(6, clientCabinetPage.okpoXpath, "ОКПО", clientCabinetPage.errorOkpoFieldXpath, true, true);
        //введены буквы
        enterValuesToField(16, clientCabinetPage.okpoXpath, "ОКПО", clientCabinetPage.errorOkpoFieldXpath, false, true);
        //введено 14 цифр
        enterValuesToField(14, clientCabinetPage.okpoXpath, "ОКПО", clientCabinetPage.errorOkpoFieldXpath, true, false);
        $(By.xpath(clientCabinetPage.okpoXpath)).clear();
        log.info("Поле \"ОКПО\" очищено");
        //введено 8 цифр
        enterValuesToField(8, clientCabinetPage.okpoXpath, "ОКПО", clientCabinetPage.errorOkpoFieldXpath, true, false);
        $(By.xpath(clientCabinetPage.okpoXpath)).clear();
        log.info("Поле \"ОКПО\" очищено");
        //введено 10 цифр
        enterValuesToField(10, clientCabinetPage.okpoXpath, "ОКПО", clientCabinetPage.errorOkpoFieldXpath, true, false);
    }

    /**
     * Заполнение поля "Расчетный счет" c проверкой корректности ввода
     */
    private void fillAccountRandomValues() {
        //введено больше 20 цифр
        enterValuesToField(26, clientCabinetPage.accountXpath, "Расчетный счет", clientCabinetPage.errorAccountFieldXpath, true, true);
        //введено меньше 20 цифр
        enterValuesToField(6, clientCabinetPage.accountXpath, "Расчетный счет", clientCabinetPage.errorAccountFieldXpath, true, true);
        //введены буквы
        enterValuesToField(16, clientCabinetPage.accountXpath, "Расчетный счет", clientCabinetPage.errorAccountFieldXpath, false, true);
        //введено 20 цифр
        enterValuesToField(20, clientCabinetPage.accountXpath, "Расчетный счет", clientCabinetPage.errorAccountFieldXpath, true, false);
    }

    /**
     * Заполнение поля "Корреспондентский счет" c проверкой корректности ввода
     */
    private void fillCorrRandomValues() {
        //введено больше 20 цифр
        enterValuesToField(26, clientCabinetPage.corrXpath, "Корреспондентский счет", clientCabinetPage.errorCorrXpath, true, true);
        //введено меньше 20 цифр
        enterValuesToField(6, clientCabinetPage.corrXpath, "Корреспондентский счет", clientCabinetPage.errorCorrXpath, true, true);
        //введены буквы
        enterValuesToField(16, clientCabinetPage.corrXpath, "Корреспондентский счет", clientCabinetPage.errorCorrXpath, false, true);
        //введено 20 цифр
        enterValuesToField(20, clientCabinetPage.corrXpath, "Корреспондентский счет", clientCabinetPage.errorCorrXpath, true, false);
    }

    /**
     * Заполнение поля "БИК" c проверкой корректности ввода
     */
    private void fillBicRandomValues() {
        //введено больше 9 цифр
        enterValuesToField(26, clientCabinetPage.bicXpath, "БИК", clientCabinetPage.errorBicXpath, true, true);
        //введено меньше 9 цифр
        enterValuesToField(6, clientCabinetPage.bicXpath, "БИК", clientCabinetPage.errorBicXpath, true, true);
        //введены буквы
        enterValuesToField(16, clientCabinetPage.bicXpath, "БИК", clientCabinetPage.errorBicXpath, false, true);
        //введено 9 цифр
        enterValuesToField(9, clientCabinetPage.bicXpath, "БИК", clientCabinetPage.errorBicXpath, true, false);
    }

    /**
     * Заполнение поля "Фактический адрес"
     */
    private void fillPostAddressRandomValues() {
        enterValuesToField(rnd.nextInt(15) + 1, clientCabinetPage.postAddressXpath, "Фактический адрес", clientCabinetPage.errorPostAddressFieldXpath, false, false);
    }

    /**
     * Заполнение поля "Банк"
     */
    private void fillBankRandomValues() {
        enterValuesToField(rnd.nextInt(15) + 1, clientCabinetPage.bankXpath, "Банк", clientCabinetPage.errorBankFieldXpath, false, false);
    }

    /**
     * Заполнение поля "Телефон"
     */
    private void fillPhoneRandomValues() {
        enterValuesToField(10, clientCabinetPage.phoneXpath, "Телефон", clientCabinetPage.errorPhoneXpath, true, false);
    }

    /**
     * Заполнение поля "Серия и номер паспорта" c проверкой корректности ввода
     */
    private void fillPassNumRandomValues() {
        //введено больше 10 цифр
        enterValuesToField(12, clientCabinetPage.passSeriesNumXpath, "Серия и номер паспорта", clientCabinetPage.errorPassSeriesXpath, true, true);
        //введено меньше 10 цифр
        enterValuesToField(6, clientCabinetPage.passSeriesNumXpath, "Серия и номер паспорта", clientCabinetPage.errorPassSeriesXpath, true, true);
        //введены буквы
        enterValuesToField(11, clientCabinetPage.passSeriesNumXpath, "Серия и номер паспорта", clientCabinetPage.errorPassSeriesXpath, false, true);
        //введено 10 цифр
        enterValuesToField(10, clientCabinetPage.passSeriesNumXpath, "Серия и номер паспорта", clientCabinetPage.errorPassSeriesXpath, true, false);
    }

    /**
     * Заполнение поля "СНИЛС" c проверкой корректности ввода
     */
    private void fillSnilsRandomValues() {
        //введено больше 11 цифр
        enterValuesToField(13, clientCabinetPage.snilsXpath, "СНИЛС", clientCabinetPage.errorSnilsXpath, true, true);
        //введено меньше 11 цифр
        enterValuesToField(6, clientCabinetPage.snilsXpath, "СНИЛС", clientCabinetPage.errorSnilsXpath, true, true);
        //введены буквы
        enterValuesToField(11, clientCabinetPage.snilsXpath, "СНИЛС", clientCabinetPage.errorSnilsXpath, false, true);
        //введено 11 цифр
        enterValuesToField(11, clientCabinetPage.snilsXpath, "СНИЛС", clientCabinetPage.errorSnilsXpath, true, false);
    }

    /**
     * Заполнение поля "Кем и когда выдан" c проверкой корректности ввода
     */
    private void fillPassGetRandomValues() {
        enterValuesToField(10, clientCabinetPage.passGetNumXpath, "Кем и когда выдан", clientCabinetPage.errorPassGetXpath, true, false);
    }

    /**
     * Заполнение всех полей юридического лица
     */
    void fillAllFieldsOfEntityForm() {
        fillCompanyRandomValues();
        fillBioEntityRandomValues();
        fillDocumentRandomValues();
        fillLawAddressRandomValues();
        fillTaxNumEntityRandomValues();
        fillKppRandomValues();
        fillOgrnRandomValues();
        fillOkpoRandomValues();
        fillAccountRandomValues();
        fillCorrRandomValues();
        fillBioEntityRandomValues();
        fillPostAddressRandomValues();
        fillBankRandomValues();
        fillBicRandomValues();
        fillPhoneRandomValues();
    }

    /**
     * Заполнение всех полей физического лица
     */
    void fillAllFieldsOfIndividualForm() {
        fillBioIndividualRandomValues();
        fillRegisterAddressRandomValues();
        fillTaxNumIndividualRandomValues();
        fillSnilsRandomValues();
        fillPassNumRandomValues();
        fillPassGetRandomValues();
    }

    /**
     * Нажать сохранить и проверить что профиль сохранен юр лицо
     */
    void saveProfileChangesEntity() {
        String profileSuccessfullySaved = "//form[@name=\"OOO\"]//span[text()=\"Профиль сохранен\"]";
        saveButtonEntityClick();
        try {
            $(By.xpath(profileSuccessfullySaved)).shouldBe(visible);
            log.info("\"Профиль сохранен\" отображено");
        } catch (UIAssertionError er) {
            log.error("\"Профиль сохранен\" не отобразилось");
            clientCabinetPage.isPassedFlag = false;
            er.printStackTrace();
        }
    }

    /**
     * Нажать сохранить и проверить что профиль сохранен физ лицо
     */
    void saveProfileChangesIndividual() {
        String profileSuccessfullySaved = "//form[@name=\"FL\"]//span[text()=\"Профиль сохранен\"]";
        saveButtonIndividualClick();
        try {
            $(By.xpath(profileSuccessfullySaved)).shouldBe(visible);
            log.info("\"Профиль сохранен\" отображено");
        } catch (UIAssertionError er) {
            log.error("\"Профиль сохранен\" не отобразилось");
            clientCabinetPage.isPassedFlag = false;
            er.printStackTrace();
        }
    }


    void testIsPassedAssertion() {
        Assert.assertTrue(clientCabinetPage.isPassedFlag);
    }
}
