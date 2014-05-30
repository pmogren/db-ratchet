SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
GO
BEGIN TRANSACTION
GO
CREATE TABLE [dbo].[CourseOffering]
(
[course_offering_id] [bigint] NOT NULL,
[course_id] [bigint] NOT NULL,
[year] [int] NOT NULL,
[semester] [varchar] (1) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL
)
GO
ALTER TABLE [dbo].[CourseOffering] ADD CONSTRAINT [PK_course_offering_id] PRIMARY KEY CLUSTERED  ([course_offering_id])
GO
ALTER TABLE [dbo].[CourseOffering] ADD CONSTRAINT [FK_CourseOffering_Courses] FOREIGN KEY ([course_id]) REFERENCES [dbo].[Courses] ([course_id])
GO
IF @@ERROR<>0 AND @@TRANCOUNT>0 ROLLBACK TRANSACTION ELSE COMMIT TRANSACTION
GO

BEGIN TRANSACTION
GO
CREATE TABLE [dbo].[CourseEnrollment]
(
[course_enrollment_id] [bigint] NOT NULL,
[course_offering_id] [bigint] NOT NULL,
[student_id] [bigint] NOT NULL
)
GO
ALTER TABLE [dbo].[CourseEnrollment] ADD CONSTRAINT [PK_course_enrollment_id] PRIMARY KEY CLUSTERED  ([course_enrollment_id])
GO
ALTER TABLE [dbo].[CourseEnrollment] ADD CONSTRAINT [FK_CourseEnrollment_CourseOffering] FOREIGN KEY ([course_offering_id]) REFERENCES [dbo].[CourseOffering] ([course_offering_id])
GO
ALTER TABLE [dbo].[CourseEnrollment] ADD CONSTRAINT [FK_CourseEnrollment_Students] FOREIGN KEY ([student_id]) REFERENCES [dbo].[Students] ([student_id])
GO
IF @@ERROR<>0 AND @@TRANCOUNT>0 ROLLBACK TRANSACTION ELSE COMMIT TRANSACTION
GO
