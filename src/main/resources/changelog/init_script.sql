--liquibase formatted sql
-- changeset milosz:1

-- Database: quizapp


GRANT TEMPORARY, CONNECT ON DATABASE quizapp TO PUBLIC;

GRANT ALL ON DATABASE quizapp TO postgres;


--
-- PostgreSQL database dump
--

-- Dumped from database version 14.5
-- Dumped by pg_dump version 14.4

-- Started on 2022-09-28 13:23:15 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 4 (class 2615 OID 16387)
-- Name: surveyor; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA surveyor;


ALTER SCHEMA surveyor OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 228 (class 1259 OID 16503)
-- Name: answer; Type: TABLE; Schema: surveyor; Owner: postgres
--

CREATE TABLE surveyor.answer (
    id bigint NOT NULL,
    answer text NOT NULL,
    value integer,
    quesiton_id bigint NOT NULL
);


ALTER TABLE surveyor.answer OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 16502)
-- Name: answer_id_seq; Type: SEQUENCE; Schema: surveyor; Owner: postgres
--

CREATE SEQUENCE surveyor.answer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE surveyor.answer_id_seq OWNER TO postgres;

--
-- TOC entry 3671 (class 0 OID 0)
-- Dependencies: 227
-- Name: answer_id_seq; Type: SEQUENCE OWNED BY; Schema: surveyor; Owner: postgres
--

ALTER SEQUENCE surveyor.answer_id_seq OWNED BY surveyor.answer.id;


--
-- TOC entry 217 (class 1259 OID 16416)
-- Name: question; Type: TABLE; Schema: surveyor; Owner: postgres
--

CREATE TABLE surveyor.question (
    id bigint NOT NULL,
    name character varying(30) NOT NULL,
    content text NOT NULL
);


ALTER TABLE surveyor.question OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 16415)
-- Name: question_id_seq; Type: SEQUENCE; Schema: surveyor; Owner: postgres
--

CREATE SEQUENCE surveyor.question_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE surveyor.question_id_seq OWNER TO postgres;

--
-- TOC entry 3672 (class 0 OID 0)
-- Dependencies: 216
-- Name: question_id_seq; Type: SEQUENCE OWNED BY; Schema: surveyor; Owner: postgres
--

ALTER SEQUENCE surveyor.question_id_seq OWNED BY surveyor.question.id;


--
-- TOC entry 218 (class 1259 OID 16424)
-- Name: question_tags; Type: TABLE; Schema: surveyor; Owner: postgres
--

CREATE TABLE surveyor.question_tags (
    tag_id bigint NOT NULL,
    question_id bigint NOT NULL
);


ALTER TABLE surveyor.question_tags OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 16396)
-- Name: role; Type: TABLE; Schema: surveyor; Owner: postgres
--

CREATE TABLE surveyor.role (
    id bigint NOT NULL,
    name character varying(50) NOT NULL
);


ALTER TABLE surveyor.role OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 16395)
-- Name: role_id_seq; Type: SEQUENCE; Schema: surveyor; Owner: postgres
--

CREATE SEQUENCE surveyor.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE surveyor.role_id_seq OWNER TO postgres;

--
-- TOC entry 3673 (class 0 OID 0)
-- Dependencies: 212
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: surveyor; Owner: postgres
--

ALTER SEQUENCE surveyor.role_id_seq OWNED BY surveyor.role.id;


--
-- TOC entry 220 (class 1259 OID 16447)
-- Name: survey; Type: TABLE; Schema: surveyor; Owner: postgres
--

CREATE TABLE surveyor.survey (
    id bigint NOT NULL,
    name character varying(30) NOT NULL,
    description text,
    author bigint NOT NULL
);


ALTER TABLE surveyor.survey OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 16516)
-- Name: survey_answer; Type: TABLE; Schema: surveyor; Owner: postgres
--

CREATE TABLE surveyor.survey_answer (
    survey_id bigint NOT NULL,
    participant_id bigint NOT NULL,
    question_id bigint NOT NULL,
    answer text NOT NULL
);


ALTER TABLE surveyor.survey_answer OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 16491)
-- Name: survey_participants; Type: TABLE; Schema: surveyor; Owner: postgres
--

CREATE TABLE surveyor.survey_participants (
    id bigint NOT NULL,
    nick character varying(50) NOT NULL,
    survey_id bigint NOT NULL
);


