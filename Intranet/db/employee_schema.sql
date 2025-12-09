-- Employee 테이블 및 시퀀스 예시

CREATE TABLE employee (
    depno      NUMBER PRIMARY KEY,
    name       VARCHAR2(50),
    email      VARCHAR2(100) UNIQUE,
    pw         VARCHAR2(100),
    gender     VARCHAR2(10),
    dep        VARCHAR2(50),
    tel        VARCHAR2(30),
    role       VARCHAR2(50),
    logtime    DATE
);

CREATE SEQUENCE depno_employee
    START WITH 2025001
    INCREMENT BY 1
    NOCACHE;
