import {
  PageContainer,
} from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import ReactECharts from 'echarts-for-react';
import {listTopInvokeInterfaceInfoUsingGET} from "@/services/kongAPI-backend/analysisController";
import {message} from "antd";


const InterfaceAnalysis: React.FC = () => {

  const [data, setData] = useState<API.InterfaceInfoVO[]>([]);

  const loadData = async ()=>{
    const res=await listTopInvokeInterfaceInfoUsingGET();
    if(res.data){
      setData(res.data);
    }
  }
  //deps为空时，加载页面时就调用一次；否则，deps修改就调用这个方法
  useEffect(()=>{
    try {
      loadData();
    }catch (error: any){
      message.error('加载失败，'+ error.message);
    }
  },[])

  const chartData = data.map(item => {
    return {
      value: item.totalNum,
      name: item.name,
    }
  })



  const option = {
    title: {
      text: '接口调用统计图',
      left: 'center'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '调用次数',
        type: 'pie',
        radius: '50%',
        data: chartData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };


  return (
    <PageContainer>
      <ReactECharts option={option} />
    </PageContainer>
  );
};

export default InterfaceAnalysis;
