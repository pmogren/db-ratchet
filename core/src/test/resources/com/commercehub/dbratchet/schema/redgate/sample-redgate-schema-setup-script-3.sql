SET TRANSACTION ISOLATION LEVEL SERIALIZABLE
GO
BEGIN TRANSACTION
GO
CREATE TABLE [dbo].[Tests]
(
[test_id] [bigint] NOT NULL IDENTITY(1, 1),
[course_offering_id] [bigint] NOT NULL,
[test_index] [int] NOT NULL,
[weight] [decimal] (3, 2) NOT NULL
)
GO
ALTER TABLE [dbo].[Tests] ADD CONSTRAINT [PK_test_id] PRIMARY KEY CLUSTERED  ([test_id])
GO
ALTER TABLE [dbo].[Tests] ADD CONSTRAINT [FK_Tests_CourseOffering] FOREIGN KEY ([course_offering_id]) REFERENCES [dbo].[CourseOffering] ([course_offering_id])
GO
IF @@ERROR<>0 AND @@TRANCOUNT>0 ROLLBACK TRANSACTION ELSE COMMIT TRANSACTION
GO

BEGIN TRANSACTION
GO
CREATE TABLE [dbo].[TestScores]
(
[test_score_id] [bigint] NOT NULL IDENTITY(1, 1),
[test_id] [bigint] NOT NULL,
[course_enrollment_id] [bigint] NOT NULL,
[score] [decimal] (3, 2) NOT NULL
)
GO
ALTER TABLE [dbo].[TestScores] ADD CONSTRAINT [PK_test_score_id] PRIMARY KEY CLUSTERED  ([test_score_id])
GO
ALTER TABLE [dbo].[TestScores] ADD CONSTRAINT [FK_TestScores_Tests] FOREIGN KEY ([test_id]) REFERENCES [dbo].[Tests] ([test_id])
GO
ALTER TABLE [dbo].[TestScores] ADD CONSTRAINT [FK_TestScores_CourseEnrollment] FOREIGN KEY ([course_enrollment_id]) REFERENCES [dbo].[CourseEnrollment] ([course_enrollment_id])
GO
IF @@ERROR<>0 AND @@TRANCOUNT>0 ROLLBACK TRANSACTION ELSE COMMIT TRANSACTION
GO

BEGIN TRANSACTION
GO
ALTER TABLE [dbo].[CourseEnrollment] ADD
[final_grade] [decimal] (3, 2) NULL
GO
IF @@ERROR<>0 AND @@TRANCOUNT>0 ROLLBACK TRANSACTION ELSE COMMIT TRANSACTION
GO
