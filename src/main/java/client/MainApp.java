package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ClientSocket.connect(); // Твоё подключение

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/login.fxml"));

        // ГЛАВНЫЙ СЕКРЕТ: Убираем цифры 400, 300.
        // Если их убрать, JavaFX автоматически возьмет размеры из твоего FXML (600x400)
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Expense Tracker - Вход");
        stage.setScene(scene);

        // Блокируем возможность растягивать окно (Пункт 8 требований — GUI)
        stage.setResizable(false);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}