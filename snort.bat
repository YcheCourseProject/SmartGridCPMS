d:
cd d:\Snort\bin
set PCAP_FRAMES=max
snort -W
pause
snort -de -l ../log -c ../etc/snort.conf
pause