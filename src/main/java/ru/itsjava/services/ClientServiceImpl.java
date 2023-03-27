package ru.itsjava.services;

import lombok.SneakyThrows;

import java.io.PrintWriter;
import java.net.Socket;

public class ClientServiceImpl implements ClientService {

    public final static int PORT = 8081;
    public final static String HOST = "localhost";

    @SneakyThrows
    @Override
    public void start() {

        Socket socket = new Socket(HOST, PORT);
        if (socket.isConnected()) {
            // Подсоединились и сразу запускаем прослушивание сокета:
            // Создаём новый поток, пихаем в него наш класс для чтения потока (String) с сервера.
            new Thread(new SocketRunnable(socket)).start();

            // Для того чтобы отправить текстовое сообщение на сервер, необходимо создать исходящий поток.
            // Чтобы создать поток, используем метод getOutputStream() на нашем созданном socket
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream());

            // Необходимо, чтобы клиент мог сначала писать сообщение в консоль, а потом отправлять его
            // Используем наш написанный MessageInputService с BufferedReader внутри для этой задачи:
            MessageInputService messageInputService = new MessageInputServiceImpl(System.in);

            // Сначала авторизируем пользователя:
            System.out.print("Введите логин: ");
            String login = messageInputService.getMessage();
            System.out.print("Введите пароль: ");
            String password = messageInputService.getMessage();
            // Отправляем данные на сервер:
            // !autho!login:password
            serverWriter.println("!autho!" + login + ":" + password);
            serverWriter.flush();

            while (true) {
                String consoleMessage = messageInputService.getMessage();
                // Выход из чата:
                if (consoleMessage.equals("exit")) {
                    System.out.println("Выход из цикла...");
                    break;
                }
                // Пишем текст:
                serverWriter.println(consoleMessage);
                // Чтобы данные не буфферизовались, отправляем данные на сервер методом flush()
                serverWriter.flush();
            }
            System.out.println("Закрываем соединение...");
            socket.close();
        }
    }
}
