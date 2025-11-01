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