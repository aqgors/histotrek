package com.agors.application.admin;

import com.agors.domain.entity.Report;
import com.agors.domain.enums.ReportType;
import com.agors.infrastructure.persistence.impl.PlaceDaoImpl;
import com.agors.infrastructure.persistence.impl.ReportDaoImpl;
import com.agors.infrastructure.persistence.impl.UserDaoImpl;
import com.agors.infrastructure.util.I18n;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Вкладка керування звітами адміністратора.
 * <p>
 * Надає можливості для генерації звітів, перегляду створених звітів,
 * їх збереження у форматах TXT та DOCX, друку та видалення.
 * </p>
 * Генерація звіту включає інформацію про користувачів і історичні місця.
 *
 * @author agors
 * @version 1.0
 */
public class ReportManagementTab extends VBox {

    /** DAO для роботи з таблицею звітів. */
    private final ReportDaoImpl reportDao = new ReportDaoImpl();
    /** Контейнер для списку звітів. */
    private final VBox reportList = new VBox(10);

    /**
     * Конструктор ініціалізує інтерфейс вкладки:
     * - кнопка генерації звіту,
     * - прокручуваний список існуючих звітів.
     */
    public ReportManagementTab() {
        getStylesheets().add(getClass().getResource("/style/report-tab.css").toExternalForm());

        setPadding(new Insets(20));
        setSpacing(15);

        Button generateBtn = new Button(I18n.get("generate_report_btn", "📝 Згенерувати звіт"));
        generateBtn.getStyleClass().addAll("button", "button-generate");
        generateBtn.setOnAction(e -> generateReport());

        reportList.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(reportList);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");

        getChildren().addAll(generateBtn, scrollPane);
        loadReports();
    }

    /**
     * Генерує новий звіт, включаючи інформацію про всіх користувачів і всі історичні місця.
     * Звіт зберігається до бази даних як {@link ReportType#TEXT_EXPORT}.
     */
    private void generateReport() {
        StringBuilder content = new StringBuilder();

        String timestamp = LocalDateTime.now().toString();
        content.append(I18n.get("report_header", "==== Звіт станом на "))
            .append(timestamp)
            .append(" ====\n\n");

        content.append(I18n.get("users_section", "▶ КОРИСТУВАЧІ:\n"));
        content.append("--------------------------------------------------\n");
        new UserDaoImpl().getAllUsers().forEach(u ->
            content.append("• ").append(u.getUsername())
                .append("\n   Email: ").append(u.getEmail())
                .append("\n   ").append(I18n.get("role_label", "Роль: ")).append(u.getRole()).append("\n\n")
        );

        content.append(I18n.get("places_section", "▶ ІСТОРИЧНІ МІСЦЯ:\n"));
        content.append("--------------------------------------------------\n");
        new PlaceDaoImpl().findAll().forEach(p ->
            content.append("• ").append(p.getName())
                .append("\n   ").append(I18n.get("country_label", "Країна: ")).append(p.getCountry())
                .append("\n   ").append(I18n.get("era_label", "Епоха: ")).append(p.getEra()).append("\n\n")
        );

        Report report = new Report();
        report.setGeneratedAt(LocalDateTime.now());
        report.setType(ReportType.TEXT_EXPORT);
        report.setContent(content.toString());

        reportDao.add(report);
        loadReports();
    }

    /**
     * Завантажує всі наявні звіти з бази даних та відображає їх у вигляді розгорнутих панелей.
     * Для кожного звіту доступні кнопки збереження, друку та видалення.
     */
    private void loadReports() {
        reportList.getChildren().clear();
        List<Report> reports = reportDao.findAll();

        for (Report r : reports) {
            TitledPane pane = new TitledPane();
            pane.setText(I18n.get("report_title", "Звіт") + " #" + r.getId() + " (" + r.getGeneratedAt() + ")");
            pane.getStyleClass().add("titled-pane");

            TextArea area = new TextArea(r.getContent());
            area.setWrapText(true);
            area.setEditable(false);
            area.getStyleClass().add("text-area");

            Button saveTxtBtn = new Button(I18n.get("save_txt_btn", "💾 TXT"));
            saveTxtBtn.getStyleClass().addAll("button", "button-save");
            saveTxtBtn.setOnAction(e -> saveAsTxt(r));

            Button saveDocxBtn = new Button(I18n.get("save_docx_btn", "📃 DOCX"));
            saveDocxBtn.getStyleClass().addAll("button", "button-docx");
            saveDocxBtn.setOnAction(e -> saveAsDocx(r));

            Button printBtn = new Button(I18n.get("print_btn", "🖨 Друк"));
            printBtn.getStyleClass().addAll("button", "button-print");
            printBtn.setOnAction(e -> printReport(r));

            Button deleteBtn = new Button(I18n.get("delete_btn_report", "🗑 Видалити"));
            deleteBtn.getStyleClass().addAll("button", "button-delete");
            deleteBtn.setOnAction(e -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle(I18n.get("confirm_delete_title", "Підтвердження видалення"));
                confirm.setHeaderText(I18n.get("confirm_delete_header", "Видалити цей звіт?"));
                confirm.setContentText(I18n.get("confirm_delete_content", "Цю дію не можна скасувати."));
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
            actions.getStyleClass().add("hbox");

            VBox box = new VBox(area, actions);
            pane.setContent(box);

            reportList.getChildren().add(pane);
        }
    }

    /**
     * Зберігає вибраний звіт у форматі текстового файлу (.txt).
     *
     * @param report звіт для збереження
     */
    private void saveAsTxt(Report report) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18n.get("save_txt_title", "Зберегти звіт як TXT"));
        fileChooser.setInitialFileName("report_" + report.getId() + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(getScene().getWindow());

        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(report.getContent().getBytes());
            } catch (IOException e) {
                showError(I18n.get("error_saving_txt_title", "Помилка збереження TXT"), e.getMessage());
            }
        }
    }

    /**
     * Зберігає вибраний звіт у форматі DOCX за допомогою Apache POI.
     *
     * @param report звіт для експорту у формат Word (.docx)
     */
    private void saveAsDocx(Report report) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18n.get("save_docx_title", "Зберегти як DOCX"));
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
                showError(I18n.get("error_saving_docx_title", "Помилка збереження DOCX"), e.getMessage());
            }
        }
    }

    /**
     * Відправляє звіт на друк через стандартну систему друку JavaFX.
     *
     * @param report звіт, який потрібно надрукувати
     */
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
                showError(I18n.get("error_print_title", "Помилка друку"), I18n.get("error_print_msg", "Не вдалося роздрукувати звіт."));
            }
        }
    }

    /**
     * Відображає повідомлення про помилку у вигляді діалогового вікна.
     *
     * @param title заголовок повідомлення
     * @param msg   текст повідомлення
     */
    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
