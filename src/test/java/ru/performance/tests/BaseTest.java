package ru.performance.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.epam.reportportal.testng.ReportPortalTestNGListener;
import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.internal.collections.Pair;
import rp.com.google.common.io.BaseEncoding;
import ru.performance.config.UxCrowdConfig;
import ru.performance.tests.flash.ListenerThatHighlightsElements;
import ru.performance.tests.flash.ListenerThatWaitsBeforeAnyAction;
import ru.performance.tests.pageobject.BasePage;
import ru.performance.tests.pageobject.StartPage;
import ru.performance.tests.tools.ScreenShotOnFailListener;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.addListener;
import static com.codeborne.selenide.WebDriverRunner.setWebDriver;
import static org.apache.commons.lang3.math.NumberUtils.toInt;


@Listeners({ScreenShotOnFailListener.class, ReportPortalTestNGListener.class})
public abstract class BaseTest {

    protected static final Logger log = LogManager.getLogger();
    private static UxCrowdConfig uxCrowdConfig = ConfigFactory.create(UxCrowdConfig.class);

    /**
     * Форматировать дату и время, выставить временнУю зону Москвы
     */
    @BeforeSuite
    public static void timeInit() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        dateFormat.setTimeZone(tz);
        log.debug("===> Start time: " + dateFormat.format(new Date()));
    }

    /**
     * Выставить параметры Selenide
     */
    @BeforeSuite
    public static void selenideSetUp() {
        Configuration.timeout = 1000 * uxCrowdConfig.failTestTimeout();  // из секунд в мс
        Configuration.browser = uxCrowdConfig.selenideBrowser();
        Configuration.browserSize = uxCrowdConfig.browserSize();
        Configuration.screenshots = false;      // Делать ли скриншот средсвами Selenide

        // Стратегия ожидания полной загрузки страницы (eager, none, normal)
        Configuration.pageLoadStrategy = "normal";

        log.info("===> remoteBrowserFlag: " + uxCrowdConfig.remoteBrowserFlag());

        // Для работы через Selenium Grid
        if (uxCrowdConfig.remoteBrowserFlag()) {
            String remoteSeleniumHub = uxCrowdConfig.remoteSeleniumHub();
            log.info(String.format("Удалённый Selenium Hub: %s", remoteSeleniumHub));

            String remoteSeleniumHubPort = uxCrowdConfig.remoteSeleniumHubPort();
            log.info(String.format("Порт удалённого Selenium Hub: %s", remoteSeleniumHubPort));

            Configuration.remote = String.format("http://%s:%s/wd/hub", remoteSeleniumHub, remoteSeleniumHubPort);
            Configuration.browserCapabilities.setCapability("enableVNC", true);
        }
    }

    /**
     * Смотреть на результат глазами
     */
    @AfterSuite(alwaysRun = true)
    public static void withMansEyesView() {
        if (!uxCrowdConfig.ciServerFlag()) {
            int sleepTime = uxCrowdConfig.eyesViewTimeout();
            log.info("Смотрим глазами на результат {} секунд", sleepTime);
            sleep(sleepTime * 1000);        // в миллисекундах
        }
    }

    /**
     * Закрыть браузер с кастомным WebDriver
     */
    @AfterSuite(alwaysRun = true, dependsOnMethods = {"withMansEyesView"})
    public static void closeBrowser() {
        if (uxCrowdConfig.videoPluginFlag() || uxCrowdConfig.ciServerFlag()) {
            log.debug(
                    String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
            WebDriverRunner.getWebDriver().quit();
        }
    }

    /**
     * Сделать скриншот для RP отчёта
     *
     * @param screenshotsLabel Сообщение-подпись для скриншота
     */
    public static void screenShot(String screenshotsLabel) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));

        byte[] screenshot_image = new byte[0];

        try {
            screenshot_image = ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
        } catch (Exception ex) {
            log.error(String.format("===> Ошибка при создании скриншота. Exception: '%s'", ex));
        }

        log.info("RP_MESSAGE#BASE64#{}#{}", BaseEncoding.base64().encode(screenshot_image), screenshotsLabel);
    }

    /**
     * Возвратить аккаунт-пару для пользователя с ролью "клиент"
     *
     * @return Пара - имя/пароль пользователя
     */
    public static Pair getClientAccountPair() {
        return Pair.create(uxCrowdConfig.clientName(), uxCrowdConfig.clientPassword());
    }

    /**
     * Возвратить аккаунт-пару для пользователя с ролью "клиент яндекс"
     *
     * @return Пара - имя/пароль пользователя
     */
    public static Pair getClientAccountYandexPair() {
        return Pair.create(uxCrowdConfig.clientYandexName(), uxCrowdConfig.clientYandexPassword());
    }

    /**
     * Возвратить аккаунт-пару для пользователя с ролью "клиент для смены тарифа"
     *
     * @return Пара - имя/пароль пользователя
     */
    public static Pair getClientAccountTariffChangingPair() {
        return Pair.create(uxCrowdConfig.clientForTariffChangingLogin(), uxCrowdConfig.clientForTariffChangingPassword());
    }


    /**
     * Замедлить выполнение тестовых сценариев
     */
    @BeforeSuite
    public void setActionSlowdown() {
        if (uxCrowdConfig.uiActionSlowdownFlag() && !uxCrowdConfig.ciServerFlag()) {
            addListener(
                    new ListenerThatWaitsBeforeAnyAction(uxCrowdConfig.uiActionSlowdown(), TimeUnit.MILLISECONDS));
        }
    }

    /**
     * Подсветить в браузере элементы
     */
    @BeforeSuite
    public void setHighlighting() {
        if (uxCrowdConfig.uiFlashFlag() && !uxCrowdConfig.ciServerFlag()) {
            addListener(new ListenerThatHighlightsElements(
                    uxCrowdConfig.uiFlashColor(),
                    uxCrowdConfig.uiFlashCount(),
                    uxCrowdConfig.uiActionInterval(),
                    TimeUnit.MILLISECONDS)
            );
        }
    }

    /**
     * Подключить видео-плагин
     */
    @BeforeSuite(alwaysRun = true)
    public void videoPluginSetUp() throws MalformedURLException {
        if (uxCrowdConfig.videoPluginFlag() || uxCrowdConfig.ciServerFlag()) {

            // Плагин подключить
            ChromeOptions options = new ChromeOptions();
            //Отключить запрос микрофона
            options.addArguments("use-fake-ui-for-media-stream").
                    addArguments("allow-file-access-from-files").
                    addArguments("use-fake-device-for-media-stream");
            options.addExtensions(new File("src/test/resources/plugins/dist_ru.crx"));
            Configuration.browserCapabilities = new DesiredCapabilities();
            Configuration.browserCapabilities.setCapability(ChromeOptions.CAPABILITY, options);

            WebDriver webDriver;
            if (!uxCrowdConfig.remoteBrowserFlag()) {
                log.info("remoteBrowserFlag: " + uxCrowdConfig.remoteBrowserFlag());
                // Кастомный WebDriver - c Selenid-ным плагин не подключается
                System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver");
                webDriver = new ChromeDriver(options);
            } else {
                // Для работы через Selenium Grid
                String remoteSeleniumHub = uxCrowdConfig.remoteSeleniumHub();
                log.info(String.format("Удалённый Selenium Hub: %s", remoteSeleniumHub));
                String remoteSeleniumHubPort = uxCrowdConfig.remoteSeleniumHubPort();
                log.info(String.format("Порт удалённого Selenium Hub: %s", remoteSeleniumHubPort));
                webDriver = new RemoteWebDriver(
                        new URL(String.format("http://%s:%s/wd/hub", remoteSeleniumHub, remoteSeleniumHubPort)),
                        Configuration.browserCapabilities);
            }

            String browserSize = uxCrowdConfig.browserSize();
            webDriver.manage().window().setSize(
                    new Dimension(toInt(browserSize.split("x")[0]), toInt(browserSize.split("x")[1])));

            setWebDriver(webDriver);

        }
    }

    /**
     * Проверить доступность хоста
     */
    @BeforeTest(alwaysRun = true)
    public static void hostAccessibility() {

        String testUrl = getTestUrl();

        // Проверить доступность хоста
        hostAccessibility(testUrl);
    }

    /**
     * Получить URL тестируемого сайта
     *
     * @return URL
     */
    private static String getTestUrl() {

        // Если URL не указан в коммандной строке Gradle, то взять из конфигурационного файла
        String testUrl = System.getProperty("testUrl");
        if (testUrl == null) {
            testUrl = uxCrowdConfig.testUrl();
        }

        return testUrl;
    }

    /**
     * Проверить доступность страницы
     *
     * @param url  URL страницы
     * @param page Объект PageObject страницы
     */
    private static void checkPageAccessibility(String url, BasePage page) {
        log.info("URL тестирумого автотестами хоста: '{}'", url);

        // Открыть страницу
        long beginOpenSiteTime = new Date().getTime();    // Время начала открывания страницы
        open(url);
        // Очистить браузер
        clearBrowser();

        // Проверить отображение страницы
        page.checkPageShow();
        long endOpenSiteTime = new Date().getTime();    // Время окончания открытия страницы

        log.info(String.format("Время открывания страницы, (секунды) '%s': '%d'.",
                url, (endOpenSiteTime - beginOpenSiteTime) / 1000)
        );
    }

    /**
     * Перейти на сайт с проверкой отображения стартовой страницы
     */
    public static void goToWebsite() {
        String testUrl = getTestUrl();
        hostAccessibility(testUrl);
        log.info(String.format("Переход на сайт '%s' прошёл успешно", testUrl));
    }

    /**
     * Проверить доступность хоста
     *
     * @param testUrl URL хоста
     */
    private static void hostAccessibility(String testUrl) {

        checkPageAccessibility(testUrl, new StartPage());
        log.debug("Хост доступен");
        if (log.getLevel() == Level.DEBUG) {
            screenShot("Отображение стартовой страницы хоста");
        }
    }

    /**
     * Возвратить аккаунт-пару (имя пользователя и его пароль)
     *
     * @param name     Имя пользователя
     * @param password Пароль пользователя
     * @return Пара - имя/пароль пользователя
     */
    protected Pair<String, String> getAccountPair(String name, String password) {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));


        // Если не указан пользователь или пароль (в "gradle.properties"), то фейлить тест
        if (name == null || password == null) {
            log.error("===> No user name or password specified (presumably, in file 'gradle.properties')");
            assert false;
        }

        return Pair.create(name, password);
    }

    /**
     * Возвратить аккаунт-пару для пользователя с ролью "модератор"
     *
     * @return Пара - имя/пароль пользователя
     */
    protected Pair getModeratorAccountPair() {
        return Pair.create(uxCrowdConfig.moderatorName(), uxCrowdConfig.moderatorPassword());
    }

    /**
     * Генератор случайной строки
     *
     * @param length требуемая длина строки
     * @return случайная строка с длиной length
     */
    protected StringBuilder randomStringGenerator(int length) {
        String symbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder randString = new StringBuilder();
        for (int i = 0; i < length; i++)
            randString.append(symbols.charAt((int) (Math.random() * symbols.length())));
        return randString;
    }

    /**
     * Генератор строки из цифр
     *
     * @param length длина строки
     * @return строка из случайных цифр с длиной length
     */
    protected StringBuilder randomStringOfIntsGenerator(int length) {
        String symbols = "1234567890";
        StringBuilder randString = new StringBuilder();
        for (int i = 0; i < length; i++)
            randString.append(symbols.charAt((int) (Math.random() * symbols.length())));
        return randString;
    }

    /**
     * Очистить куки и "local storage" в браузере
     */
    private static void clearBrowser() {

        if (log.getLevel() == Level.DEBUG) {
            BaseTest.screenShot("Скриншот до очистки браузера");
        }

        // Чистить браузер
        Selenide.clearBrowserCookies();
        log.debug("Cookies в браузере очищены");
        Selenide.clearBrowserLocalStorage();
        log.debug("Local storage в браузере очишено");

        // Перегрузить страницу
        refresh();

        if (log.getLevel() == Level.DEBUG) {
            BaseTest.screenShot("Скриншот после очистки браузера и перезагрузки страницы");
        }
    }

}
