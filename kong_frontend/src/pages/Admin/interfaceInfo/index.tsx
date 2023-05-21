import { PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns, ProDescriptionsItemProps } from '@ant-design/pro-components';
import {
  FooterToolbar,
  PageContainer,
  ProDescriptions,
  ProTable,
} from '@ant-design/pro-components';
import { FormattedMessage, useIntl } from '@umijs/max';
import { Button, Drawer, Input, message } from 'antd';
import React, { useRef, useState } from 'react';
import {
  addinterfaceInfoUsingPOST,
  deleteinterfaceInfoUsingPOST,
  listinterfaceInfoByPageUsingPOST,
  onlineInterfaceInfoUsingPOST,
  updateinterfaceInfoUsingPOST,
  updateofflineinterfaceInfoUsingPOST
} from "@/services/kongAPI-backend/interfaceInfoController";
import UpdateModal from "@/pages/Admin/interfaceInfo/components/UpdateModal";
import CreateModal from "@/pages/Admin/interfaceInfo/components/CreateModal";



const TableList: React.FC = () => {
  /**
   * @en-US Pop-up window of new window
   * @zh-CN 新建窗口的弹窗
   *  */
  const [createModalVisible, handleModalVisible] = useState<boolean>(false);
  // const [createModalOpen, handleModalOpen] = useState<boolean>(false);
  /**
   * @en-US The pop-up window of the distribution update window
   * @zh-CN 分布更新窗口的弹窗
   * */
  const [updateModalVisible, handleUpdateModalVisible] = useState<boolean>(false);
  // const [updateModalOpen, handleUpdateModalOpen] = useState<boolean>(false);

  const [showDetail, setShowDetail] = useState<boolean>(false);

  const actionRef = useRef<ActionType>();
  const [currentRow, setCurrentRow] = useState<API.InterfaceInfo>();
  const [selectedRowsState, setSelectedRows] = useState<API.InterfaceInfo[]>([]);




  /**
   * @en-US Add node
   * @zh-CN 添加节点
   * @param fields
   */
  const handleAdd = async (fields: API.InterfaceInfo) => {
    const hide = message.loading('正在添加');
    try {
      await addinterfaceInfoUsingPOST({ ...fields });
      hide();
      message.success('创建成功');
      handleModalVisible(false);
      return true;
    } catch (error: any) {
      hide();
      message.error('创建失败，' + error.message);
      return false;
    }
  };
  /**
   * @en-US Update node
   * @zh-CN 更新节点
   *
   * @param fields
   */
  const handleUpdate = async (fields: API.InterfaceInfo) => {
    if(!currentRow){return;}
    const hide = message.loading('修改中');
    try {
      await updateinterfaceInfoUsingPOST({
        id: currentRow.id,
        ...fields
      });
      hide();
      message.success('修改成功');
      return true;
    } catch (error: any) {
      hide();
      message.error('修改失败，'+ error.message);
      return false;
    }
  };
  /**
   *  online
   * @zh-CN 发布接口
   *
   * @param selectedRows
   */
  const handleOnline = async (record: API.IdRequest) => {
    const hide = message.loading('正在发布');
    if (!record) return true;
    try {
      await onlineInterfaceInfoUsingPOST({
        id: record.id
      });
      hide();
      message.success('发布成功');
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('发布失败，'+error.message);
      return false;
    }
  };
  /**
   *  offline
   * @zh-CN 下线接口
   *
   * @param selectedRows
   */
  const handleOffline = async (record: API.IdRequest) => {
    const hide = message.loading('正在下线');
    if (!record) return true;
    try {
      await updateofflineinterfaceInfoUsingPOST({
        id: record.id
      });
      hide();
      message.success('下线成功');
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('下线失败，'+error.message);
      return false;
    }
  };
  /**
   *  Delete node
   * @zh-CN 删除节点
   *
   * @param selectedRows
   */
  const handleRemove = async (record: API.InterfaceInfo) => {
    const hide = message.loading('正在删除');
    if (!record) return true;
    try {
      await deleteinterfaceInfoUsingPOST({
        id: record.id
      });
      hide();
      message.success('删除成功');
      actionRef.current?.reload();
      return true;
    } catch (error: any) {
      hide();
      message.error('删除失败，'+error.message);
      return false;
    }
  };
  const intl = useIntl();

  const columns: ProColumns<API.RuleListItem>[] = [
    {
      title: 'id',
      dataIndex: 'id',
      valueType: 'index',
    },
    {
      title: "接口名称",
      dataIndex: 'name',
      formItemProps: {
        rules: [{
          required: true,
          message: '请输入接口名称',
        }]
      },
      render: (dom, entity) => {
        return (
          <a
            onClick={() => {
              setCurrentRow(entity);
              setShowDetail(true);
            }}
          >
            {dom}
          </a>
        );
      },
    },
    {
      title: <FormattedMessage id="pages.searchTable.titleDesc" defaultMessage="描述" />,
      dataIndex: 'description',
      valueType: 'textarea',
    },
    {
      title: '请求方法',
      dataIndex: 'method',
      valueType: 'text',
    },
    {
      title: 'url',
      dataIndex: 'url',
      valueType: 'text',
    },
    {
      title: '请求参数',
      dataIndex: 'requestParams',
      valueType: 'text',
    },
    {
      title: '请求头',
      dataIndex: 'requestHeader',
      valueType: 'text',
    },
    {
      title: '响应头',
      dataIndex: 'responseHeader',
      valueType: 'text',
    },
    {
      title: '状态',
      dataIndex: 'status',
      hideInForm: true,
      valueEnum: {
        0: {
          text: '关闭',
          status: 'Default',
        },
        1: {
          text: '开启',
          status: 'Processing',
        },
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      valueType: 'dateTime',
      hideInForm: true,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      valueType: 'dateTime',
      hideInForm: true,
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_,record)=>[
        <a
          key="config"
          onClick={()=>{
            handleUpdateModalVisible(true);
            setCurrentRow(record);
          }}
          >
          修改
        </a>,
        record.status===0 ? <a
          type={"text"}
          key="config"
          onClick={()=>{
            handleOnline(record);
          }}
        >
          发布
        </a> :
        <a
          type={"text"}
          key="config"
          onClick={()=>{
            handleOffline(record);
          }}
        >
          下线
        </a>,
        <Button
          type={"text"}
          key="config"
          danger
          onClick={()=>{
            handleRemove(record);
          }}
        >
          删除
        </Button>,
      ],
    },
  ];

  return (
    <PageContainer>
      <ProTable<API.RuleListItem, API.PageParams>
        headerTitle={'查询表格'}
        actionRef={actionRef}
        rowKey="key"
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              handleModalVisible(true);
            }}
          >
            <PlusOutlined /> <FormattedMessage id="pages.searchTable.new" defaultMessage="新建" />
          </Button>,
        ]}
        // request={async (
        //   params,
        //   sort: Record<string, SortOrder>,
        //   filter: Record<string, React.ReactText[] | null>,
        // ) => {
        //   const res: any = await listinterfaceInfoByPageUsingPOST({
        //     ...params,
        //   });
        //   if (res?.data) {
        //     return {
        //       data: res?.data.records || [],
        //       success: true,
        //       total: res?.data.total || 0,
        //   };
        // }else {
        //     return {
        //       data: [],
        //       success: false,
        //       total: 0,
        //     };
        //   }
        // }}
        request={listinterfaceInfoByPageUsingPOST}
        columns={columns}
        rowSelection={{
          onChange: (_, selectedRows) => {
            setSelectedRows(selectedRows);
          },
        }}
      />
      {selectedRowsState?.length > 0 && (
        <FooterToolbar
          extra={
            <div>
              已选择{' '}
              {/*<FormattedMessage id="pages.searchTable.chosen" defaultMessage="Chosen" />{' '}*/}
              <a style={{ fontWeight: 600 }}>{selectedRowsState.length}</a>{' '}
              <FormattedMessage id="pages.searchTable.item" defaultMessage="项" />
              &nbsp;&nbsp;
              <span>
                服务调用次数
                {selectedRowsState.reduce((pre, item) => pre + item.callNo!, 0)} 万
              </span>
            </div>
          }
        >
          <Button
            onClick={async () => {
              await handleRemove(selectedRowsState);
              setSelectedRows([]);
              actionRef.current?.reloadAndRest?.();
            }}
          >
            <FormattedMessage
              id="pages.searchTable.batchDeletion"
              defaultMessage="批量删除"
            />
          </Button>
          <Button type="primary">
            <FormattedMessage
              id="pages.searchTable.batchApproval"
              defaultMessage="批量提审"
            />
          </Button>
        </FooterToolbar>
      )}

      <UpdateModal
        columns={columns}
        onSubmit={async (value) => {
          const success = await handleUpdate(value);
          if (success) {
            handleUpdateModalVisible(false);
            setCurrentRow(undefined);
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
        onCancel={() => {
          handleUpdateModalVisible(false);
          if (!showDetail) {
            setCurrentRow(undefined);
          }
        }}
        visible={updateModalVisible}
        values={currentRow || {}}
      />
      <CreateModal
        columns={columns}
        onCancel={()=>{
          handleModalVisible(false)
        }
        }
        onSubmit={async (values)=>{
          const res = await handleAdd(values);
          if(res){
            handleModalVisible(false);
            if (actionRef.current) {
              actionRef.current.reload();
            }
          }
        }}
        visible={createModalVisible}/>

      <Drawer
        width={600}
        open={showDetail}
        onClose={() => {
          setCurrentRow(undefined);
          setShowDetail(false);
        }}
        closable={false}
      >
        {currentRow?.name && (
          <ProDescriptions<API.RuleListItem>
            column={2}
            title={currentRow?.name}
            request={async () => ({
              data: currentRow || {},
            })}
            params={{
              id: currentRow?.name,
            }}
            columns={columns as ProDescriptionsItemProps<API.RuleListItem>[]}
          />
        )}
      </Drawer>

    </PageContainer>
  );
};

export default TableList;
