using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ModbusTestForm
{
    /// <summary>
    /// 对SmartMeterDataInfo赋值
    /// </summary>

    class CSmartMeterData
    {
        public CSmartMeterDataInfo smartMeterData = new CSmartMeterDataInfo();

        /// <summary>
        /// 构造函数
        /// </summary>
        /// <param name="voltage_Vph_n"></param>
        /// <param name="current"></param>
        /// <param name="apparent_Power"></param>
        /// <param name="active_Power"></param>
        /// <param name="reactive_Power"></param>
        /// <param name="power_Factor"></param>

        public CSmartMeterData(byte[] voltage_Vph_n, byte[] voltage_Vph_ph, byte[] current, byte[] active_Power, byte[] active_Energy)
        {
            getVoltage_Vph_n(voltage_Vph_n);
            getVoltage_Vph_ph(voltage_Vph_ph);
            getCurrent(current);
            getActive_Power(active_Power);
            getActive_Energy(active_Energy);
        }

        public CSmartMeterData(byte[] voltage_Vph_n,/* byte[] voltage_Vph_ph, */byte[] current,byte[] apparent_Power, byte[] active_Power,byte[] reactive_Power,byte[] power_Factor/*, byte[] active_Energy*/)
        {
            getVoltage_Vph_n(voltage_Vph_n);
            //getVoltage_Vph_ph(voltage_Vph_ph);
            getCurrent(current);
            getApparent_Power(apparent_Power);
            getActive_Power(active_Power);
            getReactive_Power(reactive_Power);
            getPower_Factor(power_Factor);                  
        }

        public CSmartMeterData(byte[] voltage_Vph_n, byte[] voltage_Vph_ph, byte[] current, byte[] apparent_Power, byte[] active_Power, byte[] reactive_Power, byte[] power_Factor, byte[] active_Energy)
        {
            getVoltage_Vph_n(voltage_Vph_n);
            getVoltage_Vph_ph(voltage_Vph_ph);
            getCurrent(current);
            getApparent_Power(apparent_Power);
            getActive_Power(active_Power);
            getReactive_Power(reactive_Power);
            getPower_Factor(power_Factor);
            getActive_Energy(active_Energy);
        }

        public void modifyData(byte[] voltage_Vph_n, byte[] voltage_Vph_ph, byte[] current, byte[] apparent_Power, byte[] active_Power, byte[] reactive_Power, byte[] power_Factor, byte[] active_Energy)
        {
            getVoltage_Vph_n(voltage_Vph_n);
            getVoltage_Vph_ph(voltage_Vph_ph);
            getCurrent(current);
            getApparent_Power(apparent_Power);
            getActive_Power(active_Power);
            getReactive_Power(reactive_Power);
            getPower_Factor(power_Factor);
            getActive_Energy(active_Energy);
        }
        public void modifyData(byte[] voltage_Vph_n, byte[] voltage_Vph_ph, byte[] current, byte[] active_Power,  byte[] active_Energy)
        {
            getVoltage_Vph_n(voltage_Vph_n);
            getVoltage_Vph_ph(voltage_Vph_ph);
            getCurrent(current);
            getActive_Power(active_Power);
            getActive_Energy(active_Energy);
        }

        public void modifyData(byte[] voltage_Vph_n,/* byte[] voltage_Vph_ph, */byte[] current, byte[] apparent_Power, byte[] active_Power, byte[] reactive_Power, byte[] power_Factor/*, byte[] active_Energy*/)
        {
            getVoltage_Vph_n(voltage_Vph_n);
            //getVoltage_Vph_ph(voltage_Vph_ph);
            getCurrent(current);
            getApparent_Power(apparent_Power);
            getActive_Power(active_Power);
            getReactive_Power(reactive_Power);
            getPower_Factor(power_Factor);
            
        }

        public void getVoltage_Vph_n(byte[] voltage_Vph_n)
        {
            CTConver conver = new CTConver();
            float[] temp = new float[4];
            temp = conver.ConvertToFloatArray(voltage_Vph_n);
            smartMeterData.Voltage_Va_n_1 = temp[3];
            smartMeterData.Voltage_Vb_n_3 = temp[2];
            smartMeterData.Voltage_Vc_n_5 = temp[1];
            smartMeterData.Average_Voltage_Vph_n_57 = temp[0];
        }

        public void getVoltage_Vph_ph(byte[] voltage_Vph_ph)
        {
            CTConver conver = new CTConver();
            float[] temp = new float[4];
            temp = conver.ConvertToFloatArray(voltage_Vph_ph);
            smartMeterData.Voltage_Va_b_7 = temp[3];
            smartMeterData.Voltage_Vb_c_9 = temp[2];
            smartMeterData.Voltage_Vc_a_11 = temp[1];
            smartMeterData.Average_Voltage_Vph_ph_59 = temp[0];
        }

        public void getCurrent(byte[] current)
        {
            CTConver conver = new CTConver();
            float[] temp = new float[4];
            temp = conver.ConvertToFloatArray(current);
            smartMeterData.Current_a_13 = temp[3];
            smartMeterData.Current_b_15 = temp[2];
            smartMeterData.Current_c_17 = temp[1];
            smartMeterData.Average_current_295 = temp[0];
        }

        public void getApparent_Power(byte[] apparent_Power)
        {
            CTConver conver = new CTConver();
            float[] temp = new float[4];
            temp = conver.ConvertToFloatArray(apparent_Power);
            smartMeterData.Apparent_Power_a_19 = temp[3];
            smartMeterData.Apparent_Power_b_21 = temp[2];
            smartMeterData.Apparent_Power_c_23 = temp[1];
            smartMeterData.Apparent_Power_Total_63 = temp[0];
        }

        public void getActive_Power(byte[] active_Power)
        {
            CTConver conver = new CTConver();
            float[] temp = new float[4];
            temp = conver.ConvertToFloatArray(active_Power);
            smartMeterData.Active_Power_a_25 = temp[3] * (-1);
            smartMeterData.Active_Power_b_27 = temp[2] * (-1);
            smartMeterData.Active_Power_c_29 = temp[1] * (-1);
            smartMeterData.Total_Active_Power_65 = temp[0] * (-1);
        }

        public void getReactive_Power(byte[] reactive_Power)
        {
            CTConver conver = new CTConver();
            float[] temp = new float[4];
            temp = conver.ConvertToFloatArray(reactive_Power);
            smartMeterData.Reactive_Power_a_31 = temp[3];
            smartMeterData.Reactive_Power_b_33 = temp[2];
            smartMeterData.Reactive_Power_c_35 = temp[1];
            smartMeterData.Reactive_Power_Total_67 = temp[0];
        }

        public void getPower_Factor(byte[] power_Factor)
        {
            CTConver conver = new CTConver();
            float[] temp = new float[4];
            temp = conver.ConvertToFloatArray(power_Factor);
            smartMeterData.Power_Factor_a_37 = temp[3];
            smartMeterData.Power_Factor_b_39 = temp[2];
            smartMeterData.Power_Factor_c_41 = temp[1];
            smartMeterData.Power_Factor_Total_69 = temp[0];
        }

        public void getActive_Energy(byte[] active_Energy)
        {
            CTConver conver = new CTConver();
            double[] tempD = new double[4];
            tempD = conver.ConvertToDoubleArray(active_Energy);
            smartMeterData.Active_Energy_Import_Tariff_1_801 = tempD[3];
            smartMeterData.Active_Energy_Import_Tariff_2_805 = tempD[2];
            smartMeterData.Active_Energy_Export_Tariff_1_809 = tempD[1];
            smartMeterData.Active_Energy_Export_Tariff_2_813 = tempD[0];
        }
    }
}
