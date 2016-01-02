SELECT     dbo.event.cid, dbo.event.sid AS event, 
dbo.iphdr.ip_src, dbo.iphdr.ip_dst, dbo.event.timestamp AS timestp, 
dbo.signature.sig_id,dbo.signature.sig_name
FROM         dbo.event INNER JOIN
                      dbo.iphdr ON dbo.event.sid = dbo.iphdr.sid AND dbo.event.cid = dbo.iphdr.cid INNER JOIN
                      dbo.signature ON dbo.event.signature = dbo.signature.sig_id