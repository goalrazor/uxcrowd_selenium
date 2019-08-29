package ru.performance.config;

import org.aeonbits.owner.Config;

@Config.Sources({"file:uxcrowd.config"})
public interface UxCrowdConfig extends Config {

    // URL тестируемого хоста
    @DefaultValue("https://test.uxcrowd.ru")
    String testUrl();

    // Директория с графическими паттернами для Sikuli
    @DefaultValue("127.0.0.1")
    String sikuliPatternsDir();

    // Флаг для работы с CI сервером, отключающий все отладочные фичи
    @DefaultValue("true")
    Boolean ciServerFlag();

    // Флаг для подключения видеоплагина
    @DefaultValue("true")
    Boolean videoPluginFlag();

    // Использование удалённых браузеров, таких как "Selenium Grid" или "Solenoid"
    @DefaultValue("true")
    Boolean remoteBrowserFlag();

    @DefaultValue("127.0.0.1")
    String remoteSeleniumHub();

    @DefaultValue("4444")
    String remoteSeleniumHubPort();

    // Время приостановки действий для замедления работы
    @DefaultValue("false")
    Boolean uiActionSlowdownFlag();

    @DefaultValue("0")
    int uiActionSlowdown();

    // Подсветка Web-элементов
    @DefaultValue("false")
    Boolean uiFlashFlag();

    @DefaultValue("100")
    int uiActionInterval();

    @DefaultValue("#00FF00")
    String uiFlashColor();

    @DefaultValue("1")
    int uiFlashCount();

    // Размер окна браузера
    @DefaultValue("1920x1080")
    String browserSize();

    // Используемый браузер
    @DefaultValue("chrome")
    String selenideBrowser();

    // Время ожидания до падения теста
    @DefaultValue("4")
    int failTestTimeout();

    // Учётная запись клиента
    String clientName();

    String clientPassword();

    String badClientName();

    String badClientPassword();

    String clientYandexName();

    String clientYandexPassword();

    String clientForTariffChangingLogin();

    String clientForTariffChangingPassword();

    // Учётная запись тестера
    String testerName();

    String testerPassword();

    // Учётная запись модератора
    String moderatorName();

    String moderatorPassword();

    // Учётная запись администратора
    String adminName();

    String adminPassword();

    // Учётная запись "Vkontakte"
    String vkUserName();

    String vkUserPassword();

    // Учётная запись "Google"
    String googleUserName();

    String googleUserPassword();

    String additionalUserFirstName();

    String additionalUserLastName();

    // Учётная запись "Twitter"
    String twitterUserName();

    String twitterUserPassword();

    // E-mail для тестов восстановления паролей
    String recoveryPasswordEmail();

    String recoveryPasswordEmailPassword();

    String recoveryPasswordNewPassword();

    // Максимальное время ожидания ответа от E-mail сервера в минутах
    @DefaultValue("5")
    int maxEmailWaitTime();

    // Имя файла кастомного отчёта
    @DefaultValue("pflb_custom_report.json")
    String customReportFileName();

    // Время просмотра после тестов глазами
    @DefaultValue("0")
    int eyesViewTimeout();

    //Реквизиты для оплаты банковской картой
    String cardNumber();

    String cardExpiredDate();

    String cardCVCCode();
}
