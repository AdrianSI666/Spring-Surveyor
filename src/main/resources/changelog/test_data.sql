--liquibase formatted sql
--changeset adrian:2

insert into surveyor.role
values (default, 'testing');

insert into surveyor."user"
values (default, 'name', 'surname', 1);

insert into surveyor.tag
values (default, 'test');

insert into surveyor.survey
values (default, 'testSurv', 'testDescription', 1);

insert into surveyor.question
values (default, 'testQuestion', 'testContent');

insert into surveyor.survey_questions
values (1,1);

insert into surveyor.survey_tags
values (1,1);

insert into surveyor.question_tags
values (1,1);

insert into surveyor.answer
values (default, 'testAnswer', 10, 1);

insert into surveyor.survey_participants
values (default, 'testNick', 1);

insert into surveyor.survey_answer
values (1, 1, 1, 'participant test answer');