ALTER TABLE surveyor.survey_participants OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 16490)
-- Name: survey_participants_id_seq; Type: SEQUENCE; Schema: surveyor; Owner: postgres
--

CREATE SEQUENCE surveyor.survey_participants_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE surveyor.survey_participants_id_seq OWNER TO postgres;

--
-- TOC entry 3674 (class 0 OID 0)
-- Dependencies: 225
-- Name: survey_participants_id_seq; Type: SEQUENCE OWNED BY; Schema: surveyor; Owner: postgres
--

ALTER SEQUENCE surveyor.survey_participants_id_seq OWNED BY surveyor.survey_participants.id;


--
-- TOC entry 219 (class 1259 OID 16446)
-- Name: survey_id_seq; Type: SEQUENCE; Schema: surveyor; Owner: postgres
--

CREATE SEQUENCE surveyor.survey_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE surveyor.survey_id_seq OWNER TO postgres;

--
-- TOC entry 3675 (class 0 OID 0)
-- Dependencies: 219
-- Name: survey_id_seq; Type: SEQUENCE OWNED BY; Schema: surveyor; Owner: postgres
--

ALTER SEQUENCE surveyor.survey_id_seq OWNED BY surveyor.survey.id;


--
-- TOC entry 224 (class 1259 OID 16475)
-- Name: survey_questions; Type: TABLE; Schema: surveyor; Owner: postgres
--

CREATE TABLE surveyor.survey_questions (
    question_id bigint NOT NULL,
    survey_id bigint NOT NULL
);


ALTER TABLE surveyor.survey_questions OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 16473)
-- Name: survey_questions_question_id_seq; Type: SEQUENCE; Schema: surveyor; Owner: postgres
--

CREATE SEQUENCE surveyor.survey_questions_question_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE surveyor.survey_questions_question_id_seq OWNER TO postgres;

--
-- TOC entry 3676 (class 0 OID 0)
-- Dependencies: 222
-- Name: survey_questions_question_id_seq; Type: SEQUENCE OWNED BY; Schema: surveyor; Owner: postgres
--

ALTER SEQUENCE surveyor.survey_questions_question_id_seq OWNED BY surveyor.survey_questions.question_id;


--
-- TOC entry 223 (class 1259 OID 16474)
-- Name: survey_questions_survey_id_seq; Type: SEQUENCE; Schema: surveyor; Owner: postgres
--

CREATE SEQUENCE surveyor.survey_questions_survey_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE surveyor.survey_questions_survey_id_seq OWNER TO postgres;

--
-- TOC entry 3677 (class 0 OID 0)
-- Dependencies: 223
-- Name: survey_questions_survey_id_seq; Type: SEQUENCE OWNED BY; Schema: surveyor; Owner: postgres
--

ALTER SEQUENCE surveyor.survey_questions_survey_id_seq OWNED BY surveyor.survey_questions.survey_id;


--
-- TOC entry 221 (class 1259 OID 16460)
-- Name: survey_tags; Type: TABLE; Schema: surveyor; Owner: postgres
--

CREATE TABLE surveyor.survey_tags (
    survey_id bigint NOT NULL,
    tag_id bigint NOT NULL
);


ALTER TABLE surveyor.survey_tags OWNER TO postgres;

--
-- TOC entry 215 (class 1259 OID 16409)
-- Name: tag; Type: TABLE; Schema: surveyor; Owner: postgres
--

CREATE TABLE surveyor.tag (
    id bigint NOT NULL,
    name character varying(20) NOT NULL
);


ALTER TABLE surveyor.tag OWNER TO postgres;

--
-- TOC entry 214 (class 1259 OID 16408)
-- Name: tag_id_seq; Type: SEQUENCE; Schema: surveyor; Owner: postgres
--

CREATE SEQUENCE surveyor.tag_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE surveyor.tag_id_seq OWNER TO postgres;

--
-- TOC entry 3678 (class 0 OID 0)
-- Dependencies: 214
-- Name: tag_id_seq; Type: SEQUENCE OWNED BY; Schema: surveyor; Owner: postgres
--

ALTER SEQUENCE surveyor.tag_id_seq OWNED BY surveyor.tag.id;


--
-- TOC entry 211 (class 1259 OID 16389)
-- Name: user; Type: TABLE; Schema: surveyor; Owner: postgres
--

