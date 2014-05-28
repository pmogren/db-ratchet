CREATE TABLE [dbo].[Students]
(
[student_id] [bigint] NOT NULL IDENTITY(1, 1),
[first_name] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
[last_name] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL
)
GO
