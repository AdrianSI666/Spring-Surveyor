--liquibase formatted sql
--changeset adrian:15
ALTER TABLE surveyor.survey_answer
  RENAME COLUMN answer_id TO chosen_answer_id;