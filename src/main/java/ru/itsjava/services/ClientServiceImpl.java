package ru.itsjava.services;

import lombok.Data;
import lombok.SneakyThrows;


import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

@Data
public class ClientServiceImpl implements ClientService {

    public final static int PORT = 8081;
    public final static String HOST = "localhost";
    public MessageInputService messageInputService;
    public PrintWriter serverWriter;
    private int authFlag;
    private String saveFileDestination;
    private static final Logger log = Logger.getLogger(ClientServiceImpl.class);


    @SneakyThrows
    @Override
    public void start() {

        log.info("Application started");

        Socket socket = new Socket(HOST, PORT);
        if (socket.isConnected()) {

            // Подсоединились и сразу запускаем прослушивание сокета:
            // Создаём нить, пихаем в неё наш класс для чтения потока (String) с сервера.
            SocketRunnable socketRunnable = new SocketRunnable(socket, this);
            new Thread(socketRunnable).start();
            // Для того чтобы отправить текстовое сообщение на сервер, необходимо создать исходящий поток.
            // Чтобы создать поток, используем метод getOutputStream() на нашем созданном socket
            serverWriter = new PrintWriter(socket.getOutputStream());

            // Необходимо, чтобы клиент мог сначала писать сообщение в консоль, а потом отправлять его.
            // Используем наш написанный MessageInputService с BufferedReader внутри для этой задачи:
            messageInputService = new MessageInputServiceImpl(System.in);

            authFlag = 0;

            // Считываем данные от сервера:
            //BufferedReader messageFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Меню
            while (true) {
                menu(); // печатаем меню
                int exitFlag = 0; // пока не авторизированы -- 0
                // Проверяем флаг авторизации из socketRunnable. Пока авторизированны, можем писать сообщения.
                // Как только выходим из учётки данного пользователя в меню по команде !exit, флаг авторизации снова 0 и цикл продолжается.
                while (exitFlag == 0) { // слушаем ответ от сервера с подтверждением авторизации.
                    Thread.sleep(3000L); // TODO Спросить, что за херня -- без этой задержки за каким то хреном не работают параллельные потоки
                    if (authFlag == 1) {
                        writeMessage();
                        exitFlag = 1;
                    }
                }
            }
        }
    }


    public void menu() {
        System.out.println("    **** CHAT ****");
        System.out.println("    1 -- Войти в чат");
        System.out.println("    2 -- Новый пользователь");
        System.out.println("    !help -- доступные команды");
        System.out.println("    !exit -- Выйти из программы");
        String menuNum = messageInputService.getMessage();

        // любая строка и !help
        while (!(menuNum.equals("1") || menuNum.equals("2") || menuNum.equals("!exit"))) {
            System.out.println("   **** Выберете пункт меню ****");
            System.out.println("   1 -- Войти в чат");
            System.out.println("   2 -- Новый пользователь");
            System.out.println("   !help -- доступные команды");
            System.out.println("   !exit -- Выйти из чата");
            menuNum = messageInputService.getMessage();
            while (menuNum.equals("!help")) { // отображаем доступные команды
                System.out.println("" +
                        "   !pm RECIPIENT_NAME MESSAGE -- написать личное сообщение пользователю. Например: !pm Ваня Привет, Ваня!\n" +
                        "   !printLast X -- вывести на экран последние 'X' сообщений. Например: !printLast 10\n" +
                        "   !who -- вывести на экран всех онлайн участников чата.\n" +
                        "   !save DESTINATION -- сохранить переписку в файл. Например: !save d:\\message.txt\n" +
                        "   !exit -- выйти из чата в главное меню.\n\n" +
                        "   **** Выберете пункт меню ****\n" +
                        "   1 -- Войти в чат\n" +
                        "   2 -- Новый пользователь\n" +
                        "   !help -- доступные команды\n" +
                        "   !exit -- Выйти из чата");
                menuNum = messageInputService.getMessage();
            }
        }
        // получили 1, 2 или !exit
        switch (menuNum) {
            case "1":
                authorization();
                break;
            case "2":
                registration();
                break;
            case "!exit":
                System.out.println("Закрываем чат...");
                log.info("Exited chat application");
                System.exit(0);
        }
    }

    public void setAuthFlag(int authFlag) {
        this.authFlag = authFlag;
    }

    // метод для сохранения переписки в файл
    public void setSaveFileDestination(String destination) {
        this.saveFileDestination = destination;
    }
    // метод для сохранения переписки в файл
    public String getSaveFileDestination() {
        return this.saveFileDestination;
    }

    public void authorization() {
        System.out.println("**** Авторизация ****");
        System.out.print("Введите логин: ");
        String login = messageInputService.getMessage();
        System.out.print("Введите пароль: ");
        String password = messageInputService.getMessage();
        password = passwordEncryptionMD5(password); // энкриптим пароль
        serverWriter.println("!autho!" + login + ":" + password);
        log.info("!autho!" + login + ":" + password);
        serverWriter.flush();
    }

    public void registration() {
        System.out.println("**** Регистрация нового пользователя ****");
        System.out.print("Введите логин нового пользователя: ");
        String login = messageInputService.getMessage();
        System.out.print("Введите пароль нового пользователя: ");
        String password = messageInputService.getMessage();
        password = passwordEncryptionMD5(password); // энкриптим пароль
        serverWriter.println("!reg!" + login + ":" + password);
        log.info("!reg!" + login + ":" + password);
        serverWriter.flush();

    }

    public void writeMessage() {
        // Цикл для отправки сообщения пользователем:
        while (true) {
            String consoleMessage = messageInputService.getMessage(); // считываем сообщение из консоли
            // Выход из чата:
            if (consoleMessage.equals("!exit")) {
                serverWriter.println(consoleMessage);
                serverWriter.flush();
                System.out.println("Выход из чата в меню...");
                break;
            }
            if (consoleMessage.startsWith("!save")) {
                try {
                    setSaveFileDestination(consoleMessage.substring(6)); // указываем место для сохранения файла
                } catch (Exception e) {
                    System.out.println("Error saving file to specified destination");
                    log.error("Error saving file to specified destination -- " + consoleMessage);
                }
            }
            serverWriter.println(consoleMessage); // Пишем текст для отправки на сервер:
            serverWriter.flush(); // Чтобы данные не буфферизировались, отправляем данные на сервер методом flush()
        }
    }

    private String passwordEncryptionMD5(String password) {
        String encryptedPassword = null;
        try {
            /* MessageDigest instance for MD5. */
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            /* Add plain-text password bytes to digest using MD5 update() method. */
            messageDigest.update(password.getBytes());

            /* Convert the hash value into bytes */
            byte[] bytes = messageDigest.digest();

            /* The bytes array has bytes in decimal form. Converting it into hexadecimal format. */
            StringBuilder stringBuilder = new StringBuilder();
            for (byte aByte : bytes) {
                stringBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }

            /* Complete hashed password in hexadecimal format */
            encryptedPassword = stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Incorrect algorithm name -- " + e);
        }
        return encryptedPassword;
    }
}
