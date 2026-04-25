package server;

import database.DatabaseHandler;
import models.DataPackage;
import models.Expense;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

// Пункт 6: Потоки (реализация Runnable)
public class ClientHandler implements Runnable {
    private Socket socket;
    private DatabaseHandler dbHandler;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.dbHandler = new DatabaseHandler();
    }

    @Override
    public void run() {
        // Пункт 5: Сериализация. Используем ObjectStreams для передачи объектов
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                // Читаем объект запроса
                DataPackage request = (DataPackage) in.readObject();
                DataPackage response = processRequest(request);

                // Отправляем ответ клиенту
                out.writeObject(response);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Клиент завершил сессию.");
        }
    }

    private DataPackage processRequest(DataPackage request) {
        DataPackage response = new DataPackage("RESPONSE");
        try {
            switch (request.getOperation()) {
                case "REGISTER":
                    dbHandler.signUpUser(request.getUser());
                    response.setOperation("SUCCESS");
                    break;
                case "LOGIN":
                    int userId = dbHandler.loginUser(request.getUser());
                    if (userId != -1) {
                        response.setOperation("SUCCESS");
                        // Возвращаем юзера с его ID из базы
                        request.getUser().setId(userId);
                        response.setUser(request.getUser());
                    } else {
                        response.setOperation("INVALID_AUTH");
                    }
                    break;
                case "GET_ALL_EXPENSES":
                    // Вызываем твой новый метод из DatabaseHandler
                    response.setExpenses(dbHandler.getExpenses(request.getUser().getId()));
                    response.setOperation("SUCCESS");
                    break;
                case "ADD_EXPENSE":
                    try {
                        dbHandler.addExpense(request.getExpense());
                        response.setOperation("SUCCESS");
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.setOperation("ERROR");
                    }
                    break;
                case "GET_EXPENSES":
                    int id = request.getUser().getId();
                    // Вызываем метод БД, который тянет траты по ID пользователя
                    ArrayList<Expense> userExpenses = dbHandler.getExpenses(id);
                    response.setExpenses(userExpenses);
                    response.setOperation("SUCCESS");
                    break;
                case "DELETE_EXPENSE":
                    try {
                        // Берем ID из присланного объекта Expense
                        dbHandler.deleteExpense(request.getExpense().getId());
                        response.setOperation("SUCCESS");
                    } catch (Exception e) {
                        e.printStackTrace();
                        response.setOperation("ERROR");
                    }
                    break;
                default:
                    response.setOperation("UNKNOWN_COMMAND");
            }
        } catch (Exception e) {
            response.setOperation("SERVER_ERROR: " + e.getMessage());
        }
        return response;
    }
}