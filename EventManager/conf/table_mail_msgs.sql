create table MAIL_MSGS (id int default serial not null)
alter table MAIL_MSGS add STATUS char(1)  not null
alter table MAIL_MSGS add RECIPIENTS varchar(4096)  not null
alter table MAIL_MSGS add SUBJECT varchar(256)  not null
alter table MAIL_MSGS add BODY varchar(4096)  not null
alter table MAIL_MSGS add ATTACHMENT1 varchar(512)
alter table MAIL_MSGS add ATTACHMENT2 varchar(512)
alter table MAIL_MSGS add TIME_STAMP timestamp
alter table MAIL_MSGS add ENCODING varchar(16)