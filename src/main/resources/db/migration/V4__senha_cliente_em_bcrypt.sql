-- V4__senha_cliente_em_bcrypt.sql
-- Migra a senha da cliente de teste para BCrypt (Aula 14)
-- Hash gerado por BCryptPasswordEncoder().encode("123456") — a senha continua "123456"
UPDATE clientes
SET senha_hash = '$2a$10$UhCG3virfi0A1g2YLIdOEeV3a9BoSr4OqZz2eFoBMwVTRrjtqydXS'
WHERE email = 'maria@email.com';
