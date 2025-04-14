alter session set container = XEPDB1;

ALTER SYSTEM SET db_create_file_dest = '/opt/oracle/oradata';

create tablespace ARM;

create user "ARM_OWNER" profile "DEFAULT" identified by "password" default tablespace "ARM" account unlock;

grant connect to ARM_OWNER;
grant unlimited tablespace to ARM_OWNER;

grant create view to ARM_OWNER;
grant create sequence to ARM_OWNER;
grant create table to ARM_OWNER;
grant create procedure to ARM_OWNER;
grant create type to ARM_OWNER;
grant create trigger to ARM_OWNER;