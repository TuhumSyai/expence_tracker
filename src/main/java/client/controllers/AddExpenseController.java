package client.controllers;

import client.ClientSocket;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.DataPackage;
import models.Expense;

import java.time.LocalDate;

public class AddExpenseController {

    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private Label errorLabel;


    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void initialize() {
        // Наполняем ComboBox категориями
        categoryBox.setItems(FXCollections.observableArrayList(
                "Еда", "Транспорт", "Жилье", "Развлечения", "Здоровье", "Прочее"
        ));

        // ЗАЩИТА: разрешаем только цифры и одну точку
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                amountField.setText(oldValue); // Возвращаем как было, если ввели букву
            }
        });
    }

    @FXML
    void handleAdd() {
        try {
            String category = categoryBox.getValue();
            String amountText = amountField.getText();
            String desc = descriptionField.getText();

            if (amountText.isEmpty() || category == null) {
                errorLabel.setText("Заполните все поля!");
                errorLabel.setVisible(true);
                System.out.println("Заполните сумму и категорию!");
                return;
            }

            double amount = Double.parseDouble(amountText);

            Expense newExpense = new Expense(
                    ClientSocket.currentUser.getId(),
                    amount, category, desc, LocalDate.now()
            );

            DataPackage request = new DataPackage("ADD_EXPENSE");
            request.setExpense(newExpense);

            DataPackage response = ClientSocket.sendRequest(request);

            if (response != null && "SUCCESS".equals(response.getOperation())) {
                mainController.loadData(); // Обновляем таблицу в главном окне
                ((Stage) amountField.getScene().getWindow()).close(); // Закрываем окно добавления
            }
        } catch (NumberFormatException e) {
            System.out.println("Некорректный формат суммы");        }
    }
}