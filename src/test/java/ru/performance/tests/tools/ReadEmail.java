package ru.performance.tests.tools;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;


public class ReadEmail {

    protected static final Logger log = LogManager.getLogger();

    private String mailHost;
    private int mailPort;
    private String userName;
    private String userPassword;


    public ReadEmail(String mailHost, int mailPort, String userName, String userPassword) {
        this.mailHost = mailHost;
        this.mailPort = mailPort;
        this.userName = userName;
        this.userPassword = userPassword;
    }


    /**
     * Читать почту
     *
     * @param beginEmailWaitTime Время, до которого письма считать "старыми"
     * @param endEmailWaitTime   Время, до которого ждать письма
     * @return Содержимое (HTML) последнего письма в виде строки. Если "новых" писем нет, то пустая строка.
     */
    public String imapReadMail(long beginEmailWaitTime, long endEmailWaitTime) throws MessagingException, IOException {

        String emailContent = "";
        Session session = Session.getDefaultInstance(new Properties());
        Store store = session.getStore("imaps");
        store.connect(mailHost, mailPort, userName, userPassword);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        // Получить непросмотренные письма
        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        if (messages.length != 0) {     // Когда есть непрочитанные письма
            // Пересортировать письма - свежие в начало
            Arrays.sort(messages, (message1, message2) -> {
                try {
                    return message2.getSentDate().compareTo(message1.getSentDate());
                } catch (MessagingException ex) {
                    throw new RuntimeException(ex);
                }
            });

            // Самое свежее письмо
            log.debug("Самое свежее письмо: " + messages[0].getSentDate() + ", Subject: " + messages[0].getSubject());

            // Проверить, что самое свежее письмо не "старое"
            if (messages[0].getSentDate().getTime() < beginEmailWaitTime) {
                log.debug(String.format("Нет новых писем. Время последнего письма: %s. Время свежих писем > %s.",
                        messages[0].getSentDate().getTime(), beginEmailWaitTime));

                long currentWaitTime = (endEmailWaitTime - new Date().getTime()) / 1000;
                if (currentWaitTime > 0) {
                    log.debug(String.format("Ещё ждать письма %s секунд", currentWaitTime));
                } else {
                    log.debug("Время ожидания письма истекло");
                }
            } else {
                // Содержимое самого свежего письма
                emailContent = (String) messages[0].getContent();
            }
        }

        return emailContent;
    }
}
