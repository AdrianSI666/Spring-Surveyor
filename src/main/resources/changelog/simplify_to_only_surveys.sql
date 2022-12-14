--liquibase formatted sql
--changeset adrian:4

-- Deleting table role and it's usage
drop table surveyor.role CASCADE;
alter table surveyor."user" DROP COLUMN role;

-- Deleting tags witch will be useful later for more statistics and re-usability
drop table surveyor.question_tags CASCADE;
drop table surveyor.survey_tags CASCADE;
drop table surveyor.tag CASCADE;

-- Deleting answer and question table which was meant to have re-usability when working with quizzes and even surveys
drop table surveyor.answer CASCADE;
drop table surveyor.question CASCADE;

-- Redesigning survey_questions to fill deleted question table
drop table surveyor.survey_questions CASCADE;
CREATE TABLE surveyor.survey_questions(
    id BIGSERIAL PRIMARY KEY,
    name character varying(30) NOT NULL,
    duration TIME NOT NULL,
    content text,
    survey_id BIGINT NOT NULL references surveyor.survey(id)
);

-- Changing answers foreign key
TRUNCATE surveyor.survey_answer;
alter table surveyor.survey_answer DROP COLUMN question_id;
alter table surveyor.survey_answer DROP COLUMN survey_id;
alter table surveyor.survey_answer ADD question_id BIGINT NOT NULL references surveyor.survey_questions(id);
alter table surveyor.survey_answer add constraint survey_answer_pks PRIMARY KEY (question_id, participant_id);

-- Testing data
insert into surveyor.survey_questions
values (default, 'testQuestion', '00:01:30' , 'testContent', 1);

insert into surveyor.survey_answer
values (1, 'testAnswer', 1);