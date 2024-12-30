package com.example.expensereimbursement.model;

public class ExpenseValidationRequest {
    private Long roleId;
    private Long categoryPackageId;
    private Integer expenseAmount;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getCategoryPackageId() {
        return categoryPackageId;
    }

    public void setCategoryPackageId(Long categoryPackageId) {
        this.categoryPackageId = categoryPackageId;
    }

    public Integer getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(Integer expenseAmount) {
        this.expenseAmount = expenseAmount;
    }
}
