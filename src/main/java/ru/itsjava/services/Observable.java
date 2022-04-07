package ru.itsjava.services;

// это отдельный сервер
public interface Observable {
    //имеет свои методы
    void addObserver(Observer observer); // добавить наблюдателя
    void deleteObserver(Observer observer); //удалить наблюдателя
    void notifyObserver(String message); // уведомить всех наблюдателей - разослать сообщение всем наблюдателям
    void notifyObserverExceptMe(String message, Observer observer); // уведомит всех наблюдателей, кроме себя
    void writeIntoFileMessageFromClient(String message); //создание файла с сообщениями от клиента
}
