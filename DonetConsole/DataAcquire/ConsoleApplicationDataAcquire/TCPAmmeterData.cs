using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ModbusTestForm
{
    /// <summary>
    /// 通过使用TCPSGInterface类将电表中读取的数据进行封装
    /// </summary>
    class TCPAmmeterData
    {
        private TCPSGInterface sg;
       
        internal TCPSGInterface Sg
        {
            get { return sg; }
            set { sg = value; }
        }
        public TCPAmmeterData(TCPSGInterface sg1)
        {
            Sg = sg1;
        }

        /// <summary>
        /// 读取四组寄存器值其中前三个是连续的寄存器值
        /// </summary>
        /// <param name="startAddress1">三组连续寄存器的首地址，每组两个寄存器，值类型为float</param>
        /// <param name="startAddress2">一组不连续寄存器的首地址，读两个寄存器，值类型为float</param>
        /// <returns>读到的四组寄存器值</returns>
        public byte[] read_floatRegisters(ushort startAddress1, ushort startAddress2)
        {
            byte[] bholdregs1= new byte[12];
            sg.ReadHoldingRegisters(startAddress1, 6, bholdregs1);
            byte[] bholdregs2 = new byte[4];
            sg.ReadHoldingRegisters(startAddress2, 2, bholdregs2);

            CTConver conver = new CTConver();
            byte[] bholdregs = new byte[16];
            bholdregs = conver.addByte(bholdregs1,bholdregs2);
            return bholdregs;
        }

        /// <summary>
        /// 读取四组连续寄存器的值，每一组有四个寄存器，值类型为double
        /// </summary>
        /// <param name="startAddress">寄存器的首地址</param>
        /// <returns>读到的四组值，对应四个double类型的值</returns>
        public byte[] read_doubleRegisters(ushort startAddress)
        {
            byte[] bholdregs= new byte[32];
            sg.ReadHoldingRegisters(startAddress, 16, bholdregs);
            return bholdregs;
        }
        
        /// <summary>
        /// 相电压
        /// </summary>
        /// <returns>a相相电压、b相相电压、c相相电压、三相相电压的平均值四组值</returns>
        /// 注意：此处得到的byte需要倒序，才能得到相应的值
        public byte[] read_voltage_Vph_n()
        {
            byte[] bholdregs = new byte[16];
            bholdregs=read_floatRegisters(1, 57);
            return bholdregs;
        }

        /// <summary>
        /// 线电压
        /// </summary>
        /// <returns>a-b相间的线电压、b-c相间的线电压、c-a相间的线电压、三相线电压平均值四组值</returns>
        /// 注意：此处得到的byte需要倒序，才能得到相应的值
        public byte[] read_voltage_Vph_ph()
        {
            byte[] bholdregs = new byte[16];
            bholdregs=read_floatRegisters(7, 59);
            return bholdregs;
        }
        
        /// <summary>
        /// 电流
        /// </summary>
        /// <returns>a相电流、b相电流、c相电流、平均电流四组值</returns>
        /// 注意：此处得到的byte需要倒序，才能得到相应的值
        public byte[] read_current()
        {
            byte[] bholdregs = new byte[16];
            bholdregs=read_floatRegisters(13, 295);
            return bholdregs;
        }
        /// <summary>
        /// 视在功率
        /// </summary>
        /// <returns>a相视在功率、b相视在功率、c相视在功率、总视在功率最大值四组值</returns>
        /// 注意：此处得到的byte需要倒序，才能得到相应的值
        public byte[] read_Apparent_Power()
        {
            byte[] bholdregs = new byte[16];
            bholdregs = read_floatRegisters(19, 63);
            return bholdregs;
        }
      
        /// <summary>
        /// 有功功率
        /// </summary>
        /// <returns>a相有功功率、b相有功功率、c相有功功率、总有功功率最大值四组值</returns>
        ///注意：此处得到的byte需要倒序，才能得到相应的值
        public byte[] read_active_Power()
        {
            byte[] bholdregs = new byte[16];
            bholdregs=read_floatRegisters(25, 65);
            return bholdregs;
        }
        /// <summary>
        /// 无功功率
        /// </summary>
        /// <returns>a相无功功率、b相无功功率、c相无功功率、总无功功率最大值四组值</returns>
        /// 注意：此处得到的byte需要倒序，才能得到相应的值
        public byte[] read_Reactive_Power()
        {
            byte[] bholdregs = new byte[16];
            bholdregs = read_floatRegisters(31, 67);
            return bholdregs;
        }
        /// <summary>
        /// 功率因数
        /// </summary>
        /// <returns>a相功率因数、b相功率因数、c相功率因数、总功率因数最大值四组值</returns>
        /// 注意：此处得到的byte需要倒序，才能得到相应的值
        public byte[] read_Power_Factor()
        {
            byte[] bholdregs = new byte[16];
            bholdregs = read_floatRegisters(37, 69);
            return bholdregs;
        }
        /// <summary>
        /// 有功电能
        /// </summary>
        /// <returns>费率1的正向有功电能、费率2的正向有功电能、费率1的负向有功电能、费率2的负向有功电能</returns>
        /// 注意：此处得到的byte需要倒序，才能得到相应的值
        public byte[] read_active_Energy()
        {
            byte[] bholdregs = new byte[32];
            bholdregs=read_doubleRegisters(801);
            return bholdregs;
        }
        /// <summary>
        /// 得到系统时间
        /// </summary>
        /// <returns></returns>
        ///注意： 由于未找到电表中存储时间的寄存器，此处的时间是本地时间。
        public DateTime read_time()
        {
            DateTime dataTime = new DateTime();
            dataTime = System.DateTime.Now;
            return dataTime;
        }
        
        
    }
}
