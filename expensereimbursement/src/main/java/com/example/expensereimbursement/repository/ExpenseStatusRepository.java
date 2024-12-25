package com.example.expensereimbursement.repository;

import com.example.expensereimbursement.model.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseStatusRepository extends JpaRepository<ExpenseStatus, Integer> {

}
