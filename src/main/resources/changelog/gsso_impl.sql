ALTER TABLE surveyor.user ALTER COLUMN id DROP DEFAULT;
ALTER TABLE surveyor.survey DROP CONSTRAINT survey_author;
ALTER TABLE surveyor.user ALTER COLUMN id SET DATA TYPE varchar(255);
ALTER TABLE surveyor.survey ALTER COLUMN author SET DATA TYPE varchar(255);
ALTER TABLE surveyor.survey ADD CONSTRAINT survey_author FOREIGN KEY (author) REFERENCES surveyor.user (id);