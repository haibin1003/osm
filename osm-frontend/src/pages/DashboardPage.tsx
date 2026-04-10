import React from 'react'
import { Card, Row, Col, Statistic } from 'antd'
import { UserOutlined, SafetyCertificateOutlined, DatabaseOutlined, AppstoreOutlined } from '@ant-design/icons'
import { useAuth } from '@/context/AuthContext'

const DashboardPage: React.FC = () => {
  const { user } = useAuth()

  return (
    <div style={{ padding: 24 }}>
      <h1 style={{ marginBottom: 24 }}>欢迎回来，{user?.username}</h1>

      <Row gutter={16}>
        <Col span={6}>
          <Card>
            <Statistic
              title="用户总数"
              value={0}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="角色总数"
              value={0}
              prefix={<SafetyCertificateOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="域总数"
              value={0}
              prefix={<DatabaseOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="应用总数"
              value={0}
              prefix={<AppstoreOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={16} style={{ marginTop: 24 }}>
        <Col span={24}>
          <Card title="快速开始">
            <p>• 使用左侧导航访问各个功能模块</p>
            <p>• 管理人员可以管理系统架构、开源软件库</p>
            <p>• 开发人员可以提交订购申请、登记使用记录</p>
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default DashboardPage
