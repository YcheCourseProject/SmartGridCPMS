
declare @endTime datetime
declare @durationTime int 
declare @startTime datetime
set @durationTime=10000
set @endTime=getdate()
set @startTime=Dateadd(s,-@durationTime,@endTime)
select @startTime,@endTime
select powersum,power1,power2,ratio
from physical_view
where DATEDIFF("second",@endTime,physical_view.time)<0 
  and DATEDIFF("second",@startTime,physical_view.time)>0
 