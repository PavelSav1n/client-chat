package ru.itsjava.services;

public interface ClientService {

    void start();

    void authorization();

    void registration();

    void writeMessage();

    void setAuthFlag(int authFlag);

    void setSaveFileDestination(String directory);

    String getSaveFileDestination();
}
