-- Insert entries into the users table
INSERT INTO users (id, first_name, last_name, birthday, inn, snils, passport_number, login, password)
VALUES
    (gen_random_uuid(), 'Иван', 'Петров', '1990-01-01', '789856132312', '12345678901', '0708567890', 'ivan', 'password1'),
    (gen_random_uuid(), 'Сергей', 'Иванов', '1985-05-15', '789856132313', '98765432109', '0708543210', 'sergey', 'password2'),
    (gen_random_uuid(), 'Дмитрий', 'Сидоров', '1995-07-20', '789856132314', '99887766554', '0708778899', 'dmitriy', 'password3');

-- Insert entries into the users_roles table
INSERT INTO user_roles (user_id, role)
VALUES
    ((SELECT id FROM users WHERE login = 'ivan'), 'USER'),
    ((SELECT id FROM users WHERE login = 'ivan'), 'ADMIN'),
    ((SELECT id FROM users WHERE login = 'sergey'), 'USER'),
    ((SELECT id FROM users WHERE login = 'dmitriy'), 'USER');