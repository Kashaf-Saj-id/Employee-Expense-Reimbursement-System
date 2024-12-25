package com.example.expensereimbursement.repository;

import com.example.expensereimbursement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}
