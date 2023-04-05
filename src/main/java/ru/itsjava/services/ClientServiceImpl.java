package ru.itsjava.services;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
//            SocketRunnable socketRunnable = new SocketRunnable(socket);
//            Thread thread = new Thread();
//            thread.start();


            // Для того чтобы отправить текстовое сообщение на сервер, необходимо создать исходящий поток.
            // Чтобы создать поток, используем метод getOutputStream() на нашем созданном socket
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream());

            // Необходимо, чтобы клиент мог сначала писать сообщение в консоль, а потом отправлять его
            // Используем наш написанный MessageInputService с BufferedReader внутри для этой задачи:
            MessageInputService messageInputService = new MessageInputServiceImpl(System.in);

            // Считываем данные от сервера:
//            Thread thread = new Thread(new SocketRunnable(socket));
//            thread.start();
            BufferedReader messageFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String msgFromSrv = null;
            // Меню
            while (true) {

                System.out.println("messageFromServer = " + messageFromServer);


                System.out.println("**** CHAT ****");
                System.out.println(" 1 -- Войти в чат");
                System.out.println(" 2 -- Новый пользователь");
                System.out.println(" exit -- Выйти из чата");
                String menuNum = messageInputService.getMessage();

                // Авторизация пользователя:
                if (menuNum.equals("1")) {

                    System.out.println("**** Авторизация ****");
                    System.out.print("Введите логин: ");
                    String login = messageInputService.getMessage();
                    System.out.print("Введите пароль: ");
                    String password = messageInputService.getMessage();

                    // Отправляем данные на сервер:
                    // !autho!login:password
                    serverWriter.println("!autho!" + login + ":" + password);
                    serverWriter.flush();

                    while ((msgFromSrv = messageFromServer.readLine()).equals("!auth failed!")) {
                        System.out.println("**** Авторизация не удалась ****");
                        System.out.print("Введите логин: ");
                        login = messageInputService.getMessage();
                        System.out.print("Введите пароль: ");
                        password = messageInputService.getMessage();
                        serverWriter.println("!autho!" + login + ":" + password);
                        serverWriter.flush();
                    }

                    if (msgFromSrv.equals("!auth success!")) {
                        System.out.println("**** Авторизация прошла успешно ****");

                        // !!! по идее тут мы должны были бы погасить созданный ранее BufferedReader, но метод close() кидает ошибку
                        // messageFromServer.close();

                        // Создаём нить, которая будет непрерывно слушать сервер
                        Thread thread = new Thread(new SocketRunnable(socket));
                        thread.start();

                        System.out.println("**** Напишите сообщение ****");

                        // Цикл для отправки сообщения пользователем:
                        while (true) {
                            String consoleMessage = messageInputService.getMessage(); // считываем сообщение из консоли
                            // Выход из чата:
                            if (consoleMessage.equals("!exit")) {
                                serverWriter.println(consoleMessage);
                                serverWriter.flush();
                                System.out.println("Выход из чата в меню...");
                                // Тут мы должны погасить thread, и использовать наш BufferedReader для авторизации.
                                // хз как погасить Thread, судя по отладке в следующий раз просто создаётся новая thread
                                // (в SocketRunnable мы выходим из бесконечного цикла по сообщению от сервера)
                                System.out.println("thread = " + thread); // отладка
                                thread.interrupt();
                                thread = null;
                                break;
                            }
                            // Пишем текст для отправки на сервер:
                            serverWriter.println(consoleMessage);
                            // Чтобы данные не буфферизировались, отправляем данные на сервер методом flush()
                            serverWriter.flush();
                        }
                    }
                }
                // Регистрация пользователя:
                if (menuNum.equals("2")) {
                    System.out.println("**** Регистрация нового пользователя ****");
                    System.out.print("Введите логин: ");
                    String login = messageInputService.getMessage();
                    System.out.print("Введите пароль: ");
                    String password = messageInputService.getMessage();
                    // Отправляем данные на сервер:
                    // !reg!login:password
                    serverWriter.println("!reg!" + login + ":" + password);
                    serverWriter.flush();

                    while ((msgFromSrv = messageFromServer.readLine()).equals("!reg failed!")) {
                        System.out.println("**** Регистрация не удалась ****");
                        System.out.println("**** попробуйте ввести другое имя ****");
                        System.out.print("Введите логин: ");
                        login = messageInputService.getMessage();
                        System.out.print("Введите пароль: ");
                        password = messageInputService.getMessage();
                        serverWriter.println("!reg!" + login + ":" + password);
                        serverWriter.flush();

                    }
                    System.out.println("**** Регистрация прошла успешно ****");
                    System.out.println();
                }
                // Выход из чата:
                if (menuNum.equals("!exit")) {
                    System.out.println("Выход из цикла...");
                    break;
                }
            }
            System.out.println("Закрываем соединение...");
            socket.close();
        }
    }
}
