package com.example.expensereimbursement.repository;

import com.example.expensereimbursement.model.Employee;
import com.example.expensereimbursement.model.Expense;
import com.example.expensereimbursement.model.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    // Find expenses by status
    List<Expense> findByStatus(ExpenseStatus status);

    // Find expenses by employee and date range
    List<Expense> findByEmployeeAndSubmitDateBetween(Employee employee, LocalDateTime startDate, LocalDateTime endDate);

    // Custom query method to find expenses by employee
    List<Expense> findByEmployee(Employee employee);
}