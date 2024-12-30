CREATE DATABASE expense_reimbursement_system;

USE expense_reimbursement_system;


CREATE TABLE role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    status BIT
);
 
 
CREATE TABLE employee (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    email VARCHAR(50),
    role_id INT,
    FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    status BIT
);

CREATE TABLE expense_status (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    status BIT
);

CREATE TABLE expense (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    amount INT,
    description VARCHAR(500),
    category_id INT,
    status_id INT,
    submit_date DATETIME,
    approval_date DATETIME,
    FOREIGN KEY (employee_id) REFERENCES employee(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (status_id) REFERENCES expense_status(id)
);

-- Insert roles (1-true, 0-False)
INSERT INTO role (name, status) VALUES ('Intern', 0),('Associate Software Engineer', 1),('Senior Software Engineer', 1),('Technical Lead', 1),('Team-Manager', 1);

-- Insert employees
INSERT INTO employee (name, email, role_id) VALUES ('Kashaf', 'kashaf.sajid@gmail.com', 1), 
                                                    ('Aqdas Manzoor', 'aqdasmanzoor@gmail.com', 1),
                                                     ('Maham Iqbal', 'maham123@gmail.com', 2),
                                                     ('Farid-ul-Hassan', 'farid.hassan@gmail.com', 3),
                                                     ('Awais', 'awais789@gmail.com', 4),
                                                     ('Almas Saleem', 'almas.saleem@gmail.com', 5);
-- Insert categories
INSERT INTO categories (name, status) VALUES ('Fuel Allowance', 0), ('Medical coverage', 1), ('Education allowances', 1);

-- Insert expense statuses
INSERT INTO expense_status (name, status) VALUES ('Pending', 1), ('Approved', 1), ('Rejected', 1);


CREATE TABLE category_package (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT,
    package_name VARCHAR(50),
    expense_limit INT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);


CREATE TABLE role_category_package (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT,
    category_package_id INT,
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (category_package_id) REFERENCES category_package(id)
);

-- Inserting CategoryPackage 
INSERT INTO category_package (category_id, package_name, expense_limit) VALUES
(1, 'Silver', 10000),
(1, 'Gold', 20000),
(1, 'Platinum', 30000),
(2, 'Silver', 15000),
(2, 'Gold', 25000),
(2, 'Platinum', 40000),
(3, 'Silver', 10000),
(3, 'Gold', 25000),
(3, 'Platinum', 50000);

-- Insert data into RoleCategoryPackage table
INSERT INTO role_category_package (role_id, category_package_id)
VALUES
(2, 1),  -- role_id 2, category_package_id 1
(2, 4),  -- role_id 2, category_package_id 4
(2, 7),  -- role_id 2, category_package_id 7
(3, 2),  -- role_id 3, category_package_id 2
(3, 5),  -- role_id 3, category_package_id 5
(3, 8),  -- role_id 3, category_package_id 8
(4, 2),  -- role_id 4, category_package_id 2
(4, 6),  -- role_id 4, category_package_id 6
(4, 8),  -- role_id 4, category_package_id 8
(5, 3),  -- role_id 5, category_package_id 3
(5, 6),  -- role_id 5, category_package_id 6
(5, 9);  -- role_id 5, category_package_id 9


-- Verify the data
SELECT * FROM role;
SELECT * FROM employee;
SELECT * FROM categories;
SELECT * FROM expense_status;
SELECT * FROM expense;	
SELECT * FROM category_package;
SELECT * FROM role_category_package;											
show tables;



