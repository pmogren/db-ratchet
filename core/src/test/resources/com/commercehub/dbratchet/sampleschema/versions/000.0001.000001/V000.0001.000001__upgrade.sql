create table dbo.Students (
    studentId int not null,
    firstName varchar(255) not null,
    lastName varchar(255) not null
);

create table dbo.Enrollment (
    courseId int not null,
    studentId int not null,
    finalGrade decimal
);
