SET FOREIGN_KEY_CHECKS = 0;

/*CREATE SEQUENCE users_seq START WITH 5;
CREATE SEQUENCE accounts_seq START WITH 5;
CREATE SEQUENCE user_relationships_seq START WITH 7;
CREATE SEQUENCE transactions_seq START WITH 4;*/

INSERT INTO users (username, email, password)
VALUES
    ('Mike', 'paladin@email.com', 'MyGirlfriendFHasSuper-Powers'),
    ('Dustin', 'dustybun@email.com', 'Never Ending Story'),
    ('Lucas', 'ranger@email.com', 'My little sister is the Boss'),
    ('Will', 'willthewise@email.com', 'I have an unmentionable secret :(');

INSERT INTO accounts (balance, user_id)
VALUES
    (90.00, 1),
    (88.50, 2),
    (122.50, 3),
    (15.00, 4);

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