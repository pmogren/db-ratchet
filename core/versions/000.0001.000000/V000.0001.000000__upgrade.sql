/*
Run this script on:

        SQLVM82.ratchet_259aef38844c4c468363f6d22fe7a933    -  This database will be modified

to synchronize it with a database with the schema represented by:

        redgate-schema

You are recommended to back up your database before running this script

Script created by SQL Compare version 10.4.8 from Red Gate Software Ltd at 5/29/2014 4:33:20 PM

*/
SET NUMERIC_ROUNDABORT OFF
GO
SET ANSI_PADDING, ANSI_WARNINGS, CONCAT_NULL_YIELDS_NULL, ARITHABORT, QUOTED_IDENTIFIER, ANSI_NULLS ON
GO
IF EXISTS (SELECT * FROM tempdb..sysobjects WHERE id=OBJECT_ID('tempdb..#tmpErrors')) DROP TABLE #tmpErrors
GO
CREATE TABLE #tmpErrors (Error int)
GO
SET XACT_ABORT ON
GO
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
GO
BEGIN TRANSACTION
GO
PRINT N'Creating [dbo].[CourseOffering]'
GO
CREATE TABLE [dbo].[CourseOffering]
(
[course_offering_id] [bigint] NOT NULL IDENTITY(1, 1),
[course_id] [bigint] NOT NULL,
[year] [int] NOT NULL,
[semester] [varchar] (1) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL
)
GO
IF @@ERROR<>0 AND @@TRANCOUNT>0 ROLLBACK TRANSACTION
GO
IF @@TRANCOUNT=0 BEGIN INSERT INTO #tmpErrors (Error) SELECT 1 BEGIN TRANSACTION END
GO
PRINT N'Creating primary key [PK_course_offering_id] on [dbo].[CourseOffering]'
GO
ALTER TABLE [dbo].[CourseOffering] ADD CONSTRAINT [PK_course_offering_id] PRIMARY KEY CLUSTERED  ([course_offering_id])
GO
IF @@ERROR<>0 AND @@TRANCOUNT>0 ROLLBACK TRANSACTION
GO
IF @@TRANCOUNT=0 BEGIN INSERT INTO #tmpErrors (Error) SELECT 1 BEGIN TRANSACTION END
GO
PRINT N'Creating [dbo].[CourseEnrollment]'
GO
CREATE TABLE [dbo].[CourseEnrollment]
(
[course_enrollment_id] [bigint] NOT NULL IDENTITY(1, 1),
[course_offering_id] [bigint] NOT NULL,
[student_id] [bigint] NOT NULL
)
GO
IF @@ERROR<>0 AND @@TRANCOUNT>0 ROLLBACK TRANSACTION
GO
IF @@TRANCOUNT=0 BEGIN INSERT INTO #tmpErrors (Error) SELECT 1 BEGIN TRANSACTION END
GO
PRINT N'Creating primary key [PK_course_enrollment_id] on [dbo].[CourseEnrollment]'
GO
ALTER TABLE [dbo].[CourseEnrollment] ADD CONSTRAINT [PK_course_enrollment_id] PRIMARY KEY CLUSTERED  ([course_enrollment_id])
GO
IF @@ERROR<>0 AND @@TRANCOUNT>0 ROLLBACK TRANSACTION
GO
IF @@TRANCOUNT=0 BEGIN INSERT INTO #tmpErrors (Error) SELECT 1 BEGIN TRANSACTION END
GO
PRINT N'Adding foreign keys to [dbo].[CourseEnrollment]'
GO
ALTER TABLE [dbo].[CourseEnrollment] ADD CONSTRAINT [FK_CourseEnrollment_CourseOffering] FOREIGN KEY ([course_offering_id]) REFERENCES [dbo].[CourseOffering] ([course_offering_id])
ALTER TABLE [dbo].[CourseEnrollment] ADD CONSTRAINT [FK_CourseEnrollment_Students] FOREIGN KEY ([student_id]) REFERENCES [dbo].[Students] ([student_id])
GO
IF @@ERROR<>0 AND @@TRANCOUNT>0 ROLLBACK TRANSACTION
GO
IF @@TRANCOUNT=0 BEGIN INSERT INTO #tmpErrors (Error) SELECT 1 BEGIN TRANSACTION END
GO
PRINT N'Adding foreign keys to [dbo].[CourseOffering]'
GO
ALTER TABLE [dbo].[CourseOffering] ADD CONSTRAINT [FK_CourseOffering_Courses] FOREIGN KEY ([course_id]) REFERENCES [dbo].[Courses] ([course_id])
GO
IF @@ERROR<>0 AND @@TRANCOUNT>0 ROLLBACK TRANSACTION
GO
IF @@TRANCOUNT=0 BEGIN INSERT INTO #tmpErrors (Error) SELECT 1 BEGIN TRANSACTION END
GO
IF EXISTS (SELECT * FROM #tmpErrors) ROLLBACK TRANSACTION
GO
IF @@TRANCOUNT>0 BEGIN
PRINT 'The database update succeeded'
COMMIT TRANSACTION
END
ELSE PRINT 'The database update failed'
GO
DROP TABLE #tmpErrors
GO
