-- authors
INSERT INTO authors (name) VALUES
  ('Фёдор Достоевский'),
  ('Лев Толстой'),
  ('Агата Кристи');

-- genres
INSERT INTO genres (name) VALUES
  ('Роман'),
  ('Детектив');

-- books
INSERT INTO books (title, author_id, genre_id) VALUES
  ('Преступление и наказание', 1, 1),
  ('Анна Каренина', 2, 1),
  ('Убийство в Восточном экспрессе', 3, 2);

-- users (password: 'password' encoded with BCrypt)
INSERT INTO users (username, password, role, enabled) VALUES
  ('admin', '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_ADMIN', true),
  ('user', '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER', true);