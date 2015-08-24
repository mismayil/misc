-- DATABASE INIT

-- create new database
CREATE DATABASE task;

USE task;

-- create tables
CREATE TABLE options(option_id INT PRIMARY KEY, option_val VARCHAR(255));
CREATE TABLE items(item_id INT PRIMARY KEY, item_val VARCHAR(255));
CREATE TABLE cart(cart_id INT PRIMARY KEY, option_val VARCHAR(255), item_val VARCHAR(255));

-- pre-populate options
INSERT INTO options VALUES(1, "option1");
INSERT INTO options VALUES(2, "option2");
INSERT INTO options VALUES(3, "option3");
INSERT INTO options VALUES(4, "option4");
INSERT INTO options VALUES(5, "option5");
INSERT INTO options VALUES(6, "option6");
INSERT INTO options VALUES(7, "option7");
INSERT INTO options VALUES(8, "option8");
INSERT INTO options VALUES(9, "option9");
INSERT INTO options VALUES(10, "option10");

-- pre-populate items
INSERT INTO items VALUES(1, "item1");
INSERT INTO items VALUES(2, "item2");
INSERT INTO items VALUES(3, "item3");
INSERT INTO items VALUES(4, "item4");
INSERT INTO items VALUES(5, "item5");
INSERT INTO items VALUES(6, "item6");
INSERT INTO items VALUES(7, "item7");
INSERT INTO items VALUES(8, "item8");
INSERT INTO items VALUES(9, "item9");
INSERT INTO items VALUES(10, "item10");