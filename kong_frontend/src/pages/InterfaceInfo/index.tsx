import { PageContainer } from '@ant-design/pro-components';
import React, { useEffect, useState } from 'react';
import {Button, Card, Descriptions, Form, message, Input, Divider} from 'antd';
import {
  getInterfaceInfoByIdUsingGET, invokeinterfaceInfoUsingPOST,
} from '@/services/kongAPI-backend/interfaceInfoController';
import { useParams } from '@@/exports';
import moment from "moment";


/**
 * 查看接口信息
 * @constructor
 */
const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<API.InterfaceInfo>();
  const [invokeRes, setInvokeRes] = useState<any>();
  const [invokeLoading, setInvokeLoading] = useState(false);

  const formatterTime = (val: any) => {
    return val ? moment(val).format('YYYY-MM-DD HH:mm:ss') : ''
  }

  //umi组件:获取url中参数id，动态路由加载不同接口信息
  const params = useParams();

  const loadData = async () => {
    if (!params.id) {
      message.error('参数不存在');
      return;
    }
    setLoading(true);
    try {
      const res = await getInterfaceInfoByIdUsingGET({
        id: Number(params.id),
      });
      setData(res.data);
    } catch (error: any) {
      message.error('请求失败，' + error.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  const onFinish = async (values: any) => {
    if (!params.id) {
      message.error('接口不存在');
      return;
    }
    setInvokeLoading(true);
    try {
      const res=await invokeinterfaceInfoUsingPOST({
        id: params.id,
        ...values
      })
      setInvokeRes(res.data);
      message.success('请求成功');
    }catch (error: any){
      message.error('操作失败，' + error.message);
    }
    setInvokeLoading(false);
  };


  return (
    <PageContainer title="接口文档">
      <Card>
        {data ? (
          <Descriptions title={data.name} column={1}>
            <Descriptions.Item label="接口状态">{data.status ? '正常' : '关闭'}</Descriptions.Item>
            <Descriptions.Item label="描述">{data.description}</Descriptions.Item>
            <Descriptions.Item label="请求地址">{data.url}</Descriptions.Item>
            <Descriptions.Item label="请求方法">{data.method}</Descriptions.Item>
            <Descriptions.Item label="请求参数">{data.requestParams}</Descriptions.Item>
            <Descriptions.Item label="请求头">{data.requestHeader}</Descriptions.Item>
            <Descriptions.Item label="响应头">{data.responseHeader}</Descriptions.Item>
            <Descriptions.Item label="创建时间">{formatterTime(data.createTime)}</Descriptions.Item>
            <Descriptions.Item label="更新时间">{formatterTime(data.updateTime)}</Descriptions.Item>
          </Descriptions>
        ) : (
          <>接口不存在</>
        )}
      </Card>
      <Divider />
       <Card title="在线测试">
         <Form
           name="invoke"
           style={{ maxWidth: 600 }}
           onFinish={onFinish}
           autoComplete="off"
         >
           <Form.Item
             label="请求参数"
             name="userRequestParams"
           >
             <Input.TextArea/>
           </Form.Item>
           <Form.Item wrapperCol={{ span: 16 }}>
             <Button type="primary" htmlType="submit">
               发送
             </Button>
           </Form.Item>
         </Form>
       </Card>
      <Divider />
      <Card title="返回结果" loading={invokeLoading}>
        {invokeRes}
      </Card>
    </PageContainer>
  );
};

export default Index;
