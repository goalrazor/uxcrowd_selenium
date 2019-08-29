package ru.performance.tests.tools;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Arrays;
import java.util.List;


public class PostShiftApi {
    protected static final Logger log = LogManager.getLogger();
    private String uri = "http://postshift.ru/api.php?";
    private String email;
    private String key;

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    private void setKey(String key) {
        this.key = key;
    }

    /**
     * Создаем новый почтовый ящик и сохраняем e-mail и ключ
     */
    public void createMailBox() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        //Создаем почтовый ящик
        String response = RestAssured.given()
                .accept(ContentType.JSON).when()
                .get(uri + "action=new").asString();
        // HttpResponse<String> response = Unirest.get(uri+"action=new").asString();

        //Сохраняем значения e-mail и ключа
        List<String> pair = Arrays.asList(response.split("\n"));
        String emailString = pair.get(0).substring(6).trim();
        String keyString = "";
        try {
            keyString = pair.get(1).substring(4).trim();
        } catch (ArrayIndexOutOfBoundsException ex) {
            log.error("Возможно, слишком много запросов к E-mail сервису");
            assert false;
        }
        setEmail(emailString);
        setKey(keyString);
        log.info("E-mail {} успешно создан", email);
        log.info("Ключ к созданному ящику {} успешно создан", key);

    }

    /**
     * Получаем ссылку из первого письма для клиента
     */
    public String getFirstLetterLinkClient() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        String url = uri + "action=getmail&key=" +
                key + "&id=1";
        //Получаем текст письма
        String response = RestAssured.given()
                .accept(ContentType.XML).when()
                .get(url).getBody().asString();
        log.info("Успешно получено первое письмо из ящика");
        return linkParserForClient(response);

    }

    /**
     * Получаем ссылку из первого письма для тестеровщика
     */
    public String getFirstLetterLinkTester() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        String url = uri + "action=getmail&key=" +
                key + "&id=1";
        //Получаем текст письма
        String response = RestAssured.given()
                .accept(ContentType.XML).when()
                .get(url).getBody().asString();
        log.info("Успешно получено первое письмо из ящика");
        return linkParserForTester(response);

    }

    /**
     * Получаем список всех входящих
     */
    public String getListOfMails() {
        log.debug(String.format("===> Start method: %s", Thread.currentThread().getStackTrace()[1].getMethodName()));
        String url = uri + "action=getlist&key=" +
                key;
        //Получаем список мейлов String
        log.debug("Получен список писем");
        return RestAssured
                .given().accept(ContentType.JSON)
                .when().get(url).getBody().asString();
    }

    /**
     * Парсер содержимого письма. Выуживает ссылку для смены пароля из письма тестировщика.
     *
     * @param emailContent Содержимое письма
     * @return Ссылка для смены пароля
     */
    private String linkParserForTester(String emailContent) {
        Document doc = Jsoup.parse(emailContent);
        return doc.getElementsContainingText("Для подтверждения регистрации").next().attr("href");
    }

    /**
     * Парсер содержимого письма. Выуживает ссылку для смены пароля из письма клиента.
     *
     * @param emailContent Содержимое письма
     * @return Ссылка для смены пароля
     */
    private String linkParserForClient(String emailContent) {
        Document doc = Jsoup.parse(emailContent);
        return doc.getElementsContainingText("Пожалуйста,").next().attr("href");
    }
}