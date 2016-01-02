select count(*) pthreatcount
from phydect_table
where phywarn=1

select count(*) cthreatcount
from cyber_view

select count(*) fusionthreatcount
from threat_fusion

select count(*) as count, sig_name
from cyber_view group by sig_name