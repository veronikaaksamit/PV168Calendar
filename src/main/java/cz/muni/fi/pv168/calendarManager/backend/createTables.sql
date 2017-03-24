CREATE TABLE "USER" (
      "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
      "FULLNAME" VARCHAR(40),
      "EMAIL" VARCHAR(30) NOT NULL,
);

CREATE TABLE "EVENT" (
    "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "USERID" BIGINT REFERENCES USER(ID),
    "EVENTNAME" VARCHAR(20),
    "DESCRIPTION" VARCHAR(50),
    "STARTDATE" DATE,
    "ENDDATE" DATE,
    "CATEGORY" SMALLINT NOT NULL

);