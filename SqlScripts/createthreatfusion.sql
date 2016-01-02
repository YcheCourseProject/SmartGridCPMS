USE [snortsnort]
GO

/****** Object:  Table [dbo].[threat_fusion]    Script Date: 03/17/2013 14:28:54 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[threat_fusion](
	[th_id] [int] IDENTITY(1,1) NOT NULL,
	[ip_src] [numeric](10, 0) NOT NULL,
	[ip_dst] [numeric](10, 0) NOT NULL,
	[th_type] [int] NOT NULL,
	[th_percent] [float] NOT NULL,
	[th_probability] [char](10) NOT NULL,
	[timestp] [datetime] NOT NULL,
 CONSTRAINT [PK_threat_fusion] PRIMARY KEY CLUSTERED 
(
	[th_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


