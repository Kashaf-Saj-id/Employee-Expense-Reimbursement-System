package com.example.expensereimbursement.controller;

import com.example.expensereimbursement.model.*;
import com.example.expensereimbursement.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    // Inject the ExpenseService to handle the business logic
    @Autowired
    private ExpenseService expenseService;

    // Endpoint to get all roles from the service layer
    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return expenseService.getAllRoles();  // Fetch all roles using the service
    }

    // Endpoint to get all employees
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return expenseService.getAllEmployees();  // Fetch all employees using the service
    }

    // Endpoint to get all categories from the service layer
    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return expenseService.getAllCategories();  // Fetch all categories using the service
    }

    // Endpoint to get all expense statuses from the service layer
    @GetMapping("/expense-statuses")
    public List<ExpenseStatus> getAllExpenseStatuses() {
        return expenseService.getAllExpenseStatuses();  // Fetch all expense statuses using the service
    }

    // Endpoint to get only pending expenses (filtered from all expenses)
    @GetMapping("/expenses")
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();  // Fetch all pending expenses using the service
    }

    // Endpoint to add a new expense
    @PostMapping("/expenses")
    public ResponseEntity<String> addExpense(@RequestBody Expense expense) {
        String result = expenseService.addExpense(expense);  // Call the service to add an expense
        if (result.startsWith("Error:")) {
            return ResponseEntity.badRequest().body(result);  // If there is an error, return a 400 Bad Request with the error message
        }
        return ResponseEntity.ok(result);  // Otherwise, return a 200 OK response with the success message
    }

    // Endpoint to update the status of an existing expense (using PATCH)
    @PatchMapping("/expenses/{expenseId}/status")
    public ResponseEntity<String> updateExpenseStatus(@PathVariable int expenseId, @RequestParam int statusId) {
        String result = expenseService.updateExpenseStatus(expenseId, statusId);  // Call the service to update the status of an expense
        if (result.startsWith("Error:")) {
            return ResponseEntity.badRequest().body(result);  // Return 400 Bad Request with the error message if any
        }
        return ResponseEntity.ok(result);  // Return 200 OK with the success message if successful
    }

    // New endpoint to get expenses by employee ID and a specified date range
    @GetMapping("/expenses/employee/{employeeId}")
    public ResponseEntity<List<Expense>> getExpensesByEmployeeAndDateRange(
            @PathVariable int employeeId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        // Trim any leading/trailing spaces from the date parameters
        startDate = startDate.trim();
        endDate = endDate.trim();

        // Parse the start and end dates to LocalDate objects
        LocalDate start = LocalDate.parse(startDate);  // Convert the start date string to LocalDate
        LocalDate end = LocalDate.parse(endDate);      // Convert the end date string to LocalDate

        // Call the service method to get expenses for the employee within the date range
        List<Expense> expenses = expenseService.getExpensesByEmployeeAndDateRange(employeeId, start, end);

        // If no expenses are found, return a 204 No Content response
        if (expenses.isEmpty()) {
            return ResponseEntity.noContent().build();  // 204 No Content
        }
        return ResponseEntity.ok(expenses);  // Otherwise, return 200 OK with the list of expenses
    }

    @GetMapping("/expenses/history")
    public ResponseEntity<?> getExpenseHistoryByStatusAndCategory(
            @RequestParam int statusId,
            @RequestParam(required = false) String categoryName) {

        try {
            // Call the service to fetch expenses by statusId and categoryName
            List<Expense> expenses = expenseService.getExpensesByStatusAndCategory(statusId, categoryName);

            // If no expenses are found, return a 204 No Content response
            if (expenses.isEmpty()) {
                return ResponseEntity.noContent().build();  // 204 No Content
            }

            return ResponseEntity.ok(expenses);  // Return 200 OK with the list of expenses
        } catch (IllegalArgumentException e) {
            // Return 400 Bad Request if the status ID is invalid or category is not found
            return ResponseEntity.badRequest().body(e.getMessage());  // 400 Bad Request
        }
    }



    // Endpoint to get all category packages available in the system
    @GetMapping("/category-packages")
    public ResponseEntity<List<CategoryPackage>> getAllCategoryPackages() {
        List<CategoryPackage> categoryPackages = expenseService.getAllCategoryPackages();  // Fetch category packages from the service

        // If no category packages are found, return a 204 No Content response
        if (categoryPackages.isEmpty()) {
            return ResponseEntity.noContent().build();  // 204 No Content
        }

        return ResponseEntity.ok(categoryPackages);  // Return 200 OK with the category packages
    }

    // Endpoint to get all role-category-package relationships
    @GetMapping("/role-category-packages")
    public ResponseEntity<List<RoleCategoryPackage>> getAllRoleCategoryPackages() {
        List<RoleCategoryPackage> roleCategoryPackages = expenseService.getAllRoleCategoryPackages();  // Fetch role-category-packages from the service

        // If no role-category-package relationships are found, return a 204 No Content response
        if (roleCategoryPackages.isEmpty()) {
            return ResponseEntity.noContent().build();  // 204 No Content
        }

        return ResponseEntity.ok(roleCategoryPackages);  // Return 200 OK with the role-category-package data
    }

    // Endpoint to validate an expense based on category package and role
    @PostMapping("/expenses/validate")
    public ResponseEntity<Boolean> validateExpense(@RequestBody ExpenseValidationRequest request) {
        try {
            // Call the service to validate the expense
            boolean isValid = expenseService.validateExpense(request);
            return ResponseEntity.ok(isValid);  // Return 200 OK with the validation result
        } catch (IllegalArgumentException e) {
            // Return 400 Bad Request if validation fails
            return ResponseEntity.badRequest().body(false);  // 400 Bad Request
        }
    }

    // New endpoint to fetch an employee's expense history categorized by expense type
    @GetMapping("/employee-history-by-category/{employeeId}")
    public ResponseEntity<Map<String, Object>> getEmployeeExpenseHistoryByCategory(@PathVariable int employeeId) {
        try {
            // Call the service method to process and return the expense history by category
            Map<String, Object> response = expenseService.getEmployeeExpenseHistoryByCategory(employeeId);
            return ResponseEntity.ok(response);  // Return 200 OK with the expense history data
        } catch (IllegalArgumentException e) {
            // Return 400 Bad Request if there is an error (e.g., invalid employee ID)
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));  // 400 Bad Request with the error message
        }
    }

}
