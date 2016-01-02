using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using MeterInterface;
using System.Threading;
using System.Data;
using System.Data.SqlClient;
using System.Timers;
using log4net;
using log4net.Config;
using System.IO;


namespace ConsoleApplicationDataAcquire
{
    class Program
    {
        static string version="0.1.2.SH_edition";
        static bool RunFlag = true;
        static int SampleCount=0;
        const int SampleTime=2;
        public TCPSGInterface TcpMeter1, TcpMeter2, TcpMeter3;
        double Energy1, Energy2, Energy3; 
        double Energy1Previous, Energy2Previous, Energy3Previous;
        double CostEnergy1, CostEnergy2, CostEnergy3;
        //double EnergyBaseError=0, Deta2EnergyBase=0;
        double Power1, Power2, Power3, RPower1, RPower2, RPower3;
        double U1, U2, U3, I1, I2, I3;
        double Ratio;
        const double CompareRatio=3;
        int warnflag;
        //double MeterBaseCost = (double)(1)/360 * SampleTime;
        //double MeterBaseCost =0.000133;//测试值 
        //用于存放读取电表数据的临时寄存器数组
        byte[] bholdregs1 = new byte[8];
        byte[] bholdregs2 = new byte[8];
        byte[] bholdregs3 = new byte[8];
        byte[] bholdregs4 = new byte[4];
        byte[] bholdregs5 = new byte[4];
        byte[] bholdregs6 = new byte[4];
        byte[] bholdregs7 = new byte[4];
        byte[] bholdregs8 = new byte[4];
        byte[] bholdregs9 = new byte[4];
        byte[] bholdregs10 = new byte[4];
        byte[] bholdregs11 = new byte[4];
        byte[] bholdregs12 = new byte[4];
        byte[] bholdregs13 = new byte[4];
        byte[] bholdregs14 = new byte[4];
        byte[] bholdregs15 = new byte[4];
        //时间，用于记录读取数据的时间
        DateTime dataTime;
        //bool FirstFlag = true;
        bool error=false;
        SqlConnection snortSqlConnection;
        SqlCommand snortSqlCommand;
        //System.Timers.Timer aTimer;
        //log configuration in app.config 日志组件
        log4net.ILog datalog = log4net.LogManager.GetLogger("DataLogger");
        log4net.ILog errorlog = log4net.LogManager.GetLogger("ErrorLogger");

        //检测程序是否停止
        public void DetectStopFunc()
        {
            Console.ReadKey(true);
            RunFlag = !RunFlag;
            //aTimer.Enabled = RunFlag;
            if(!RunFlag)
                Console.WriteLine("Process stopped.Press any key to continue");
            else
                Console.WriteLine("Process started.Press any key to stop");
        }

