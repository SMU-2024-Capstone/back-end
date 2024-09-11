CREATE TABLE place AS
SELECT *
FROM CSVREAD('places_db.csv', NULL, 'charset=UTF-8');
