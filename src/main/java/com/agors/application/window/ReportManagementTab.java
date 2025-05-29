package com.agors.application.window;

import com.agors.domain.entity.Report;
import com.agors.infrastructure.persistence.impl.PlaceDaoImpl;
import com.agors.infrastructure.persistence.impl.ReportDaoImpl;
import com.agors.infrastructure.persistence.impl.UserDaoImpl;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ReportManagementTab extends VBox {

    private final ReportDaoImpl reportDao = new ReportDaoImpl();
    private final VBox reportList = new VBox(10);

    public ReportManagementTab() {
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("Звіти");
        title.setFont(Font.font("Arial", 18));

        Button generateBtn = new Button("📝 Згенерувати звіт");
        generateBtn.setOnAction(e -> generateReport());

        reportList.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(reportList);
        scrollPane.setFitToWidth(true);

        getChildren().addAll(title, generateBtn, scrollPane);
        loadReports();
    }

    private void generateReport() {
        StringBuilder content = new StringBuilder();

        String timestamp = LocalDateTime.now().toString();
        content.append("==== Звіт станом на ").append(timestamp).append(" ====\n\n");

        content.append("▶ КОРИСТУВАЧІ:\n");
        content.append("--------------------------------------------------\n");
        new UserDaoImpl().getAllUsers().forEach(u ->
            content.append("• ").append(u.getUsername())
                .append("\n   Email: ").append(u.getEmail())
                .append("\n   Роль: ").append(u.getRole()).append("\n\n")
        );

        content.append("▶ ІСТОРИЧНІ МІСЦЯ:\n");
        content.append("--------------------------------------------------\n");
        new PlaceDaoImpl().findAll().forEach(p ->
            content.append("• ").append(p.getName())
                .append("\n   Країна: ").append(p.getCountry())
                .append("\n   Епоха: ").append(p.getEra()).append("\n\n")
        );

        Report report = new Report();
        report.setGeneratedAt(LocalDateTime.now());
        report.setType("TEXT");
        report.setContent(content.toString());

        reportDao.add(report);
        loadReports();
    }

    private void loadReports() {
        reportList.getChildren().clear();
        List<Report> reports = reportDao.findAll();

        for (Report r : reports) {
            TitledPane pane = new TitledPane();
            pane.setText("Звіт #" + r.getId() + " (" + r.getGeneratedAt() + ")");

            TextArea area = new TextArea(r.getContent());
            area.setWrapText(true);
            area.setEditable(false);

            Button saveTxtBtn = new Button("💾 TXT");
            saveTxtBtn.setOnAction(e -> saveAsTxt(r));

            Button saveDocxBtn = new Button("📃 DOCX");
            saveDocxBtn.setOnAction(e -> saveAsDocx(r));

            Button printBtn = new Button("🖨 Друк");
            printBtn.setOnAction(e -> printReport(r));

            Button deleteBtn = new Button("🗑 Видалити");
            deleteBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Підтвердження видалення");
                confirm.setHeaderText("Видалити цей звіт?");
                confirm.setContentText("Цю дію не можна скасувати.");
                confirm.showAndWait().ifPresent(result -> {
                    if (result == ButtonType.OK) {
                        reportDao.remove(r.getId());
                        loadReports();
                    }
                });
            });

            HBox actions = new HBox(10, saveTxtBtn, saveDocxBtn, printBtn, deleteBtn);
            actions.setPadding(new Insets(5));
            actions.setAlignment(Pos.CENTER_RIGHT);

            VBox box = new VBox(area, actions);
            pane.setContent(box);

            reportList.getChildren().add(pane);
        }
    }

    private void saveAsTxt(Report report) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти звіт як TXT");
        fileChooser.setInitialFileName("report_" + report.getId() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(getScene().getWindow());

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(report.getContent().getBytes());
            } catch (IOException e) {
                showError("Помилка збереження TXT", e.getMessage());
            }
        }
    }

    private void saveAsDocx(Report report) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Зберегти як DOCX");
        fileChooser.setInitialFileName("report_" + report.getId() + ".docx");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DOCX файли", "*.docx"));
        File file = fileChooser.showSaveDialog(getScene().getWindow());

        if (file != null) {
            try (XWPFDocument doc = new XWPFDocument();
                FileOutputStream out = new FileOutputStream(file)) {

                String[] lines = report.getContent().split("\n");
                for (String line : lines) {
                    XWPFParagraph para = doc.createParagraph();
                    XWPFRun run = para.createRun();
                    run.setFontSize(12);
                    run.setFontFamily("Times New Roman");
                    run.setText(line);
                }

                doc.write(out);
            } catch (IOException e) {
                showError("Помилка збереження DOCX", e.getMessage());
            }
        }
    }

    private void printReport(Report report) {
        TextArea printArea = new TextArea(report.getContent());
        printArea.setWrapText(true);
        printArea.setEditable(false);

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(getScene().getWindow())) {
            boolean success = job.printPage(printArea);
            if (success) {
                job.endJob();
            } else {
                showError("Помилка друку", "Не вдалося роздрукувати звіт.");
            }
        }
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
