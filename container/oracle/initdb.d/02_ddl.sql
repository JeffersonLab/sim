alter session set container = XEPDB1;

CREATE SEQUENCE SIM_OWNER.REPOSITORY_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
    NOCACHE
    ORDER;

CREATE SEQUENCE SIM_OWNER.SOFTWARE_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
    NOCACHE
    ORDER;

CREATE TABLE SIM_OWNER.SETTING
(
    KEY                VARCHAR2(128 CHAR)  NOT NULL,
    VALUE              VARCHAR2(4000 BYTE) NOT NULL,
    TYPE               VARCHAR2(32 CHAR) DEFAULT 'STRING' NOT NULL,
    DESCRIPTION        VARCHAR2(2048 CHAR) NOT NULL,
    TAG                VARCHAR2(32 CHAR) DEFAULT 'OTHER' NOT NULL,
    WEIGHT             INTEGER DEFAULT 0 NOT NULL,
    CHANGE_ACTION_JNDI VARCHAR2(128 CHAR) NULL ,
    CONSTRAINT SETTING_PK PRIMARY KEY (KEY),
    CONSTRAINT SETTING_CK1 CHECK (TYPE IN ('STRING', 'BOOLEAN', 'CSV')),
    CONSTRAINT SETTING_CK2 CHECK ((VALUE in ('Y', 'N') AND TYPE = 'BOOLEAN') OR TYPE <> 'BOOLEAN')
);

CREATE TABLE SIM_OWNER.REPOSITORY
(
    REPOSITORY_ID INTEGER NOT NULL,
    NAME          VARCHAR2(128 CHAR) NOT NULL,
    DESCRIPTION   VARCHAR2(256 CHAR) NULL,
    HOME_URL      VARCHAR2(256 CHAR) NULL,
    CONSTRAINT REPO_PK PRIMARY KEY (REPOSITORY_ID),
    CONSTRAINT REPO_AK1 UNIQUE (NAME)
);

CREATE TABLE SIM_OWNER.SOFTWARE
(
   SOFTWARE_ID             INTEGER NOT NULL,
   NAME                    VARCHAR2(128 CHAR) NOT NULL,
   TYPE                    VARCHAR2(32 CHAR) NULL,
   DESCRIPTION             VARCHAR2(256 CHAR) NULL,
   REPOSITORY_ID           INTEGER NOT NULL,
   HOME_URL                VARCHAR2(256 CHAR) NULL,
   MAINTAINER_USERNAME_CSV VARCHAR2(256 CHAR) NULL,
   CONSTRAINT SOFTWARE_PK PRIMARY KEY (SOFTWARE_ID),
   CONSTRAINT SOFTWARE_AK1 UNIQUE (NAME, REPOSITORY_ID),
   CONSTRAINT SOFTWARE_FK1 FOREIGN KEY (REPOSITORY_ID) REFERENCES SIM_OWNER.REPOSITORY (REPOSITORY_ID),
   CONSTRAINT SOFTWARE_CK1 CHECK (TYPE IN ('APP', 'LIB', 'SCRIPT', 'PLUGIN'))
);