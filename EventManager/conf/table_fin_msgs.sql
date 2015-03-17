create table FIN_MSGS (id int default serial not null)
alter table FIN_MSGS add STATUS char(1) not null
alter table FIN_MSGS add SOURCE varchar(512) not null
alter table FIN_MSGS add MSG_TEXT varchar(4096) not null
alter table FIN_MSGS add INSERT_TIME timestamp not null
alter table FIN_MSGS add SAA_SERVER varchar(23)  not null
alter table FIN_MSGS add FIELD1 varchar(256)
alter table FIN_MSGS add FIELD2 varchar(256)
alter table FIN_MSGS add FIELD3 varchar(256)
alter table FIN_MSGS add FIELD4 varchar(256)
alter table FIN_MSGS add FIELD5 varchar(256)
alter table FIN_MSGS add FIELD6 varchar(256)
alter table FIN_MSGS add FIELD7 varchar(256)
alter table FIN_MSGS add FIELD8 varchar(256)
alter table FIN_MSGS add FIELD9 varchar(256)