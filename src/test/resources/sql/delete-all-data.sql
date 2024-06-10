delete from `users`;
delete from `posts`;
ALTER TABLE `users` ALTER COLUMN id RESTART WITH 1;
ALTER TABLE `posts` ALTER COLUMN id RESTART WITH 1;
