package ru.performance.tests.suites.Requisites;

import com.epam.reportportal.testng.ReportPortalTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import ru.performance.tests.pageobject.cabinetspages.ClientCabinetPage;
import ru.performance.tests.tools.ScreenShotOnFailListener;

@Listeners({ScreenShotOnFailListener.class, ReportPortalTestNGListener.class})
public class Requisites extends RequisitesHelper {

    @Test(description = "1.2.2 Проверка редактирования профиля клиента Юридическое лицо", groups = "B1")
    public void entityRequisitesTest() {
        ClientCabinetPage clientCabinetPage = new ClientCabinetPage();
        clientCabinetPage.isPassedFlag = true; //сбрасываем флаг об удачном прохождении в начале теста
        goToWebsite();              // Перейти на сайт
        clientLogin();              // Залогиниться Клиентом
        goProfileMenu();            // перейти в настройки профиля
        setRoleType("Юридическое лицо"); //выставить роль
        clearProfileEntity();       //очистить все по ля в профиле с проверкой высвечивания ошибок
        setCheckBoxToAgreeEntity();       //Установить чекбокс "согласен на соглашение о конфенденциальности"
        saveButtonEntityClick();          //Нажать на кнопку сохранить
        negativeAssertAllFieldsEntity();  //Проверка что все поля красные
        fillAllFieldsOfEntityForm();//Заполнение всех полей Юридического лица
        saveProfileChangesEntity();       //Сохранить и проверить что все сохранилось
        testIsPassedAssertion();    //Проверка на наличие ошибок
    }

    @Test(description = "1.2.1 Проверка редактирования профиля клиента Физическое лицо", groups = "B1")
    public void individualRequisitesTest() {
        ClientCabinetPage clientCabinetPage = new ClientCabinetPage();
        clientCabinetPage.isPassedFlag = true; //сбрасываем флаг об удачном прохождении в начале теста
        goToWebsite();              // Перейти на сайт
        clientLogin();              // Залогиниться Клиентом
        goProfileMenu();            // перейти в настройки профиля
        setRoleType("Физическое лицо"); //выставить роль
        clearProfileIndividual();   //очистить все поля в профиле с проверкой высвечивания ошибок
        setCheckBoxToAgreeIndividual();       //Установить чекбокс "согласен на соглашение о конфенденциальности"
        saveButtonIndividualClick();          //Нажать на кнопку сохранить
        negativeAssertAllFieldsIndividual();  //Проверка что все поля красные
        fillAllFieldsOfIndividualForm();//Заполнение всех полей Физического лица
        saveProfileChangesIndividual();       //Сохранить и проверить что все сохранилось
        testIsPassedAssertion();    //Проверка на наличие ошибок
    }
}
