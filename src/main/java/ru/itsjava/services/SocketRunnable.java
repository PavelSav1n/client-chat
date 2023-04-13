package ru.itsjava.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.Socket;

// Класс необходимый для чтения сообщений с сервера
@Getter
@RequiredArgsConstructor
public class SocketRunnable implements Runnable {

    private static final Logger log = Logger.getLogger(SocketRunnable.class);
    private final Socket socket;
    private final ClientService clientService;
    private int authFlag;

    @SneakyThrows
    @Override
    public void run() {
        // BufferedReader выделим в отдельный сервис
        MessageInputService serverReader = new MessageInputServiceImpl(socket.getInputStream());
        authFlag = 0;
        String messageFromServer;

        while (true) {
            messageFromServer = serverReader.getMessage();

            if (messageFromServer.equals("!exit!")) {
                log.info(messageFromServer);
                clientService.setAuthFlag(0);
            } else if (messageFromServer.equals("!auth failed!")) {
                System.out.println("**** Авторизация не удалась ****");
                System.out.println("**** имя пользователя или пароль не верны ****");
                log.info(messageFromServer);
                clientService.authorization();
            } else if (messageFromServer.equals("!reg failed!")) {
                System.out.println("**** Регистрация не удалась ****");
                System.out.println("**** попробуйте ввести другое имя ****");
                log.info(messageFromServer);
                clientService.registration();
            } else if (messageFromServer.equals("!auth success!")) {
                System.out.println("**** Авторизация прошла успешно ****");
                log.info(messageFromServer);
                clientService.setAuthFlag(1);
            } else if (messageFromServer.equals("!reg success!")) {
                System.out.println("**** Регистрация прошла успешно ****");
                log.info(messageFromServer);
                clientService.setAuthFlag(1);
            } else
                // !saveMsg!number!messages
                if (messageFromServer.startsWith("!saveSTART!")) {
                    System.out.println("_________ ENTERED SAVE FILE MODULE ______________");
                    String msgCount = (messageFromServer.substring(11).split("!", 2)[0]);
                    System.out.println("Message count = " + msgCount);
                    String chatMsg = messageFromServer.substring(11).split("!", 2)[1];
                    String destination = clientService.getSaveFileDestination();
                    System.out.println("Destination = '" + destination + "'");
                    File file = new File(destination);
                    try (PrintWriter printWriter = new PrintWriter(file)) {
                        printWriter.println(chatMsg);
                        while (!(messageFromServer = serverReader.getMessage()).startsWith("!saveEND!")) {
                            printWriter.println(messageFromServer);
                        }
                        log.info("Saving " + msgCount + " messages to " + destination);
                    } catch (FileNotFoundException e) {
                        System.out.println("There is no file in the folder. Please insert filename in destination line.");
                        log.error("There is no file in the folder. Please insert filename in destination line.");
                    }
                    System.out.println("_________ EXITING SAVE FILE MODULE ______________");
                } else {
                    System.out.println(messageFromServer); // вывод сообщения от сервера в консоль
                }
        }
    }
}

