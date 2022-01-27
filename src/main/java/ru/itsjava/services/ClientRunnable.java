package ru.itsjava.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.itsjava.dao.UserDao;
import ru.itsjava.domain.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@RequiredArgsConstructor
// запуск нового пользователя - за это отвечает ClientRunnable
// это сущность клиента
public class ClientRunnable implements Runnable, Observer {
    private final Socket socket;
    private final ServerService serverService;
    private User user; // это наш пользователь
    private final UserDao userDao; // подключили сюда и еще проинициализировали
    private String messageFromClient;


    @SneakyThrows
    @Override
    public void run() {
        System.out.println("Client connected");
        // чтобы считывать сообщение с клиента есть BufferedReader
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // чтобы считать что-то с клиента берем InputStream

        // сообщение от клиента
//        String messageFromClient;

        // проверка на авторизацию
        if (authorization(bufferedReader)) {
            serverService.addObserver(this);

            // отсылаем сообщение клиенту
            notifyMe("Можете начинать общение!");

            // будем считывать с помощью цикла while - писать бесконечно
            // начинаем цикл, где проверяем сообщение от клиента
            while ((messageFromClient = bufferedReader.readLine()) != null) {
                System.out.println(user.getName() + ":" + messageFromClient);
                // с сервера отправляем сообщение всем
//                serverService.notifyObserver(user.getName() + ":" + messageFromClient);
                // от клиента отправляем сообщение всем кроме себя
                serverService.notifyObserverExceptMe(user.getName() + ":" + messageFromClient, this);
            }
        } else if (registration(bufferedReader)) {
            serverService.addObserver(this);

            // отсылаем сообщение клиенту
            notifyMe("Можете начинать общение!");

            // будем считывать с помощью цикла while - писать бесконечно
            // начинаем цикл, где проверяем сообщение от клиента
            while ((messageFromClient = bufferedReader.readLine()) != null) {
                System.out.println(user.getName() + ":" + messageFromClient);
                // с сервера отправляем сообщение всем
//                serverService.notifyObserver(user.getName() + ":" + messageFromClient);
                // от клиента отправляем сообщение всем кроме себя
                serverService.notifyObserverExceptMe(user.getName() + ":" + messageFromClient, this);
            }
        }
    }

    // создаем метод авторизации
    @SneakyThrows
    private boolean authorization(BufferedReader bufferedReader) {
        // сообщение от клиента
//        String authorizationMessage;
        // считываем authorizationMessage с помощью bufferedReader.readLine
        while (true) {
            // !autho!login:password
            // делаем проверку
            if ((messageFromClient = bufferedReader.readLine()) != null && messageFromClient.startsWith("!autho!")) { // если authorizationMessage начинается с !autho!, подставляем login и password
                String login = messageFromClient.substring(7).split(":")[0]; // substring - выделили подстроку, далее разбиваем строку по : методом split
                String password = messageFromClient.substring(7).split(":")[1]; // substring - выделили подстроку, далее разбиваем строку по : методом split

                // должны у userDao вызывать соответствующий метод
                // новому пользователю присваиваем логин и пассворд
                user = userDao.findByNameAndPassword(login, password);
                if (user != null){
                    notifyMe("Вы авторизованы!");
                } else {
                    notifyMe("Вы не авторизованы: имя или пароль не совпадают!");
                    continue;
                }

                return true; // все в порядке
            }
            return false; // если false, то применить метод регистрации
        }
    }

    // создаем метод регистрации
    @SneakyThrows
    private boolean registration(BufferedReader bufferedReader) {
        // сообщение от клиента
//        String registrationMessage;
        // считываем registrationMessage с помощью bufferedReader.readLine
        while (true) {
            // !reg!login:password
            // делаем проверку
            if (messageFromClient.startsWith("!reg!")) { // если registrationMessage начинается с !reg!, подставляем login и password
                String login = messageFromClient.substring(5).split(":")[0]; // substring - выделили подстроку, далее разбиваем строку по : методом split
                String password = messageFromClient.substring(5).split(":")[1]; // substring - выделили подстроку, далее разбиваем строку по : методом split

                // должны у userDao вызывать соответствующий метод
                // новому пользователю присваиваем логин и пассворд
                user = userDao.createNewUser(login, password);
                notifyMe("Вы зарегистрированы!");
                return true; // все в порядке

            }
            return false; // если false, то применить метод регистрации
        }
    }

    @SneakyThrows
    @Override
    // метод notifyMe пишет конкретному Oserver'y, т.е. пишет сообшение клиенту
    public void notifyMe(String message) {
        // отправка сообщения с сервера клиенту c помощью PrintWriter'a
        PrintWriter clientWriter = new PrintWriter(socket.getOutputStream()); // OutputStream - отдаем
        clientWriter.println(message); // message - это наше сообщение
        clientWriter.flush();
    }
}