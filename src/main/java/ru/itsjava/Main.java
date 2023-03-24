package ru.itsjava;

import ru.itsjava.services.ClientService;
import ru.itsjava.services.ClientServiceImpl;

public class Main {

    public static void main(String[] args) {

        ClientService clientService = new ClientServiceImpl();
        clientService.start();
    }
}
