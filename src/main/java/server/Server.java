package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        // Пункт 7: Сокеты. Открываем порт 12345
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("--- Сервер Expense Tracker запущен ---");
            System.out.println("Ожидание подключений...");

            while (true) {
                Socket socket = serverSocket.accept(); // Ждем клиента
                System.out.println("Клиент подключен: " + socket.getInetAddress());

                // Пункт 6: Потоки. Для каждого клиента — новый поток ClientHandler
                Thread thread = new Thread(new ClientHandler(socket));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
        }
    }
}