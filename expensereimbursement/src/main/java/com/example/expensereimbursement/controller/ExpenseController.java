package com.example.expensereimbursement.controller;

import com.example.expensereimbursement.model.*;
import com.example.expensereimbursement.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // Get all roles
    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return expenseService.getAllRoles();
    }

    // Get all employees
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return expenseService.getAllEmployees();
    }

    // Get all categories
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return expenseService.getAllCategories();
    }

    // Get all expense statuses
    @GetMapping("/expense-statuses")
    public List<ExpenseStatus> getAllExpenseStatuses() {
        return expenseService.getAllExpenseStatuses();
    }

    // Get all expenses (approved, rejected, pending)
    @GetMapping("/expenses")
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    // Get only pending expenses
    @GetMapping("/expenses/pending")
    public List<Expense> getPendingExpenses() {
        return expenseService.getPendingExpenses();
    }

    // Add expense
    @PostMapping("/expenses")
    public ResponseEntity<String> addExpense(@RequestBody Expense expense) {
        String result = expenseService.addExpense(expense);
        if (result.startsWith("Error:")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    // PATCH endpoint to update expense status
    @PatchMapping("/expenses/{expenseId}/status")
    public ResponseEntity<String> updateExpenseStatus(@PathVariable int expenseId, @RequestParam int statusId) {
        String result = expenseService.updateExpenseStatus(expenseId, statusId);
        if (result.startsWith("Error:")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }


    // New endpoint to get expenses by employee ID and date range
    // New endpoint to get expenses by employee ID and date range
    @GetMapping("/expenses/employee/{employeeId}")
    public ResponseEntity<List<Expense>> getExpensesByEmployeeAndDateRange(
            @PathVariable int employeeId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        // Remove any leading/trailing whitespaces or newline characters from the input strings
        startDate = startDate.trim();
        endDate = endDate.trim();

        // Parse the start and end dates
        LocalDate start = LocalDate.parse(startDate);  // e.g. "2024-12-01"
        LocalDate end = LocalDate.parse(endDate);      // e.g. "2024-12-31"

        // Get the expenses from the service layer
        List<Expense> expenses = expenseService.getExpensesByEmployeeAndDateRange(employeeId, start, end);

        // Return the expenses or a not found message
        if (expenses.isEmpty()) {
            return ResponseEntity.noContent().build();  // Return 204 No Content if no expenses found
        }
        return ResponseEntity.ok(expenses);  // Return 200 OK with the expenses list
    }

    // New endpoint to get expenses by status (history)
    @GetMapping("/expenses/history")
    public ResponseEntity<?> getExpenseHistoryByStatus(@RequestParam String statusName) {
        try {
            List<Expense> expenses = expenseService.getExpensesByStatus(statusName.trim());

            if (expenses.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content if no expenses found
            }

            return ResponseEntity.ok(expenses); // Return 200 OK with the expenses list
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // Return 400 Bad Request for invalid status
        }
    }
}