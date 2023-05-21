import Footer from '@/components/Footer';
import { getFakeCaptcha } from '@/services/ant-design-pro/login';
import {
  LockOutlined,
  UserOutlined,
} from '@ant-design/icons';
import {
  LoginForm,
  ProFormText,
} from '@ant-design/pro-components';
import { useEmotionCss } from '@ant-design/use-emotion-css';
import { FormattedMessage, history, SelectLang, useIntl, useModel, Helmet } from '@umijs/max';
import { Alert, message, Tabs } from 'antd';
import Settings from '../../../../config/defaultSettings';
import React, { useState } from 'react';
import { userRegisterUsingPOST} from "@/services/kongAPI-backend/userController";


const Lang = () => {
  const langClassName = useEmotionCss(({ token }) => {
    return {
      width: 42,
      height: 42,
      lineHeight: '42px',
      position: 'fixed',
      right: 16,
      borderRadius: token.borderRadius,
      ':hover': {
        backgroundColor: token.colorBgTextHover,
      },
    };
  });

  return (
    <div className={langClassName} data-lang>
      {SelectLang && <SelectLang />}
    </div>
  );
};

// const RegisterMessage: React.FC<{
//   content: string;
// }> = ({ content }) => {
//   return (
//     <Alert
//       style={{
//         marginBottom: 24,
//       }}
//       message={content}
//       type="error"
//       showIcon
//     />
//   );
// };

const Register: React.FC = () => {

  const [type, setType] = useState<string>('account');

  const containerClassName = useEmotionCss(() => {
    return {
      display: 'flex',
      flexDirection: 'column',
      height: '100vh',
      overflow: 'auto',
      backgroundImage:
        "url('https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/V-_oS6r-i7wAAAAAAAAAAAAAFl94AQBr')",
      backgroundSize: '100% 100%',
    };
  });

  const intl = useIntl();

  const handleSubmit = async (values: API.UserRegisterRequest) => {

    const {checkPassword, userPassword} = values;
    if (checkPassword !== userPassword){
      message.error("两次输入密码不一致！");
      return;
    }

    try {
      // 注册
      const res = await userRegisterUsingPOST({ ...values});
      if (res.data) {
        const defaultRegisterSuccessMessage = intl.formatMessage({
          id: 'pages.register.success',
          defaultMessage: '注册成功！',
        });
        message.success(defaultRegisterSuccessMessage);
        if (!history) return;
        const {query} = history.location;
        history.push({
          pathname: '/user/login',
          query,
          }
        );
        return;
      }
      // 如果失败去设置用户错误信息

    } catch (error) {
      const defaultRegisterFailureMessage = intl.formatMessage({
        id: 'pages.register.failure',
        defaultMessage: '注册失败，请重试！',
      });
      console.log(error);
      message.error(defaultRegisterFailureMessage);
    }
  };


  return (
    <div className={containerClassName}>
      <Helmet>
        <title>
          {intl.formatMessage({
            id: 'menu.login',
            defaultMessage: '注册页',
          })}
          - {Settings.title}
        </title>
      </Helmet>
      <Lang />
      <div
        style={{
          flex: '1',
          padding: '32px 0',
        }}
      >
        <LoginForm
          submitter={
            {
              searchConfig: {
                submitText: '注册'
              }
            }
          }
          contentStyle={{
            minWidth: 280,
            maxWidth: '75vw',
          }}
          logo={<img alt="logo" src="/logo.svg" />}
          title="API开放平台"
          onFinish={async (values) => {
            await handleSubmit(values as API.UserRegisterRequest);
          }}
        >
          <Tabs
            activeKey={type}
            onChange={setType}
            centered
            items={[
              {
                key: 'account',
                label: intl.formatMessage({
                  id: 'pages.register.accountRegister.tab',
                  defaultMessage: '注册用户',
                }),
              },
            ]}
          />

          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined />,
                }}
                placeholder={intl.formatMessage({
                  id: 'pages.register.userAccount.placeholder',
                  defaultMessage: '用户名',
                })}
                rules={[
                  {
                    required: true,
                    message: (
                      <FormattedMessage
                        id="pages.register.userAccount.required"
                        defaultMessage="请输入用户名!"
                      />
                    ),
                  },
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder={intl.formatMessage({
                  id: 'pages.register.userPassword.placeholder',
                  defaultMessage: '密码',
                })}
                rules={[
                  {
                    required: true,
                    message: (
                      <FormattedMessage
                        id="pages.register.userPassword.required"
                        defaultMessage="请输入密码！"
                      />
                    ),
                  },
                ]}
              />
              <ProFormText.Password
                name="checkPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined />,
                }}
                placeholder={intl.formatMessage({
                  id: 'pages.register.checkPassword.placeholder',
                  defaultMessage: '确认密码',
                })}
                rules={[
                  {
                    required: true,
                    message: (
                      <FormattedMessage
                        id="pages.register.checkPassword.required"
                        defaultMessage="请输入确认密码！"
                      />
                    ),
                  },
                ]}
              />
            </>
          )}
        </LoginForm>
      </div>
      <Footer />
    </div>
  );
};

export default Register;
