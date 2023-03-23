package ru.itsjava;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {

    public final static int PORT = 8081;
    public final static String HOST = "localhost";


    public static void main(String[] args) throws IOException {

        Socket socket = new Socket(HOST, PORT);

        if (socket.isConnected()) {

            // Для того, чтобы отправить текстовое сообщение на сервер, необходимо создать объект для записи в поток.
            // Чтобы создать поток, искользуем метод getOutputStream() на нашем созданном socket
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream());
            // Пишем текст:
            serverWriter.println("Hi from Client");
            // Чтобы данные не буфферизовались, отправляем данные на сервер методом flush()
            serverWriter.flush();
        }


    }
}
