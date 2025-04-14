alter session set container = XEPDB1;

ALTER SYSTEM SET db_create_file_dest = '/opt/oracle/oradata';

create tablespace SIM;

create user "SIM_OWNER" profile "DEFAULT" identified by "password" default tablespace "SIM" account unlock;

grant connect to SIM_OWNER;
grant unlimited tablespace to SIM_OWNER;

grant create view to SIM_OWNER;
grant create sequence to SIM_OWNER;
grant create table to SIM_OWNER;
grant create procedure to SIM_OWNER;
grant create type to SIM_OWNER;
grant create trigger to SIM_OWNER;