CREATE TABLE "USERS" (
      "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
      "FULLNAME" VARCHAR(50),
      "EMAIL" VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE "EVENTS" (
    "ID" BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    "USERID" BIGINT REFERENCES USERS (ID),
    "EVENTNAME" VARCHAR(50),
    "DESCRIPTION" VARCHAR(50),
    "STARTDATE" TIMESTAMP,
    "ENDDATE" TIMESTAMP,
    "CATEGORY" SMALLINT NOT NULL

);

