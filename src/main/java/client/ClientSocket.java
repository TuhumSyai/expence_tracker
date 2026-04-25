package client;

import models.DataPackage;
import models.User;

import java.io.*;
import java.net.Socket;

public class ClientSocket {
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static User currentUser;

    public static void connect() {
        try {
            socket = new Socket("localhost", 12345);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Подключено к серверу!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Отправка запроса и получение ответа
    public static DataPackage sendRequest(DataPackage request) {
        try {
            out.writeObject(request);
            return (DataPackage) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}