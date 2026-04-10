import { Layout, Menu } from 'antd'
import {
  DashboardOutlined,
  AppstoreOutlined,
  CloudOutlined,
  ShoppingOutlined,
  FileTextOutlined,
  BarChartOutlined,
} from '@ant-design/icons'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'

const { Header, Sider, Content } = Layout

const menuItems = [
  {
    key: '/',
    icon: <DashboardOutlined />,
    label: '首页',
  },
  {
    key: '/systems',
    icon: <AppstoreOutlined />,
    label: '系统管理',
  },
  {
    key: '/software',
    icon: <CloudOutlined />,
    label: '软件库',
  },
  {
    key: '/orders',
    icon: <ShoppingOutlined />,
    label: '订购管理',
  },
  {
    key: '/usage',
    icon: <FileTextOutlined />,
    label: '使用登记',
  },
  {
    key: '/statistics',
    icon: <BarChartOutlined />,
    label: '统计分析',
  },
]

export default function MainLayout() {
  const navigate = useNavigate()
  const location = useLocation()

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider theme="light">
        <div style={{ height: 64, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <h2 style={{ margin: 0 }}>OSM</h2>
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ background: '#fff', padding: '0 24px' }}>
          <h3 style={{ margin: 0 }}>开源软件治理平台</h3>
        </Header>
        <Content style={{ margin: 24, padding: 24, background: '#fff' }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}
