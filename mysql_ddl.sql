mysqld --initialize
ALTER USER 'root'@'localhost' IDENTIFIED BY '1234';

create database spraymoney;
create user 'test'@'%' identified by '1234';
grant all privileges on test.* to 'test'@'%';
flush privileges;

create table `TEST` (
  `id`   int(16)      not null auto_increment,
  `name` varchar(32)  not null,
  primary key(`id`)
);


alter user 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '1234';


create table `tuser` (
  `user_id`     varchar(8)    not null primary key,
  `room_id`     varchar(4)    not null
);

create table `ttoken` (
  `token`       varchar(3)  not null primary key,
  `amt`         bigint(18)  not null,
  `person_num`  int(9)      not null,
  `reg_dt`      datetime    not null,
  `reg_id`      varchar(8)  not null,
  foreign key(reg_id) references tuser(user_id)
);

create table `tmoney` (
  `seq`         int(9)      not null auto_increment,
  `token`       varchar(3)  not null,
  `part_amt`    bigint(18)  not null,
  `rcv_id`      varchar(8)  null,
  primary key(seq),
  foreign key(token) references ttoken(token),
  foreign key(rcv_id) references tuser(user_id)
);