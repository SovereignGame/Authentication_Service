DROP TABLE IF EXISTS Account;
 
create table Account
(
    username varchar(255) not null
        primary key,
    password varchar(255) null,
    salt     varchar(255) null,
    alias    varchar(255) null
);
 
--INSERT INTO Account (username, last_name, career) VALUES
  --('Aliko', 'Dangote', 'Billionaire Industrialist'),
  --('Bill', 'Gates', 'Billionaire Tech Entrepreneur'),
 -- ('Folrunsho', 'Alakija', 'Billionaire Oil Magnate');