        Program()
        {
            try
            {
                //电表连接
                TcpMeter1 = new TCPSGInterface("192.168.1.105");
                TcpMeter2 = new TCPSGInterface("192.168.1.106"); 
                TcpMeter3 = new TCPSGInterface("192.168.1.107");

                //数据库连接
                string strConnetcion = "user id=snort;password=snort;";
                strConnetcion += "database=snortsnort;Server=127.0.0.1";
                snortSqlConnection = new SqlConnection(strConnetcion);
                ////创建SqlCommand对象
                snortSqlCommand = snortSqlConnection.CreateCommand();
            }
            catch (Exception e)
            {
                error = true;
                Console.WriteLine("{0}", e.ToString());
                errorlog.Error(e);
            }

        }
        void Data2mssql()
        {
            if (error)
            {
                Console.WriteLine("Something error happened");
                return;
            }
            dataTime = System.DateTime.Now;
            //Console.WriteLine("Starttime:{0}",dataTime);
            try
            {
                //费率1的正向有功电能，单位Wh，格式Double
                //meter1
                TcpMeter1.ReadHoldingRegisters(801, 4, bholdregs1);
                //meter2
                TcpMeter2.ReadHoldingRegisters(801, 4, bholdregs2);
                //meter3
                TcpMeter3.ReadHoldingRegisters(801, 4, bholdregs3);

                //总有功功率，单位W，格式Float
                //meter1  active power
                TcpMeter1.ReadHoldingRegisters(65, 2, bholdregs4);
                //meter2  
                TcpMeter2.ReadHoldingRegisters(65, 2, bholdregs5);
                //meter3 
                TcpMeter3.ReadHoldingRegisters(65, 2, bholdregs6);

                //总无功功率，单位VAR，格式Float
                //meter1  reactive power
                TcpMeter1.ReadHoldingRegisters(67, 2, bholdregs7);
                //meter2 
                TcpMeter2.ReadHoldingRegisters(67, 2, bholdregs8);
                //meter3
                TcpMeter3.ReadHoldingRegisters(67, 2, bholdregs9);

                //各表的电压值
                //meter1  U
                TcpMeter1.ReadHoldingRegisters(1, 2, bholdregs10);
                //meter2  U
                TcpMeter2.ReadHoldingRegisters(1, 2, bholdregs11);
                //meter3  U
                TcpMeter3.ReadHoldingRegisters(1, 2, bholdregs12);

                //各表的电流值
                //meter1  I
                TcpMeter1.ReadHoldingRegisters(13, 2, bholdregs13);
                //meter2  I
                TcpMeter2.ReadHoldingRegisters(13, 2, bholdregs14);
                //meter3  I
                TcpMeter3.ReadHoldingRegisters(13, 2, bholdregs15);
                //按照上述函数的功能，应该能够采集到电表中的电压、电流、有功功率、无功功率和实际消耗的电能
            }
            catch (Exception e)
            {
                Console.WriteLine("{0}", e.ToString());
                errorlog.Error(e);
            }
            //Console.WriteLine("endtime:  {0}", System.DateTime.Now);
            if (System.DateTime.Now.Subtract(dataTime).Seconds > 1)
            {
                Console.WriteLine("too long time between meter reading,drop this data");
                return;
            }
            //协议数据流转换为实际物理数据
            Array.Reverse(bholdregs1);//为何要进行数组中数据的反转？
            Energy1 = BitConverter.ToDouble(bholdregs1, 0);
            Array.Reverse(bholdregs2);
            Energy2 = BitConverter.ToDouble(bholdregs2, 0);
            Array.Reverse(bholdregs3);
            Energy3 = BitConverter.ToDouble(bholdregs3, 0);
            //Deta1Energy = Energy1 - Energy2 - Energy3;

            Array.Reverse(bholdregs4, 0, 4);
            Power1 = BitConverter.ToSingle(bholdregs4, 0);
            Array.Reverse(bholdregs5, 0, 4);
            Power2 = BitConverter.ToSingle(bholdregs5, 0);
            Array.Reverse(bholdregs6, 0, 4);
            Power3 = BitConverter.ToSingle(bholdregs6, 0);

            Array.Reverse(bholdregs7, 0, 4);
            RPower1 = BitConverter.ToSingle(bholdregs7, 0);
            Array.Reverse(bholdregs8, 0, 4);
            RPower2 = BitConverter.ToSingle(bholdregs8, 0);
            Array.Reverse(bholdregs9, 0, 4);
            RPower3 = BitConverter.ToSingle(bholdregs9, 0);

            Array.Reverse(bholdregs10, 0, 4);
            U1 = BitConverter.ToSingle(bholdregs10, 0);
            Array.Reverse(bholdregs11, 0, 4);
            U2 = BitConverter.ToSingle(bholdregs11, 0);
            Array.Reverse(bholdregs12, 0, 4);
            U3 = BitConverter.ToSingle(bholdregs12, 0);

            Array.Reverse(bholdregs13, 0, 4);
            I1 = BitConverter.ToSingle(bholdregs13, 0);
            Array.Reverse(bholdregs14, 0, 4);
            I2 = BitConverter.ToSingle(bholdregs14, 0);
            Array.Reverse(bholdregs15, 0, 4);
            I3 = BitConverter.ToSingle(bholdregs15, 0);

            switch (SampleCount)
            {
                case 0://第一次采样
                    SampleCount++;
                    //Deta1EnergyPrevious = Deta1Energy;
                    Energy1Previous = Energy1;
                    Energy2Previous = Energy2;
                    Energy3Previous = Energy3;
                    break;
                default://第二次采样，可计算出两次采样间隔间的能量消耗在三表间的偏差Deta2Energy，以此为偏差基准Deta2EnergyBase
                    SampleCount++;
                    //Deta2Energy = Math.Abs(Deta1Energy - Deta1EnergyPrevious - MeterBaseCost);
                    //Deta1EnergyPrevious = Deta1Energy;
                    //Deta2EnergyPrevious = Deta2Energy;
                    //Deta2EnergyBase = Deta2Energy;//基准偏差

                    CostEnergy1 = Energy1 - Energy1Previous;
                    CostEnergy2 = Energy2 - Energy2Previous;
                    CostEnergy3 = Energy3 - Energy3Previous;

                    Energy1Previous = Energy1;
                    Energy2Previous = Energy2;
                    Energy3Previous = Energy3;

                    //Ratio = (CostEnergy1 - CostEnergy2 - CostEnergy3 - MeterBaseCost) / CostEnergy1;
                    Ratio = Power1 - Power2 - Power3;

                    //Console.WriteLine("Deta2Energy:{0}Wh", Deta2Energy);
                    //Console.WriteLine("CostEnergy1-2-3:{0}Wh", CostEnergy1 - CostEnergy2 - CostEnergy3);
                    break;
                //default://经过两次采样后的采样
                    //Deta2Energy = Math.Abs(Deta1Energy - Deta1EnergyPrevious - MeterBaseCost);
                    //Deta1EnergyPrevious = Deta1Energy;
                    //Deta2EnergyPrevious = Deta2Energy;
                    //Deta3Energy = Deta2Energy - Deta2EnergyBase;

                    //CostEnergy1 = Energy1 - Energy1Previous;
                    //CostEnergy2 = Energy2 - Energy2Previous;
                    //CostEnergy3 = Energy3 - Energy3Previous;

                    //Energy1Previous = Energy1;
                    //Energy2Previous = Energy2;
                    //Energy3Previous = Energy3;

                    //Ratio = (CostEnergy1 - CostEnergy2 - CostEnergy3 - MeterBaseCost) / CostEnergy1;
                    //Ratio = Power1 - Power2 - Power3;
                    //Ratio=Deta2Energy/CostEnergy1;
                    //Console.WriteLine("Deta2Energy:{0}Wh", Deta2Energy);
                    //Console.WriteLine("CostEnergy1-2-3:{0}Wh", CostEnergy1 - CostEnergy2 - CostEnergy3);
                    //Console.WriteLine("Deta3Energy:{0}Wh", Deta3Energy);
                    //break;

            }
            LogInfo();
            DebugInfo();
            if (SampleCount >= 2)
            {
                //确定报警标志
                if (Math.Abs(Ratio) > CompareRatio)
                    warnflag = 1;
                else warnflag = 0;
                try
                {
                    Console.WriteLine("write to mssql");
                    //写入数据库
                    string InsertString = "INSERT INTO phydect_table(EnergySum,Energy1,Energy2,PowerSum,Power1,Power2,RPowerSum,RPower1,RPower2,USum,U1,U2,ISum,I1,I2,CostEnergy,Ratio,PhyWarn,Time) VALUES(" +
                        Energy1 + "," +
                        Energy2 + "," +
                        Energy3 + "," +
                        Power1 + "," +
                        Power2 + "," +
                        Power3 + "," +
                        RPower1 + "," +
                        RPower2 + "," +
                        RPower3 + "," +
                        U1 + "," +
                        U2 + "," +
                        U3 + "," +
                        I1 + "," +
                        I2 + "," +
                        I3 + "," +
                        CostEnergy1 + "," +
                        Ratio + "," +
                        warnflag + "," +
                        "\'" + dataTime + "\'" + ")";
                    snortSqlCommand.CommandText = InsertString;
                 
                    //用SqlCommand对象，插入一条新的记录
                    snortSqlConnection.Open();
                    snortSqlCommand.ExecuteNonQuery();
                    snortSqlConnection.Close();
                }
                catch (Exception e)
                {
                    Console.WriteLine("{0}", e.ToString());
                    errorlog.Error(e);
                }
            }

        }



