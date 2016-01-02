	/* IF UPDATE() ... */
alter procedure fusion 
as
	create table #temp1(event_num numeric(10,0),
	event numeric(10,0),ip_src numeric(10,0),
	ip_dst numeric(10,0),timestamp datetime,
	sig_id numeric(10,0))

	create table #temp2(event_num numeric(10,0),
	event numeric(10,0),ip_src numeric(10,0),
	ip_dst numeric(10,0),timestamp datetime,
	sig_id numeric(10,0))


	declare @num int,
	@phywarn int,
	@Time datetime,

	@physicalwarn int,

	/* 检测的时间范围*/
	@endtime datetime,
	@starttime datetime,

	/*用于存放从snort表检测出来的数据*/
	@event_num numeric(10,0),
	@event numeric(10,0),
	@ip_src numeric(10,0),
	@ip_dst numeric(10,0),
	@timestamp datetime,
	@sig_id numeric(10,0),

	/*分析snort表之后的得到的变量*/
	@first_ip_src numeric(10,0),
	@first_ip_dst numeric(10,0),
	@first_sig_id numeric(10,0),

	@second_ip_src numeric(10,0),
	@second_ip_dst numeric(10,0),
	@second_sig_id numeric(10,0),

	/*当前的ip_src,用于分开两个电表的标志变量*/
	@current_ip numeric(10,0),

	/*分析sig_id=2的个数*/

	@count_sig2 int,
	@count_sig1 int

	/*@c_phydet int,
	@c_snort int*/



	begin
	

	set @endtime=getdate()

	set @starttime=Dateadd(s,-10,@endtime)

	/*读取信息层检测表,这里使用了游标，并且用了两张临时表，用于存放数据*/

	Declare c_snort CURSOR FOR

	select  cid,event,ip_src,ip_dst,timestp,sig_id from
	cyber_view 
	where DATEDIFF(second,@endtime,cyber_view.timestp)<0 and DATEDIFF(second,@starttime,cyber_view.timestp)>0
	OPEN c_snort
	FETCH NEXT from c_snort
	INTO @event_num,@event,@ip_src,@ip_dst,@timestamp,@sig_id
	set @current_ip =@ip_dst
	insert  into #temp1  (event_num,event,ip_src,ip_dst,timestamp,sig_id) values (@event_num,@event,@ip_src,@ip_dst,@timestamp,@sig_id)
	set @first_ip_src=@ip_src
	set @first_ip_dst=@ip_dst

	WHILE @@FETCH_STATUS=0
	BEGIN
	FETCH NEXT from c_snort into @event_num,@event,@ip_src,@ip_dst,@timestamp,@sig_id
	if @current_ip<>@ip_dst
	begin
	insert  into #temp2  (event_num,event,ip_src,ip_dst,timestamp,sig_id) values (@event_num,@event,@ip_src,@ip_dst,@timestamp,@sig_id)
	set @second_ip_src=@ip_src
	set @second_ip_dst=@ip_dst
	end
	else
	insert  into #temp1  (event_num,event,ip_src,ip_dst,timestamp,sig_id) values (@event_num,@event,@ip_src,@ip_dst,@timestamp,@sig_id)
    END
	CLOSE c_snort
	DEALLOCATE c_snort
	

	/*两个ip的攻击检测在两张临时表里面.分析这两张表格*/
	select @count_sig2=count(*) from #temp1  where sig_id=4
	select @count_sig1=count(*) from #temp1 where sig_id=3

	if @count_sig2>0
	set @first_sig_id=2
	else if @count_sig2=0 and @count_sig1>0
	set @first_sig_id=1
	else if @count_sig2=0 and @count_sig1=0
	begin
	set @first_sig_id=0
	end

	select @count_sig2=count(*) from #temp2  where sig_id=4
	select @count_sig1=count(*) from #temp2 where sig_id=3
	if @count_sig2>0
	set @second_sig_id=2
	else if @count_sig2=0 and @count_sig1>0
	set @second_sig_id=1
	else if @count_sig2=0 and @count_sig1=0
	set @second_sig_id=0

	drop table #temp1
	drop table #temp2

	/*读取物理层检测表,初始化physicalwarn为0*/
	set @physicalwarn=0

	Declare c_phydet CURSOR FOR

	select num,PhyWarn,Time
	from phydect_table 
	WHERE DATEDIFF(second,@endtime,phydect_table.Time)<0 and DATEDIFF(second,@starttime,phydect_table.Time)>0

	OPEN c_phydet
	FETCH NEXT from c_phydet INTO @num,@phywarn,@Time
	IF @phywarn =1
	set @physicalwarn=1
	WHILE @@FETCH_STATUS=0
	BEGIN
	FETCH NEXT from c_phydet INTO @num,@phywarn,@Time
	IF @phywarn =1
	set @physicalwarn=1
	end
	
	CLOSE c_phydet
	DEALLOCATE c_phydet
	/*物理层数据和信息层数据进行融合*/

	if @first_sig_id=2 and @physicalwarn=1
	insert into threat_fusion (ip_src,ip_dst,th_type,th_percent,th_probability,timestp)
	values(@first_ip_src,@first_ip_dst,2,1,'pc',@endtime)

	else if @first_sig_id=1 and @physicalwarn=1
	insert into threat_fusion (ip_src,ip_dst,th_type,th_percent,th_probability,timestp)
	values(@first_ip_src,@first_ip_dst,1,0.5,'pc',@endtime)

	else if @first_sig_id=0 and @physicalwarn=1
	insert into threat_fusion (ip_src,ip_dst,th_type,th_percent,th_probability,timestp)
	values(1,1,1,0.1,'p',@endtime)

	else if @first_sig_id=2 and @physicalwarn=0
	insert into threat_fusion (ip_src,ip_dst,th_type,th_percent,th_probability,timestp)
	values(@first_ip_src,@first_ip_dst,1,0.7,'c',@endtime)

	else if @first_sig_id=1 and @physicalwarn=0
	insert into threat_fusion (ip_src,ip_dst,th_type,th_percent,th_probability,timestp)
	values(@first_ip_src,@first_ip_dst,1,0.3,'c',@endtime)

	else if @first_sig_id=0 and @physicalwarn=0
	insert into threat_fusion (ip_src,ip_dst,th_type,th_percent,th_probability,timestp)
	values(1,1,0,0.0,'great',@endtime)



	if @second_sig_id=2 and @physicalwarn=1
	insert into threat_fusion (ip_src,ip_dst,th_type,th_percent,th_probability,timestp)
	values(@second_ip_src,@second_ip_dst,2,1,'pc',@endtime)

	else if @second_sig_id=1 and @physicalwarn=1
	insert into threat_fusion (ip_src,ip_dst,th_type,th_percent,th_probability,timestp)
	values(@second_ip_src,@second_ip_dst,1,0.5,'pc',@endtime)


	else if @second_sig_id=2 and @physicalwarn=0
	insert into threat_fusion (ip_src,ip_dst,th_type,th_percent,th_probability,timestp)
	values(@second_ip_src,@second_ip_dst,1,0.7,'c',@endtime)

	else if @second_sig_id=1 and @physicalwarn=0
	insert into threat_fusion (ip_src,ip_dst,th_type,th_percent,th_probability,timestp)
	values(@second_ip_src,@second_ip_dst,1,0.3,'c',@endtime)

end