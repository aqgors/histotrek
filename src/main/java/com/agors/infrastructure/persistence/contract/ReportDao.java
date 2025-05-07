package com.agors.infrastructure.persistence.contract;

import com.agors.domain.entity.Report;
import java.util.List;

public interface ReportDao {
    Report add(Report report);
    List<Report> findAll();
    Report findById(int id);
    void remove(int id);
}