        /// <summary>
        /// 调试用
        /// </summary>
        void DebugInfo()
        {
            Console.WriteLine("**************************************");
            Console.WriteLine("{0}", dataTime);
            Console.WriteLine("Energy1:{0} Wh", Energy1);
            Console.WriteLine("Energy2:{0} Wh", Energy2);
            Console.WriteLine("Energy3:{0} Wh", Energy3);
            Console.WriteLine("power1:{0}W", Power1);
            Console.WriteLine("power2:{0}W", Power2);
            Console.WriteLine("power3:{0}W", Power3);
            Console.WriteLine("NPower1={0}var", RPower1);
            Console.WriteLine("NPower2={0}var", RPower2);
            Console.WriteLine("NPower3={0}var", RPower3);
            Console.WriteLine("U1={0}V", U1);
            Console.WriteLine("U2={0}V", U2);
            Console.WriteLine("U3={0}V", U3);
            Console.WriteLine("I1={0}A", I1);
            Console.WriteLine("I2={0}A", I2);
            Console.WriteLine("I3={0}A", I3);
            Console.WriteLine("CostEnergy1={0}Wh", CostEnergy1);
            Console.WriteLine("Ratio:{0}", Ratio);
            Console.WriteLine("**************************************");
        }

        /// <summary>
        /// using log4net, write log to [logfile]
        /// configuration in:AssemblyInfo.cs &amp; app.config
        /// </summary>
        void LogInfo()
        {
            string LogStr;
            LogStr =
                "Date:"+dataTime.ToString()+" "+
                "Energy1:" + Energy1.ToString() + "Wh "+" "+
                "Energy2:" + Energy2.ToString() + "Wh "+
                "Energy3:" + Energy3.ToString() + "Wh "+
                "CostEnergy1:" + CostEnergy1.ToString() + "Wh "+
                "Ratio:" + Ratio.ToString();
            datalog.Info(LogStr);
        }

        /// <summary>
        /// 程序入口
        /// </summary>
        /// <param name="args"></param>
        static void Main(string[] args)
        {
            Console.WriteLine("SmartMemte DataAcquire Program");
            Console.WriteLine("Version:{0}",version);
            Console.WriteLine("SampleTime: {0} seconds",SampleTime);
            Console.WriteLine("waiting......");
            Program a = new Program();
            Thread aThread = new Thread(new ThreadStart(a.DetectStopFunc));
            aThread.Start();
            Console.WriteLine("<Start>");
            while (RunFlag)
            {
                a.Data2mssql();
                System.Threading.Thread.Sleep(SampleTime*1000);
                //a.DetectStopFunc();
            }

            Console.ReadKey(true);
        }


    }
}


   