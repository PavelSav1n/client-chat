package ru.itsjava.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.net.Socket;

// Класс необходимый для чтения сообщений с сервера
@RequiredArgsConstructor
public class SocketRunnable implements Runnable {

    private final Socket socket;

    @SneakyThrows
    @Override
    public void run() {
        // BufferedReader выделим в отдельный сервис
        MessageInputService serverReader = new MessageInputServiceImpl(socket.getInputStream());

        String messageFromServer = null;

        while (true) {
            messageFromServer = serverReader.getMessage();
            if (messageFromServer.equals("!exit!")) {
                break;
            }
            System.out.println(messageFromServer); // и вызываем метод, читающий входящий поток строк
        }
    }
}
