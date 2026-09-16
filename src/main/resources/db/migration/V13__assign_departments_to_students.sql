UPDATE student
SET department_id = (
    SELECT id FROM department
    WHERE name = 'Computer Science'
)
WHERE email = 'john1@gmail.com';

UPDATE student
SET department_id = (
    SELECT id FROM department
    WHERE name = 'Information Tech'
)
WHERE email = 'alex12@gmail.com';

UPDATE student
SET department_id = (
    SELECT id FROM department
    WHERE name = 'Electronics'
)
WHERE email = 'akka206@gmail.com';

UPDATE student
SET department_id = (
    SELECT id FROM department
    WHERE name = 'Mechanical'
)
WHERE email = 'ashakumari12@gmail.com';