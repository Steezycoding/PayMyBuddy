SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO users (username, email, password, role, balance)
VALUES
    ('Admin', 'admin@paymybuddy.com', '$2b$12$Mo5GJMCAU7UdPvBauGNL.uI73Yr.oYwt.teE8WsGoOUWLK85mpaOC', 'ADMIN', 0),
    ('Mike', 'mike@email.com', '$2b$12$ZstSalnRGd5rdZZ7ZCktY.ny5v.qGzo1gzmbSl5HKt61yEKU7bFxm', 'USER', 90.00),
    ('Dustin', 'dustin@email.com', '$2b$12$ZstSalnRGd5rdZZ7ZCktY.ny5v.qGzo1gzmbSl5HKt61yEKU7bFxm', 'USER', 88.50),
    ('Lucas', 'lucas@email.com', '$2b$12$ZstSalnRGd5rdZZ7ZCktY.ny5v.qGzo1gzmbSl5HKt61yEKU7bFxm', 'USER', 122.50),
    ('Will', 'will@email.com', '$2b$12$ZstSalnRGd5rdZZ7ZCktY.ny5v.qGzo1gzmbSl5HKt61yEKU7bFxm', 'USER', 15.00);


INSERT INTO user_relationships (user_id, relation_user_id)
VALUES
    (3,1),
    (1,3),
    (1,2),
    (2,1),
    (2,3),
    (3,2);

INSERT INTO transactions (amount, description, sender, receiver)
VALUES
    (10.00, 'Remb. bonbons', 3, 1),
    (32.50, 'Achat radio occasion', 2, 3),
    (20.00, 'T-shirt Hellfire Club', 1, 2);

SET FOREIGN_KEY_CHECKS = 1;