package client;

import models.DataPackage;
import models.User;
import java.io.*;
import java.net.Socket;

public class TestClient {
    public static void main(String[] args) {
        // Подключаемся к серверу
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            System.out.println("Подключено к серверу!");

            // 1. Создаем данные пользователя для теста (Твоё ФИО)
            User testUser = new User("igor_dev", "pass123", "Игорь Кобяков");

            // 2. Упаковываем в DataPackage
            DataPackage request = new DataPackage("REGISTER");
            request.setUser(testUser);

            // 3. Отправляем серверу
            out.writeObject(request);
            System.out.println("Запрос на регистрацию отправлен...");

            // 4. Ждем ответ
            DataPackage response = (DataPackage) in.readObject();
            System.out.println("Ответ от сервера: " + response.getOperation());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}