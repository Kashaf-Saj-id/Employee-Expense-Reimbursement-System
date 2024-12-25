package com.example.expensereimbursement.repository;

import com.example.expensereimbursement.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
