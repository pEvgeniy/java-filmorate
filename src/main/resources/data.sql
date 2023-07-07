INSERT INTO mpa_type(mpa_id, name)
SELECT *
FROM (VALUES  (1, 'G'),
              (2, 'PG'),
              (3, 'PG-13'),
              (4, 'R'),
              (5, 'NC-17')) AS data(mpa_id, name)
WHERE NOT EXISTS (SELECT 1 FROM mpa_type WHERE mpa_id = data.mpa_id);

INSERT INTO genres(genre_id, name)
SELECT *
FROM (VALUES  (1,'Комедия'),
              (2, 'Драма'),
              (3, 'Мультфильм'),
              (4, 'Триллер'),
              (5, 'Документальный'),
              (6, 'Боевик')) AS data(genre_id, name)
WHERE NOT EXISTS (SELECT 1 FROM genres WHERE genre_id = data.genre_id);