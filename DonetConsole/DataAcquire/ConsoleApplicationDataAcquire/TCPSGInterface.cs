using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using Modbus.Device;

namespace MeterInterface
{
    class TCPSGInterface//TCP，用于读取电表数据的简单接口类
    {
        public TcpClient client;
        /// <summary>
        /// 构造函数
        /// </summary>
        public TCPSGInterface(String meter_ipaddr)//参数为电表的IP地址
        {
            this.client = new TcpClient(meter_ipaddr, 502);
        }
        /// <summary>
        /// Simple Modbus TCP master read holding registers
        /// Modbus协议函数功能3，读取保持寄存器(16位)
        /// startAddress：起始偏移量（从0开始计数）
        /// numInputs：读取的寄存器个数
        /// holdregs：存放结果用的数组
        /// 读回结果以UINT16为单位存放在数组里(以电表的16位寄存器为单位存放)
        /// </summary>
        public void ReadHoldingRegisters(ushort startAddress, ushort numInputs, ushort[] holdregs)
        {
            ModbusIpMaster master = ModbusIpMaster.CreateIp(client);
            // read read holding registers读保持寄存器（Fun3）
            holdregs = master.ReadHoldingRegisters(startAddress, numInputs);
        }
        /// <summary>
        /// Simple Modbus TCP master read holding registers
        /// Modbus协议函数功能3，读取保持寄存器(16位)
        /// startAddress：起始偏移量（从0开始计数）
        /// numInputs：读取的寄存器个数
        /// holdregs：存放结果用的数组
        /// 读回结果以UINT8为单位存放在数组里(以通信过程中的字节流顺序为单位存放)
        /// </summary>
        public void ReadHoldingRegisters(ushort startAddress, ushort numInputs, byte[] bholdregs)
        {
            ModbusIpMaster master = ModbusIpMaster.CreateIp(client);
            // read read holding registers读保持寄存器（Fun3）
            ushort[] holdregs = master.ReadHoldingRegisters(startAddress, numInputs);
            ushort2byte(holdregs, bholdregs);
        }
        /// <summary>
        /// 析构函数
        /// </summary>
        ~TCPSGInterface()
        {
            client.Close();
        }
        /// <summary>
        /// 关闭TCP连接，断开于电表的通信
        /// </summary>
        public void close()
        {
            client.Close();
        }
        
        /// <summary>
        /// 将UINT16的数组的每一个UINT16分割为两个UINT8，存放到UINT8的数组里面
        /// </summary>
        public void ushort2byte(ushort[] a, byte[] b)
        {
            for (int i = 0; i < a.GetLength(0); i++)
            {
                byte[] c = BitConverter.GetBytes(a[i]);
                b[2 * i] = c[1];
                b[2 * i + 1] = c[0];
            }
        }

    }
}
