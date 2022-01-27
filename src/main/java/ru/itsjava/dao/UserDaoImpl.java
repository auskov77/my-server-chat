package ru.itsjava.dao;

import lombok.AllArgsConstructor;

import lombok.SneakyThrows;
import ru.itsjava.domain.User;
import ru.itsjava.domain.UserNotFoundException;
import ru.itsjava.utils.Props;

import java.sql.*;

@AllArgsConstructor
public class UserDaoImpl implements UserDao {
    private final Props props; // используем наши свойства из Props

    @SneakyThrows
    @Override
    public User findByNameAndPassword(String name, String password) {
        // создаем Connection по данным файла applicaion.properties
        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password")
        )) {
            // создали connection, создаем statement -> запрос к БД
            PreparedStatement preparedStatement = connection.
                    prepareStatement("select count(*) cnt from schema_online_course.users where name = ? and password = ?");

            // у prepareStatement проставляем параметры: 1-й - name, 2-й - password
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);

            // далее у preparedStatement получить executeQuery и соответственно получить resultSet
            ResultSet resultSet = preparedStatement.executeQuery();
            // у resultSet выбрать next
            resultSet.next();

            // взять resultSet -> getInt и передать сюда название колонки
            // это у нас будет переменная userCount
            int userCount = resultSet.getInt("cnt");

            // проверяем значение userCount
            if (userCount == 1) {
                // если верно возвращаем пользователя
                return new User(name, password);
            }

        } catch (UserNotFoundException userNotFoundException){
            System.out.println("Пользователь с таким именем и паролем не найден в БД!");
        }
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
//        // если userCount не равен 1, то кидаем ошибку
//        throw new UserNotFoundException("Пользователь с таким именем и паролем не найден в БД!");
        return null;
    }

    // метод по созданию нового пользователя
    @SneakyThrows
    @Override
    public User createNewUser(String newName, String newPassword) {
        // создаем Connection по данным файла applicaion.properties
        try (Connection connection = DriverManager.getConnection(
                props.getValue("db.url"),
                props.getValue("db.login"),
                props.getValue("db.password")
        )) {
            // создали connection, создаем statement -> запрос к БД
            PreparedStatement preparedStatement = connection.
                    prepareStatement("insert into schema_online_course.users(name, password) values (?, ?)");

            // у prepareStatement проставляем параметры: 1-й - name, 2-й - password
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newPassword);

            preparedStatement.executeUpdate();

            return new User(newName, newPassword);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        // если userCount не равен 0, то кидаем ошибку
        throw new RuntimeException("Вы вели что-то не то!");
    }
}
