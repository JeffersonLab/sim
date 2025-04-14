alter session set container = XEPDB1;

ALTER SYSTEM SET db_create_file_dest = '/opt/oracle/oradata';

create tablespace TEMPLATE;

create user "TEMPLATE_OWNER" profile "DEFAULT" identified by "password" default tablespace "TEMPLATE" account unlock;

grant connect to TEMPLATE_OWNER;
grant unlimited tablespace to TEMPLATE_OWNER;

grant create view to TEMPLATE_OWNER;
grant create sequence to TEMPLATE_OWNER;
grant create table to TEMPLATE_OWNER;
grant create procedure to TEMPLATE_OWNER;
grant create type to TEMPLATE_OWNER;
grant create trigger to TEMPLATE_OWNER;