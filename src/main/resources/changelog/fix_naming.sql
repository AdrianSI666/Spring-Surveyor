--liquibase formatted sql
--changeset adrian:3

ALTER TABLE surveyor.answer RENAME answer TO content;
