UPDATE student
SET phone_number = '9000000002'
WHERE id = 2 AND phone_number IS NULL;

UPDATE student
SET phone_number = '9000000004'
WHERE id = 4 AND phone_number IS NULL;

UPDATE student
SET phone_number = '9000000005'
WHERE id = 5 AND phone_number IS NULL;