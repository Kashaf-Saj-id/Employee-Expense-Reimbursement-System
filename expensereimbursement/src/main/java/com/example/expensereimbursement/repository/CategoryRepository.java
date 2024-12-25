package com.example.expensereimbursement.repository;

import com.example.expensereimbursement.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
