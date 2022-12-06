create table users
(
    id       INTEGER primary key ,
    name     VARCHAR(50),
    login    VARCHAR(20) unique,
    password VARCHAR(20),
    role     varchar(11) default 'COLABORADOR'
);

insert into users (id, name, login, password, role) values (1, 'Alisson Rosa', 'admin', 'admin', 'ADMIN');
insert into users (id, name, login, password) values (2, 'Alisson Colab', 'colab', 'colab');

create table time_sheet (
    id             INTEGER primary key AUTOINCREMENT,
    datePoint      date not null,
    startTime      time,
    startTimeLunch time,
    endTimeLunch   time,
    endTime        time,
    users_id       int,
    constraint FK_TIME_SHEET_USERS
        foreign key (users_id) references users (id),
    constraint UQ_TIME_SHEET
        unique (users_id, datePoint)
);