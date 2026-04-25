package client.controllers;

import client.ClientSocket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.DataPackage;
import models.Expense;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MainController {

    @FXML private PieChart expensePieChart;
    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, String> colCategory;
    @FXML private TableColumn<Expense, String> colDescription;
    @FXML private TableColumn<Expense, Double> colAmount;
    @FXML private TableColumn<Expense, String> colDate;
    @FXML private Button btnAddExpense;    // Добавь эту строку

    @FXML
    void initialize() {
        // Привязываем колонки к полям класса Expense
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        loadData();
    }

    public void loadData() {
        DataPackage request = new DataPackage("GET_ALL_EXPENSES");
        request.setUser(ClientSocket.currentUser);

        DataPackage response = ClientSocket.sendRequest(request);

        if (response != null && response.getExpenses() != null) {
            ObservableList<Expense> observableList = FXCollections.observableArrayList(response.getExpenses());
            expenseTable.setItems(observableList);
            updatePieChart(observableList);
        }
    }

    private void updatePieChart(ObservableList<Expense> expenses) {
        Map<String, Double> categoryMap = new HashMap<>();
        for (Expense e : expenses) {
            categoryMap.put(e.getCategory(), categoryMap.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryMap.forEach((category, sum) -> {
            pieChartData.add(new PieChart.Data(category + " (" + sum + ")", sum));
        });

        expensePieChart.setData(pieChartData);
    }

    @FXML
    void openAddWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_expense.fxml"));
            Parent root = loader.load();

            // Передаем ссылку на текущий контроллер, чтобы обновить таблицу после добавления
            AddExpenseController controller = loader.getController();
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Блокирует основное окно
            stage.setTitle("Добавить новую трату");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        // 1. Очищаем данные о текущем пользователе
        ClientSocket.currentUser = null;

        try {
            // 2. Возвращаемся на окно логина
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnAddExpense.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Expense Tracker - Авторизация");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteExpense() {
        // 1. Берем выбранную строку из таблицы
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("Выберите строку для удаления!");
            return;
        }

        // 2. Отправляем запрос на удаление
        DataPackage request = new DataPackage("DELETE_EXPENSE");
        request.setExpense(selected);

        DataPackage response = ClientSocket.sendRequest(request);

        if (response != null && "SUCCESS".equals(response.getOperation())) {
            // 3. Обновляем данные на экране
            loadData();
        }
    }

    @FXML
    void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить отчет");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(btnAddExpense.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Расходы");

                // Создаем заголовок
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Категория");
                header.createCell(1).setCellValue("Описание");
                header.createCell(2).setCellValue("Сумма");
                header.createCell(3).setCellValue("Дата");

                // Заполняем данными из таблицы
                ObservableList<Expense> items = expenseTable.getItems();
                for (int i = 0; i < items.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    row.createCell(0).setCellValue(items.get(i).getCategory());
                    row.createCell(1).setCellValue(items.get(i).getDescription());
                    row.createCell(2).setCellValue(items.get(i).getAmount());
                    row.createCell(3).setCellValue(items.get(i).getDate().toString());
                }

                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                System.out.println("Данные успешно экспортированы!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showOpenDialog(btnAddExpense.getScene().getWindow());

        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheetAt(0);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    // --- ВОТ СЮДА ВСТАВЛЯЕМ ПРОВЕРКУ ---
                    double amount = 0;
                    org.apache.poi.ss.usermodel.Cell amountCell = row.getCell(2); // Колонка с суммой

                    if (amountCell != null) {
                        switch (amountCell.getCellType()) {
                            case NUMERIC:
                                amount = amountCell.getNumericCellValue();
                                break;
                            case STRING:
                                try {
                                    // Если число записано как текст "500.5", переводим в double
                                    amount = Double.parseDouble(amountCell.getStringCellValue());
                                } catch (NumberFormatException e) {
                                    System.out.println("Ошибка в строке " + i + ": неверный формат числа");
                                    continue; // Пропускаем эту строку и идем к следующей
                                }
                                break;
                            default:
                                continue; // Если ячейка пустая или другого типа
                        }
                    }
                    // ------------------------------------

                    Expense expense = new Expense(
                            ClientSocket.currentUser.getId(),
                            amount, // Используем проверенную переменную
                            row.getCell(0).getStringCellValue(),  // Категория
                            row.getCell(1).getStringCellValue(),  // Описание
                            LocalDate.now()
                    );

                    DataPackage request = new DataPackage("ADD_EXPENSE");
                    request.setExpense(expense);
                    ClientSocket.sendRequest(request);
                }
                loadData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}