package com.example.expensereimbursement.model;

import jakarta.persistence.*;

@Entity
public class RoleCategoryPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "category_package_id")
    private CategoryPackage categoryPackage;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public CategoryPackage getCategoryPackage() {
        return categoryPackage;
    }

    public void setCategoryPackage(CategoryPackage categoryPackage) {
        this.categoryPackage = categoryPackage;
    }
}
