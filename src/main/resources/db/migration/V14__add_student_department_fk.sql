ALTER TABLE student
ADD CONSTRAINT fk_student_department
FOREIGN KEY (department_id)
REFERENCES department(id);