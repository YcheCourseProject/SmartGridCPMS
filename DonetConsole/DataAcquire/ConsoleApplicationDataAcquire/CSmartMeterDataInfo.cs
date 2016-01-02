using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ModbusTestForm
{
    /// <summary>
    /// 电表数据
    /// </summary>
    class CSmartMeterDataInfo
    {
        //相电压
        private float voltage_Va_n_1;
        private float voltage_Vb_n_3;
        private float voltage_Vc_n_5;
        private float average_Voltage_Vph_n_57;

        //线电压
        private float voltage_Va_b_7;
        private float voltage_Vb_c_9;
        private float voltage_Vc_a_11;
        private float average_Voltage_Vph_ph_59;

        //电流
        private float current_a_13;
        private float current_b_15;
        private float current_c_17;
        private float average_current_295;

        //视在功率
        private float apparent_Power_a_19;
        private float apparent_Power_b_21;
        private float apparent_Power_c_23;
        private float apparent_Power_Total_63;

        //有功功率
        private float active_Power_a_25;
        private float active_Power_b_27;
        private float active_Power_c_29;
        private float total_Active_Power_65;

        //无功功率
        private float reactive_Power_a_31;
        private float reactive_Power_b_33;
        private float reactive_Power_c_35;
        private float reactive_Power_Total_67;

        //功率因数
        private float power_Factor_a_37;
        private float power_Factor_b_39;
        private float power_Factor_c_41;
        private float power_Factor_Total_69;

        //有功电能
        private double active_Energy_Import_Tariff_1_801;
        private double active_Energy_Import_Tariff_2_805;
        private double active_Energy_Export_Tariff_1_809;
        private double active_Energy_Export_Tariff_2_813;
        
        //时间
        private DateTime time;

        public DateTime Time
        {
            get { return time; }
            set { time = value; }
        }
        public float Voltage_Va_n_1
        {
            get { return voltage_Va_n_1; }
            set { voltage_Va_n_1 = value; }
        }

        public float Voltage_Vb_n_3
        {
            get { return voltage_Vb_n_3; }
            set { voltage_Vb_n_3 = value; }
        }

        public float Voltage_Vc_n_5
        {
            get { return voltage_Vc_n_5; }
            set { voltage_Vc_n_5 = value; }
        }

        public float Average_Voltage_Vph_n_57
        {
            get { return average_Voltage_Vph_n_57; }
            set { average_Voltage_Vph_n_57 = value; }
        }

        public float Voltage_Va_b_7
        {
            get { return voltage_Va_b_7; }
            set { voltage_Va_b_7 = value; }
        }

        public float Voltage_Vb_c_9
        {
            get { return voltage_Vb_c_9; }
            set { voltage_Vb_c_9 = value; }
        }

        public float Voltage_Vc_a_11
        {
            get { return voltage_Vc_a_11; }
            set { voltage_Vc_a_11 = value; }
        }

        public float Average_Voltage_Vph_ph_59
        {
            get { return average_Voltage_Vph_ph_59; }
            set { average_Voltage_Vph_ph_59 = value; }
        }


        public float Current_a_13
        {
            get { return current_a_13; }
            set { current_a_13 = value; }
        }

        public float Current_b_15
        {
            get { return current_b_15; }
            set { current_b_15 = value; }
        }

        public float Current_c_17
        {
            get { return current_c_17; }
            set { current_c_17 = value; }
        }

        public float Average_current_295
        {
            get { return average_current_295; }
            set { average_current_295 = value; }
        }

        public float Apparent_Power_a_19
        {
            get { return apparent_Power_a_19; }
            set { apparent_Power_a_19 = value; }
        }

        public float Apparent_Power_b_21
        {
            get { return apparent_Power_b_21; }
            set { apparent_Power_b_21 = value; }
        }

        public float Apparent_Power_c_23
        {
            get { return apparent_Power_c_23; }
            set { apparent_Power_c_23 = value; }
        }


        public float Apparent_Power_Total_63
        {
            get { return apparent_Power_Total_63; }
            set { apparent_Power_Total_63 = value; }
        }


        public float Active_Power_a_25
        {
            get { return active_Power_a_25; }
            set { active_Power_a_25 = value; }
        }

        public float Active_Power_b_27
        {
            get { return active_Power_b_27; }
            set { active_Power_b_27 = value; }
        }

        public float Active_Power_c_29
        {
            get { return active_Power_c_29; }
            set { active_Power_c_29 = value; }
        }

        public float Total_Active_Power_65
        {
            get { return total_Active_Power_65; }
            set { total_Active_Power_65 = value; }
        }
        public float Reactive_Power_a_31
        {
            get { return reactive_Power_a_31; }
            set { reactive_Power_a_31 = value; }
        }

        public float Reactive_Power_b_33
        {
            get { return reactive_Power_b_33; }
            set { reactive_Power_b_33 = value; }
        }

        public float Reactive_Power_c_35
        {
            get { return reactive_Power_c_35; }
            set { reactive_Power_c_35 = value; }
        }


        public float Reactive_Power_Total_67
        {
            get { return reactive_Power_Total_67; }
            set { reactive_Power_Total_67 = value; }
        }


        public float Power_Factor_a_37
        {
            get { return power_Factor_a_37; }
            set { power_Factor_a_37 = value; }
        }

        public float Power_Factor_b_39
        {
            get { return power_Factor_b_39; }
            set { power_Factor_b_39 = value; }
        }

        public float Power_Factor_c_41
        {
            get { return power_Factor_c_41; }
            set { power_Factor_c_41 = value; }
        }


        public float Power_Factor_Total_69
        {
            get { return power_Factor_Total_69; }
            set { power_Factor_Total_69 = value; }
        }

        public double Active_Energy_Import_Tariff_1_801
        {
            get { return active_Energy_Import_Tariff_1_801; }
            set { active_Energy_Import_Tariff_1_801 = value; }
        }

        public double Active_Energy_Import_Tariff_2_805
        {
            get { return active_Energy_Import_Tariff_2_805; }
            set { active_Energy_Import_Tariff_2_805 = value; }
        }

        public double Active_Energy_Export_Tariff_1_809
        {
            get { return active_Energy_Export_Tariff_1_809; }
            set { active_Energy_Export_Tariff_1_809 = value; }
        }

        public double Active_Energy_Export_Tariff_2_813
        {
            get { return active_Energy_Export_Tariff_2_813; }
            set { active_Energy_Export_Tariff_2_813 = value; }
        }
    }
}
