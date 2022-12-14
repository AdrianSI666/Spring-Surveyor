--liquibase formatted sql
--changeset milosz:12

ALTER TABLE surveyor.survey ALTER COLUMN passcode DROP NOT NULL;

ALTER TABLE surveyor.survey ALTER COLUMN passcode DROP DEFAULT;

