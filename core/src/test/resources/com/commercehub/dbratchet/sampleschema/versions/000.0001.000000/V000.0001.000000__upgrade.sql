create schema dbo;

create table dbo.Courses (
    courseId int not null,
    name varchar(255) not null,
    yearOffered int not null,
    semester varchar(1) not null
);
