create table STUDENTS (
    STUDENTID int not null,
    FIRSTNAME varchar(255) not null,
    LASTNAME varchar(255) not null
);

create table ENROLLMENT (
    COURSEID int not null,
    STUDENTID int not null,
    FINALGRADE decimal
);

create table EMPTY (
    EMPTYID int not null
);