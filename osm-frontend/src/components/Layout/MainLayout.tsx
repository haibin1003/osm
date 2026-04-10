import { Layout, Menu, Dropdown, Avatar, Space } from 'antd'
import {
  DashboardOutlined,
  UserOutlined,
  TeamOutlined,
  DatabaseOutlined,
  AppstoreOutlined,
  CloudOutlined,
  ShoppingOutlined,
  FileTextOutlined,
  BarChartOutlined,
  LogoutOutlined,
} from '@ant-design/icons'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '@/context/AuthContext'

const { Header, Sider, Content } = Layout

const menuItems = [
  {
    key: '/',
    icon: <DashboardOutlined />,
    label: '首页',
  },
  {
    key: '/system',
    icon: <DatabaseOutlined />,
    label: '系统架构',
    children: [
      {
        key: '/system/domains',
        icon: <DatabaseOutlined />,
        label: '域管理',
      },
      {
        key: '/system/systems',
        icon: <AppstoreOutlined />,
        label: '系统管理',
      },
      {
        key: '/system/applications',
        icon: <CloudOutlined />,
        label: '应用管理',
      },
    ],
  },
  {
    key: '/system/users',
    icon: <TeamOutlined />,
    label: '用户管理',
  },
  {
    key: '/system/roles',
    icon: <UserOutlined />,
    label: '角色管理',
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
  const { user, logout } = useAuth()

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key)
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const userMenuItems = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      danger: true,
    },
  ]

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider theme="light" width={220}>
        <div style={{ height: 64, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <h2 style={{ margin: 0, color: '#1890ff' }}>OSM</h2>
        </div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          defaultOpenKeys={['/system']}
          items={menuItems}
          onClick={handleMenuClick}
          style={{ height: 'calc(100vh - 64px)', overflowY: 'auto' }}
        />
      </Sider>
      <Layout>
        <Header style={{ background: '#fff', padding: '0 24px', display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
          <Dropdown menu={{ items: userMenuItems, onClick: handleLogout }}>
            <Space style={{ cursor: 'pointer' }}>
              <Avatar icon={<UserOutlined />} />
              <span>{user?.username}</span>
            </Space>
          </Dropdown>
        </Header>
        <Content style={{ margin: 0, padding: 0, background: '#f0f2f5', minHeight: 'calc(100vh - 64px)' }}>
          <div style={{ padding: 24 }}>
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  )
}
