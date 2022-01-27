package ru.itsjava.services;

import lombok.SneakyThrows;

// наблюдатель
public interface Observer {
    void notifyMe(String message); // уведомлять, рассылать сообщение самому себе
}
