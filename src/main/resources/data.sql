INSERT INTO authors (name) VALUES ('Фёдор Достоевский'), ('Лев Толстой');
INSERT INTO genres (name) VALUES ('Роман'), ('Детектив');
INSERT INTO books (title, author_id, genre_id) VALUES
  ('Преступление и наказание', 1, 1),
  ('Анна Каренина', 2, 1);