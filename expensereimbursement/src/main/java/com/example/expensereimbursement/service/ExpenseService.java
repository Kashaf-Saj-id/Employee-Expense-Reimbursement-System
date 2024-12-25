package com.example.expensereimbursement.service;

import com.example.expensereimbursement.model.*;
import com.example.expensereimbursement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExpenseStatusRepository expenseStatusRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    // Get all roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Get all employees
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Get all expense statuses
    public List<ExpenseStatus> getAllExpenseStatuses() {
        return expenseStatusRepository.findAll();
    }

    // Get all expenses (approved, rejected, pending)
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    // Get only pending expenses
    public List<Expense> getPendingExpenses() {
        Optional<ExpenseStatus> pendingStatus = expenseStatusRepository.findById(1); // Assuming "Pending" has ID 1
        if (pendingStatus.isEmpty()) {
            return List.of();
        }
        return expenseRepository.findByStatus(pendingStatus.get());
    }

    // Add expense with validations
    // Add expense with validations
    public String addExpense(Expense expense) {
        // Validate employee
        Optional<Employee> optionalEmployee = employeeRepository.findById(expense.getEmployee().getId());
        if (optionalEmployee.isEmpty()) {
            return "Error: No employee with this ID exists.";
        }

        Employee employee = optionalEmployee.get();

        // Validate employee role
        Role role = employee.getRole();
        if (!role.isStatus()) {
            return "Error: Employee's role is not supported by the company.";
        }

        // Validate category
        Optional<Category> optionalCategory = categoryRepository.findById(expense.getCategory().getId());
        if (optionalCategory.isEmpty()) {
            return "Error: Invalid category ID.";
        }

        Category category = optionalCategory.get();
        if (!category.isStatus()) {
            return "Error: This expense category is not supported by the company.";
        }

        // Set expense status to "Pending"
        Optional<ExpenseStatus> pendingStatus = expenseStatusRepository.findById(1); // Assuming "Pending" has ID 1
        if (pendingStatus.isEmpty()) {
            return "Error: Could not set expense status to pending.";
        }

        expense.setStatus(pendingStatus.get());
        expense.setSubmitDate(LocalDateTime.now());
        expense.setApprovalDate(null); // Approval date not set initially

        // Save expense
        expenseRepository.save(expense);
        return "Expense submitted successfully!";
    }


    // Method to update expense status by manager
    public String updateExpenseStatus(int expenseId, int statusId) {
        // Validate expense existence
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        if (optionalExpense.isEmpty()) {
            return "Error: Expense not found.";
        }

        Expense expense = optionalExpense.get();

        // Validate status ID (only 2 = Approved, 3 = Rejected allowed)
        if (statusId != 2 && statusId != 3) {
            return "Error: Invalid status ID. Only 'Approved' (2) or 'Rejected' (3) are allowed.";
        }

        Optional<ExpenseStatus> optionalStatus = expenseStatusRepository.findById(statusId);
        if (optionalStatus.isEmpty()) {
            return "Error: Status not found.";
        }

        ExpenseStatus status = optionalStatus.get();

        // Update the expense status
        expense.setStatus(status);

        // Set approval date for 'Approved' (2) or 'Rejected' (3)
        if (statusId == 2 || statusId == 3) {
            expense.setApprovalDate(LocalDateTime.now());
        }

        // Save the updated expense
        expenseRepository.save(expense);
        return "Expense status updated successfully!";
    }

    // New method to get expenses by employee ID and date range
    public List<Expense> getExpensesByEmployeeAndDateRange(int employeeId, LocalDate startDate, LocalDate endDate) {
        // Convert startDate and endDate to LocalDateTime with proper time boundaries
        LocalDateTime startDateTime = startDate.atStartOfDay(); // start at 12 AM
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // end at 11:59:59 PM

        // Retrieve the employee
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isEmpty()) {
            return List.of(); // Return empty list if employee not found
        }

        // Get the expenses for the given employee within the date range
        return expenseRepository.findByEmployeeAndSubmitDateBetween(employee.get(), startDateTime, endDateTime);
    }

    // New method to fetch expenses by status
    public List<Expense> getExpensesByStatus(String statusName) {
        // Find status by name
        Optional<ExpenseStatus> optionalStatus = expenseStatusRepository
                .findAll()
                .stream()
                .filter(status -> status.getName().equalsIgnoreCase(statusName))
                .findFirst();

        if (optionalStatus.isEmpty()) {
            throw new IllegalArgumentException("Error: Invalid status name provided.");
        }

        // Fetch and return expenses with the given status
        return expenseRepository.findByStatus(optionalStatus.get());
    }
}