using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace MeterInterface
{
    /// <summary>
    /// 数据类型的转换
    /// </summary>
    class CTConver
    { 
        /// <summary>
        /// byte 数组变为float 数组
        /// </summary>
        /// <param name="srcByte"></param>
        /// <returns></returns>
        public float[] ConvertToFloatArray(byte[] srcByte)
        {
            unsafe
            {
                int FLOATLEN = sizeof(float);
                int srcLen = srcByte.Length;
                int dstLen = srcLen / FLOATLEN;
                float[] dstFloat = new float[dstLen];

                byte[] srcByteTemp = new byte[srcLen];
                for (int i = 0; i < srcLen; i++)
                    srcByteTemp[i] = srcByte[srcLen - i - 1];
                srcByte = srcByteTemp;

                    for (int i = 0; i < dstLen; i++)
                    {
                        float temp = 0.0F;
                        void* pf = &temp;
                        fixed (byte* pxb = srcByte)
                        {
                            byte* px = pxb;
                            px += i * FLOATLEN;

                            for (int j = 0; j < FLOATLEN; j++)
                            {
                                *((byte*)pf + j) = *(px + j);
                            }
                            dstFloat[i] = temp;
                        }
                    }
                return dstFloat;
            }
            
        }
        
        /// <summary>
        /// byte 数组变为double数组
        /// </summary>
        /// <param name="srcByte"></param>
        /// <returns></returns>
        public double[] ConvertToDoubleArray(byte[] srcByte)
        {
            byte[] dstByte = new byte[srcByte.Length];
            double[] dstDouble = new double[srcByte.Length / 8];
            for (int i = 0; i < srcByte.Length; i++)
                dstByte[i] = srcByte[srcByte.Length - 1 - i];

            for (int i = 0; i < srcByte.Length / 8; i++)
                dstDouble[i]=BitConverter.ToDouble(dstByte,i*8);
            return dstDouble;
        }

        
        /// <summary>
        /// byte+byte
        /// </summary>
        /// <param name="x1"></param>
        /// <param name="x2"></param>
        /// <returns></returns>
        public byte[] addByte(byte[] x1, byte[] x2)
        {
            byte[] result = new byte[x1.Length + x2.Length];
            for (int i = 0; i < x1.Length; i++)
                result[i] = x1[i];
            for (int i = x1.Length; i < x1.Length + x2.Length; i++)
                result[i] = x2[i - x1.Length];
            return result;
        }
    }
}
