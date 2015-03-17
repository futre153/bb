create table SAA_TRAPS (id int default serial not null)
alter table SAA_TRAPS add STATUS char(1) not null
alter table SAA_TRAPS add SAA_IP_ADDRESS varchar(23) not null
alter table SAA_TRAPS add SAA_INSTANCE varchar(15) not null
alter table SAA_TRAPS add SAA_DATE date not null
alter table SAA_TRAPS add SAA_TIME time not null
alter table SAA_TRAPS add SAA_PLUGIN varchar(64) not null
alter table SAA_TRAPS add SAA_EVENT_NUMBER int not null
alter table SAA_TRAPS add SAA_EVENT_SEVERITY varchar(64) not null
alter table SAA_TRAPS add SAA_EVENT_CLASS varchar(64) not null
alter table SAA_TRAPS add SAA_EVENT_NAME varchar(64) not null
alter table SAA_TRAPS add SAA_EVENT_DESCRIPTION varchar(2048) not null
alter table SAA_TRAPS add FIELD1 varchar(256)
alter table SAA_TRAPS add FIELD2 varchar(256)
alter table SAA_TRAPS add FIELD3 varchar(256)
alter table SAA_TRAPS add FIELD4 varchar(256)
alter table SAA_TRAPS add FIELD5 varchar(256)
alter table SAA_TRAPS add FIELD6 varchar(256)
alter table SAA_TRAPS add FIELD7 varchar(256)
alter table SAA_TRAPS add FIELD8 varchar(2048)