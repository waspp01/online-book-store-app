INSERT into users(id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'user1@i.ua', '123456', 'test', 'test', 'test Address1', false);
INSERT into users(id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (2, 'user2@i.ua', '123456', 'test', 'test', 'test Address2', false);
INSERT INTO shopping_carts (user_id)
VALUES (1);
INSERT INTO shopping_carts (user_id)
VALUES (2);