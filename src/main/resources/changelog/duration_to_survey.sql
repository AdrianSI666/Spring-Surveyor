--liquibase formatted sql


--changeset szymon:8
ALTER TABLE surveyor.survey_questions DROP duration
-- rollback alter table surveyor.survey_questions ADD duration TIME NOT NULL DEFAULT '00:01:00'

--changeset szymon:9
ALTER TABLE surveyor.survey ADD duration TIME NOT NULL DEFAULT '00:01:00'
-- rollback alter table surveyor.survey DROP duration

-- changeset adrian:10
-- ALTER TABLE surveyor.survey ADD time_started TIMESTAMP WITH TIME ZONE
-- rollback alter table surveyor.survey DROP time_started

--changeset adrian:11
ALTER TABLE surveyor.survey ADD time_started TIMESTAMPTZ
-- rollback alter table surveyor.survey DROP time_started