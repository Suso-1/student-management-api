ALTER TABLE student
    ADD CONSTRAINT uk_student_email UNIQUE (email);