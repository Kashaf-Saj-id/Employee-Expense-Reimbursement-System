package com.example.expensereimbursement.service;

import com.example.expensereimbursement.model.*;
import com.example.expensereimbursement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExpenseService {

    // Repositories injected via Spring's Dependency Injection
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

    @Autowired
    private CategoryPackageRepository categoryPackageRepository;

    @Autowired
    private RoleCategoryPackageRepository roleCategoryPackageRepository;

    /**
     * Fetches all roles from the database.
     * @return List of all roles
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Fetches all employees from the database.
     * @return List of all employees
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Fetches all categories from the database.
     * @return List of all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Fetches all expense statuses from the database.
     * @return List of all expense statuses
     */
    public List<ExpenseStatus> getAllExpenseStatuses() {
        return expenseStatusRepository.findAll();
    }

    /**
     * Fetches all expenses with a "Pending" status (assuming ID 1 is for "Pending").
     * @return List of expenses with "Pending" status
     */
    public List<Expense> getAllExpenses() {
        Optional<ExpenseStatus> pendingStatus = expenseStatusRepository.findById(1);
        if (pendingStatus.isEmpty()) {
            return List.of(); // Return empty list if "Pending" status not found
        }
        return expenseRepository.findByStatus(pendingStatus.get());
    }

    /**
     * Adds a new expense after performing validations for employee, role, and category.
     * @param expense The expense object to be added
     * @return A string message indicating success or error
     */
    public String addExpense(Expense expense) {
        // Validate employee
        Optional<Employee> optionalEmployee = employeeRepository.findById(expense.getEmployee().getId());
        if (optionalEmployee.isEmpty()) {
            return "Error: No employee with this ID exists.";
        }

        Employee employee = optionalEmployee.get();

        // Validate employee's role
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
        Optional<ExpenseStatus> pendingStatus = expenseStatusRepository.findById(1);
        if (pendingStatus.isEmpty()) {
            return "Error: Could not set expense status to pending.";
        }

        expense.setStatus(pendingStatus.get());
        expense.setSubmitDate(LocalDateTime.now());
        expense.setApprovalDate(null); // Approval date not set initially

        // Save expense to repository
        expenseRepository.save(expense);
        return "Expense submitted successfully!";
    }

    /**
     * Updates the status of an existing expense (by a manager).
     * @param expenseId The ID of the expense to update
     * @param statusId The new status ID (2 for Approved, 3 for Rejected)
     * @return A string message indicating success or error
     */
    public String updateExpenseStatus(int expenseId, int statusId) {
        // Validate the existence of the expense
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        if (optionalExpense.isEmpty()) {
            return "Error: Expense not found.";
        }

        Expense expense = optionalExpense.get();

        // Validate the provided status ID (Only 2 or 3 are allowed)
        if (statusId != 2 && statusId != 3) {
            return "Error: Invalid status ID. Only 'Approved' (2) or 'Rejected' (3) are allowed.";
        }

        // Fetch the new status
        Optional<ExpenseStatus> optionalStatus = expenseStatusRepository.findById(statusId);
        if (optionalStatus.isEmpty()) {
            return "Error: Status not found.";
        }

        ExpenseStatus status = optionalStatus.get();

        // Update expense status
        expense.setStatus(status);

        // Set approval date if status is "Approved" or "Rejected"
        if (statusId == 2 || statusId == 3) {
            expense.setApprovalDate(LocalDateTime.now());
        }

        // Save the updated expense
        expenseRepository.save(expense);
        return "Expense status updated successfully!";
    }

    /**
     * Retrieves expenses for a specific employee within a given date range.
     * @param employeeId The ID of the employee
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of expenses for the employee within the specified date range
     */
    public List<Expense> getExpensesByEmployeeAndDateRange(int employeeId, LocalDate startDate, LocalDate endDate) {
        // Convert LocalDate to LocalDateTime to define time boundaries
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 12 AM
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 11:59 PM

        // Fetch the employee
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isEmpty()) {
            return List.of(); // Return an empty list if employee is not found
        }

        // Retrieve the expenses for the employee within the date range
        return expenseRepository.findByEmployeeAndSubmitDateBetween(employee.get(), startDateTime, endDateTime);
    }

    /**
     * Retrieves expenses by status and category name. If no category is provided, returns all expenses for the status.
     * @param statusId The status ID to filter expenses
     * @param categoryName The category name to filter expenses, or null to return all categories
     * @return List of expenses filtered by status and category
     */
    public List<Expense> getExpensesByStatusAndCategory(int statusId, String categoryName) {
        // Fetch the status by ID
        Optional<ExpenseStatus> optionalStatus = expenseStatusRepository.findById(statusId);
        if (optionalStatus.isEmpty()) {
            throw new IllegalArgumentException("Error: Invalid status ID provided.");
        }

        ExpenseStatus status = optionalStatus.get();

        // If no categoryName is provided, return all expenses for the given status, sorted by submitDate descending
        if (categoryName == null || categoryName.isEmpty()) {
            return expenseRepository.findByStatusOrderBySubmitDateDesc(status);
        }

        // Find the category by name using CategoryRepository
        Category category = categoryRepository.findByName(categoryName);
        if (category == null) {
            throw new IllegalArgumentException("Error: Category not found with the name: " + categoryName);
        }

        // Fetch and return expenses with the given status and category, sorted by submitDate descending
        return expenseRepository.findByStatusAndCategoryOrderBySubmitDateDesc(status, category);
    }

    /**
     * Fetches all category packages from the database.
     * @return List of all category packages
     */
    public List<CategoryPackage> getAllCategoryPackages() {
        return categoryPackageRepository.findAll();
    }

    /**
     * Retrieves all RoleCategoryPackage entities.
     * @return List of all RoleCategoryPackage entities
     */
    public List<RoleCategoryPackage> getAllRoleCategoryPackages() {
        List<RoleCategoryPackage> roleCategoryPackages = roleCategoryPackageRepository.findAll();

        // Log the fetched data for debugging purposes
        System.out.println("Fetched RoleCategoryPackage data: " + roleCategoryPackages);

        return roleCategoryPackages;
    }

    /**
     * Validates an expense based on the role, category package, and the expense amount.
     * @param request The ExpenseValidationRequest containing the validation details
     * @return true if the expense is valid, false otherwise
     */
    public boolean validateExpense(ExpenseValidationRequest request) {
        // Fetch the role by roleId
        Optional<Role> optionalRole = roleRepository.findById(request.getRoleId().intValue());
        if (optionalRole.isEmpty()) {
            throw new IllegalArgumentException("Role not found for ID: " + request.getRoleId());
        }

        // Fetch the category package by categoryPackageId
        Optional<CategoryPackage> optionalCategoryPackage = categoryPackageRepository.findById(request.getCategoryPackageId().intValue());
        if (optionalCategoryPackage.isEmpty()) {
            throw new IllegalArgumentException("Category Package not found for ID: " + request.getCategoryPackageId());
        }

        CategoryPackage categoryPackage = optionalCategoryPackage.get();

        // Check if the role is associated with the category package
        Optional<RoleCategoryPackage> roleCategoryPackage = roleCategoryPackageRepository
                .findAll()
                .stream()
                .filter(rcp -> rcp.getRole().getId() == request.getRoleId().intValue() &&
                        rcp.getCategoryPackage().getId() == request.getCategoryPackageId().intValue())
                .findFirst();

        if (roleCategoryPackage.isEmpty()) {
            throw new IllegalArgumentException("Role is not associated with this Category Package.");
        }

        // Check if the expense amount is within the limit
        return request.getExpenseAmount() <= categoryPackage.getExpenseLimit();
    }

    /**
     * Fetches the expense history for an employee categorized by expense type and limit.
     * @param employeeId The ID of the employee
     * @return A map containing expense details categorized by type, with remaining limits
     */
    public Map<String, Object> getEmployeeExpenseHistoryByCategory(int employeeId) {
        // Fetch employee by ID
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("Employee not found.");
        }

        Employee employee = employeeOpt.get();

        // Fetch the role of the employee
        Role role = employee.getRole();
        if (role == null || !role.isStatus()) {
            throw new IllegalArgumentException("Employee role is not active.");
        }

        // Find category packages associated with the employee's role
        List<RoleCategoryPackage> roleCategoryPackages = roleCategoryPackageRepository.findAll();
        Map<Integer, CategoryPackage> categoryPackageMap = new HashMap<>();
        for (RoleCategoryPackage rcp : roleCategoryPackages) {
            if (rcp.getRole().getId() == role.getId()) {
                categoryPackageMap.put(rcp.getCategoryPackage().getCategory().getId(), rcp.getCategoryPackage());
            }
        }

        // Fetch all expenses of the employee (including "Pending" expenses)
        List<Expense> expenses = expenseRepository.findByEmployee(employee);
        Map<String, Integer> categoryTotalExpenses = new HashMap<>();

        // Loop through expenses and sum them by category
        for (Expense expense : expenses) {
            String categoryName = expense.getCategory().getName();

            // Add expenses to category totals, including "Pending" expenses
            categoryTotalExpenses.put(categoryName, categoryTotalExpenses.getOrDefault(categoryName, 0) + expense.getAmount());
        }

        // Build the result response
        Map<String, Object> result = new HashMap<>();
        result.put("employeeName", employee.getName());
        result.put("role", role.getName());

        // Prepare category-wise details
        Map<String, Object> categoryDetails = new HashMap<>();
        for (Map.Entry<String, Integer> entry : categoryTotalExpenses.entrySet()) {
            String categoryName = entry.getKey();
            Integer totalSpent = entry.getValue();

            // Find category package for this category
            Category category = categoryRepository.findByName(categoryName);
            if (category != null) {
                CategoryPackage categoryPackage = categoryPackageMap.get(category.getId());
                if (categoryPackage != null) {
                    int expenseLimit = categoryPackage.getExpenseLimit();
                    int remainingAmount = expenseLimit - totalSpent;

                    // Prepare category data and check if limit is exceeded
                    Map<String, Object> categoryData = new HashMap<>();
                    categoryData.put("expenseUsed", totalSpent);
                    categoryData.put("remainingLimit", remainingAmount);

                    // Add warning if the amount exceeds the limit
                    if (remainingAmount < 0) {
                        categoryData.put("warning", "You have exceeded the limit by " + Math.abs(remainingAmount) + ".");
                    } else {
                        categoryData.put("warning", "Remaining limit: " + remainingAmount);
                    }

                    categoryDetails.put(categoryName, categoryData);
                }
            }
        }

        result.put("categoryDetails", categoryDetails);
        return result;
    }

}
