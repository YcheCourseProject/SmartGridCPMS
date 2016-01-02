
declare @endTime datetime
declare @durationTime int 
declare @startTime datetime
set @durationTime=360000
set @endTime=getdate()
set @startTime=Dateadd(s,-@durationTime,@endTime)
select @startTime,@endTime
select *
from physical_view
where DATEDIFF("second",@endTime,physical_view.time)<0 
  and DATEDIFF("second",@startTime,physical_view.time)>0
select * from cyber_view
where DATEDIFF("second",@endTime,cyber_view.timestp)<0 
  and DATEDIFF("second",@startTime,cyber_view.timestp)>0
select * from threat_fusion
where DATEDIFF("second",@endTime,threat_fusion.timestp)<0 
  and DATEDIFF("second",@startTime,threat_fusion.timestp)>0