--liquibase formatted sql
--changeset adrian:5

alter table surveyor.survey add passcode character varying(8) NOT NULL DEFAULT 'without';

--changeset szymon:6
alter table surveyor.survey add surveyStarted boolean NOT NULL DEFAULT FALSE;

--changeset szymon:7
alter table surveyor.survey rename COLUMN surveyStarted TO survey_started;