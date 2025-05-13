package com.agors.infrastructure.persistence.contract;

import com.agors.domain.entity.Report;
import java.util.List;

/**
 * Контракт для роботи із звітами в базі даних.
 * <p>
 * Оголошує методи для додавання, отримання та видалення об'єктів Report.
 * </p>
 *
 * @author agors
 * @version 1.0
 */
public interface ReportDao {

    /**
     * Додає новий звіт до бази даних.
     *
     * @param report об'єкт Report з даними для збереження
     * @return збережений об'єкт Report з встановленим id
     */
    Report add(Report report);

    /**
     * Повертає список усіх доступних звітів.
     *
     * @return список об'єктів Report
     */
    List<Report> findAll();

    /**
     * Знаходить звіт за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор звіту
     * @return знайдений об'єкт Report або null, якщо не знайдено
     */
    Report findById(int id);

    /**
     * Видаляє звіт за його унікальним ідентифікатором.
     *
     * @param id унікальний ідентифікатор звіту
     */
    void remove(int id);
}
