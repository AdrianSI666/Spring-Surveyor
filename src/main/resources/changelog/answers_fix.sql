--liquibase formatted sql
--changeset adrian:14
ALTER TABLE surveyor.answer
  RENAME COLUMN quesiton_id TO question_id;
ALTER TABLE surveyor.answer DROP CONSTRAINT question_id;
ALTER TABLE ONLY surveyor.answer
    ADD CONSTRAINT question_id FOREIGN KEY (question_id) REFERENCES surveyor.survey_questions(id);