CREATE TABLE surveyor."user" (
    id bigint NOT NULL,
    name character varying(30) NOT NULL,
    surname character varying(30) NOT NULL,
    role bigint NOT NULL
);


ALTER TABLE surveyor."user" OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 16388)
-- Name: user_id_seq; Type: SEQUENCE; Schema: surveyor; Owner: postgres
--

CREATE SEQUENCE surveyor.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE surveyor.user_id_seq OWNER TO postgres;

--
-- TOC entry 3679 (class 0 OID 0)
-- Dependencies: 210
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: surveyor; Owner: postgres
--

ALTER SEQUENCE surveyor.user_id_seq OWNED BY surveyor."user".id;


--
-- TOC entry 3488 (class 2604 OID 16506)
-- Name: answer id; Type: DEFAULT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.answer ALTER COLUMN id SET DEFAULT nextval('surveyor.answer_id_seq'::regclass);


--
-- TOC entry 3483 (class 2604 OID 16419)
-- Name: question id; Type: DEFAULT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.question ALTER COLUMN id SET DEFAULT nextval('surveyor.question_id_seq'::regclass);


--
-- TOC entry 3481 (class 2604 OID 16399)
-- Name: role id; Type: DEFAULT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.role ALTER COLUMN id SET DEFAULT nextval('surveyor.role_id_seq'::regclass);


--
-- TOC entry 3484 (class 2604 OID 16450)
-- Name: survey id; Type: DEFAULT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey ALTER COLUMN id SET DEFAULT nextval('surveyor.survey_id_seq'::regclass);


--
-- TOC entry 3487 (class 2604 OID 16494)
-- Name: survey_participants id; Type: DEFAULT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_participants ALTER COLUMN id SET DEFAULT nextval('surveyor.survey_participants_id_seq'::regclass);


--
-- TOC entry 3485 (class 2604 OID 16478)
-- Name: survey_questions question_id; Type: DEFAULT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_questions ALTER COLUMN question_id SET DEFAULT nextval('surveyor.survey_questions_question_id_seq'::regclass);


--
-- TOC entry 3486 (class 2604 OID 16479)
-- Name: survey_questions survey_id; Type: DEFAULT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_questions ALTER COLUMN survey_id SET DEFAULT nextval('surveyor.survey_questions_survey_id_seq'::regclass);


--
-- TOC entry 3482 (class 2604 OID 16412)
-- Name: tag id; Type: DEFAULT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.tag ALTER COLUMN id SET DEFAULT nextval('surveyor.tag_id_seq'::regclass);


--
-- TOC entry 3480 (class 2604 OID 16392)
-- Name: user id; Type: DEFAULT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor."user" ALTER COLUMN id SET DEFAULT nextval('surveyor.user_id_seq'::regclass);


--
-- TOC entry 3511 (class 2606 OID 16510)
-- Name: answer answer_pkey; Type: CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.answer
    ADD CONSTRAINT answer_pkey PRIMARY KEY (id);


--
-- TOC entry 3497 (class 2606 OID 16423)
-- Name: question question_pkey; Type: CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.question
    ADD CONSTRAINT question_pkey PRIMARY KEY (id);


--
-- TOC entry 3501 (class 2606 OID 16537)
-- Name: question_tags question_tags_pkey; Type: CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.question_tags
    ADD CONSTRAINT question_tags_pkey PRIMARY KEY (tag_id, question_id);


--
-- TOC entry 3493 (class 2606 OID 16401)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 3513 (class 2606 OID 16539)
-- Name: survey_answer survey_answer_pkey; Type: CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_answer
    ADD CONSTRAINT survey_answer_pkey PRIMARY KEY (survey_id, participant_id, question_id);


--
-- TOC entry 3509 (class 2606 OID 16496)
-- Name: survey_participants survey_participants_pkey; Type: CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_participants
    ADD CONSTRAINT survey_participants_pkey PRIMARY KEY (id);


--
-- TOC entry 3503 (class 2606 OID 16454)
-- Name: survey survey_pkey; Type: CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey
    ADD CONSTRAINT survey_pkey PRIMARY KEY (id);


--
-- TOC entry 3507 (class 2606 OID 16541)
-- Name: survey_questions survey_questions_pkey; Type: CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_questions
    ADD CONSTRAINT survey_questions_pkey PRIMARY KEY (question_id, survey_id);


