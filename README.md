# Smart Grid Cyber Physical Fusion Intrusion Detection System

## 概览

项目名称: Smart Grid Cyber-Physical Fusion Intrusion Detection System 

奖项: 全国大学生电子设计竞赛-2014年信息安全技术专题邀请赛， 二等奖

团队: 车煜林， 孙鸿， 刘玉琪 (西安交通大学本科时期)， 指导老师: 刘烃

## 项目文件结构

### Android Client
- [Android Source Code](Android), the android app is used to
attack the simulated smart grid with several meters.  

### Console Program  
- [DataAcquire Module Source Code](DonetConsole/DataAcquire),
this module is used to acquire data from smart meters  
- [InfoFusion Module Source Code](DonetConsole/InfoFusion),
this module is used to fuse the info from cyber and physical sides.  

### Web Program  
- [Web Source Code](DonetWeb), this module is used to display intrusion status,
this part is the most important part of whole project, which mainly uses `.net`
and `javascript`.


### Snort Files
- [Snort Files](Snort), third-party codes, used for collecting cyber side information  
- [Snort.bat](snort.bat) is used for windows users  

### SqlScripts  
- [SqlScripts](SqlScripts), script used for creating tables  
