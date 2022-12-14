--liquibase formatted sql
--changeset adrian:13

CREATE TABLE surveyor.answer (
    id bigint NOT NULL,
    answer text NOT NULL,
    value integer,
    quesiton_id bigint NOT NULL
);


ALTER TABLE surveyor.answer OWNER TO postgres;

CREATE SEQUENCE surveyor.answer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE surveyor.answer_id_seq OWNER TO postgres;

ALTER SEQUENCE surveyor.answer_id_seq OWNED BY surveyor.answer.id;

ALTER TABLE ONLY surveyor.answer ALTER COLUMN id SET DEFAULT nextval('surveyor.answer_id_seq'::regclass);

ALTER TABLE ONLY surveyor.answer
    ADD CONSTRAINT answer_pkey PRIMARY KEY (id);

ALTER TABLE ONLY surveyor.answer
    ADD CONSTRAINT question_id FOREIGN KEY (quesiton_id) REFERENCES surveyor.survey_questions(id);

ALTER TABLE surveyor.survey_answer ALTER COLUMN answer DROP NOT NULL;
ALTER TABLE surveyor.survey_answer ADD COLUMN answer_id bigint REFERENCES surveyor.answer;