--
-- TOC entry 3505 (class 2606 OID 16543)
-- Name: survey_tags survey_tags_pkey; Type: CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_tags
    ADD CONSTRAINT survey_tags_pkey PRIMARY KEY (survey_id, tag_id);


--
-- TOC entry 3495 (class 2606 OID 16414)
-- Name: tag tag_pkey; Type: CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (id);


--
-- TOC entry 3491 (class 2606 OID 16394)
-- Name: user user_pkey; Type: CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- TOC entry 3489 (class 1259 OID 16407)
-- Name: fki_r; Type: INDEX; Schema: surveyor; Owner: postgres
--

CREATE INDEX fki_r ON surveyor."user" USING btree (role);


--
-- TOC entry 3498 (class 1259 OID 16438)
-- Name: fki_t; Type: INDEX; Schema: surveyor; Owner: postgres
--

CREATE INDEX fki_t ON surveyor.question_tags USING btree (question_id);


--
-- TOC entry 3499 (class 1259 OID 16432)
-- Name: fki_tag_id; Type: INDEX; Schema: surveyor; Owner: postgres
--

CREATE INDEX fki_tag_id ON surveyor.question_tags USING btree (tag_id);


--
-- TOC entry 3525 (class 2606 OID 16526)
-- Name: survey_answer participant_id; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_answer
    ADD CONSTRAINT participant_id FOREIGN KEY (participant_id) REFERENCES surveyor.survey_participants(id);


--
-- TOC entry 3516 (class 2606 OID 16433)
-- Name: question_tags question_id; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.question_tags
    ADD CONSTRAINT question_id FOREIGN KEY (question_id) REFERENCES surveyor.question(id);


--
-- TOC entry 3520 (class 2606 OID 16480)
-- Name: survey_questions question_id; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_questions
    ADD CONSTRAINT question_id FOREIGN KEY (question_id) REFERENCES surveyor.question(id);


--
-- TOC entry 3523 (class 2606 OID 16511)
-- Name: answer question_id; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.answer
    ADD CONSTRAINT question_id FOREIGN KEY (quesiton_id) REFERENCES surveyor.question(id);


--
-- TOC entry 3526 (class 2606 OID 16531)
-- Name: survey_answer question_id; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_answer
    ADD CONSTRAINT question_id FOREIGN KEY (question_id) REFERENCES surveyor.question(id);


--
-- TOC entry 3517 (class 2606 OID 16455)
-- Name: survey survey_author; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey
    ADD CONSTRAINT survey_author FOREIGN KEY (author) REFERENCES surveyor."user"(id);


--
-- TOC entry 3518 (class 2606 OID 16463)
-- Name: survey_tags survey_id; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_tags
    ADD CONSTRAINT survey_id FOREIGN KEY (survey_id) REFERENCES surveyor.survey(id);


--
-- TOC entry 3521 (class 2606 OID 16485)
-- Name: survey_questions survey_id; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_questions
    ADD CONSTRAINT survey_id FOREIGN KEY (survey_id) REFERENCES surveyor.survey(id);


--
-- TOC entry 3522 (class 2606 OID 16497)
-- Name: survey_participants survey_id; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_participants
    ADD CONSTRAINT survey_id FOREIGN KEY (survey_id) REFERENCES surveyor.survey(id);


--
-- TOC entry 3524 (class 2606 OID 16521)
-- Name: survey_answer survey_id; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_answer
    ADD CONSTRAINT survey_id FOREIGN KEY (survey_id) REFERENCES surveyor.survey(id);


--
-- TOC entry 3515 (class 2606 OID 16427)
-- Name: question_tags tag_id; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.question_tags
    ADD CONSTRAINT tag_id FOREIGN KEY (tag_id) REFERENCES surveyor.tag(id);


--
-- TOC entry 3519 (class 2606 OID 16468)
-- Name: survey_tags tag_id; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor.survey_tags
    ADD CONSTRAINT tag_id FOREIGN KEY (tag_id) REFERENCES surveyor.tag(id);


--
-- TOC entry 3514 (class 2606 OID 16402)
-- Name: user user_role; Type: FK CONSTRAINT; Schema: surveyor; Owner: postgres
--

ALTER TABLE ONLY surveyor."user"
    ADD CONSTRAINT user_role FOREIGN KEY (role) REFERENCES surveyor.role(id);


-- Completed on 2022-09-28 13:23:15 CEST

--
-- PostgreSQL database dump complete
--

