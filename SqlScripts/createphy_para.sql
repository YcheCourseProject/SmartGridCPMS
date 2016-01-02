USE [snortsnort]
GO

/****** Object:  Table [dbo].[phy_para]    Script Date: 03/17/2013 14:35:24 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[phy_para](
	[NUM] [int] NOT NULL,
	[refer_k] [float] NULL,
	[alpha] [float] NULL,
 CONSTRAINT [PK_phy_para] PRIMARY KEY CLUSTERED 
(
	[NUM] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


