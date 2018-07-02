truncate table configuration;
ALTER SEQUENCE configuration_id_seq RESTART WITH 1;
insert into configuration (key_for_value, value) values ('database.salt', '8f6yeFkStCGfad5JOhBp/jAJVZS93mcuY6jYe4KQ8K0=');
--local host: insert into configuration (key_for_value, value) values ('database.salt', 'X1rMAJi7cIPIDFKQsAqI2gzi0tdvPhoiXKxAmU7lvOg=');
insert into configuration (key_for_value, value) values ('source.directory', 'P2DnW7q4rjmwMz0Q+fr/MQ==');--test, dev server:
insert into configuration (key_for_value, value) values ('target.directory', 'c7Mnvgu2h2G52AGhfrPVZ4DGQoYHvd7scfnaep0kUW7taiUlgqMYm1CZ16S7n/60');
insert into configuration (key_for_value, value) values ('target.domain', 'GbKIFTa4Ssx9+/YV7+gZ1g==');
insert into configuration (key_for_value, value) values ('target.auth.username', 'xW3y7jpMAP7ZegyqljxV0A==');
insert into configuration (key_for_value, value) values ('target.auth.password', 'PMiPCDVdO7uRiafvtR08bQ==');