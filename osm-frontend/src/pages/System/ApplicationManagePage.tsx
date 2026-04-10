import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, message, Popconfirm } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import { applicationApi, systemApi } from '@/services/api'
import type { ApplicationVO, CreateApplicationRequest, SystemVO } from '@/services/types'

const ApplicationManagePage: React.FC = () => {
  const [data, setData] = useState<ApplicationVO[]>([])
  const [systems, setSystems] = useState<SystemVO[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [form] = Form.useForm()

  const loadData = async () => {
    setLoading(true)
    try {
      const response = await applicationApi.list()
      setData(response.data)
    } catch (error) {
      // Error handled by interceptor
    } finally {
      setLoading(false)
    }
  }

  const loadSystems = async () => {
    try {
      const response = await systemApi.list()
      setSystems(response.data)
    } catch (error) {
      // Error handled by interceptor
    }
  }

  useEffect(() => {
    loadData()
    loadSystems()
  }, [])

  const handleAdd = () => {
    setEditingId(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: ApplicationVO) => {
    setEditingId(record.id)
    form.setFieldsValue({
      name: record.name,
      description: record.description,
      systemId: record.systemId,
      code: record.code,
    })
    setModalVisible(true)
  }

  const handleDelete = async (id: number) => {
    try {
      await applicationApi.delete(id)
      message.success('删除成功')
      loadData()
    } catch (error) {
      // Error handled by interceptor
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      if (editingId) {
        await applicationApi.update(editingId, values)
        message.success('更新成功')
      } else {
        await applicationApi.create(values as CreateApplicationRequest)
        message.success('创建成功')
      }
      setModalVisible(false)
      loadData()
    } catch (error) {
      // Validation or API error
    }
  }

  const columns: ColumnsType<ApplicationVO> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '应用名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '应用编码',
      dataIndex: 'code',
      key: 'code',
    },
    {
      title: '所属系统ID',
      dataIndex: 'systemId',
      key: 'systemId',
    },
    {
      title: '描述',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_, record) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm title="确认删除？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 16 }}>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新建应用
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
      />

      <Modal
        title={editingId ? '编辑应用' : '新建应用'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={500}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="应用名称"
            rules={[{ required: true, message: '请输入应用名称' }]}
          >
            <Input placeholder="请输入应用名称" />
          </Form.Item>
          <Form.Item
            name="code"
            label="应用编码"
            rules={[{ required: true, message: '请输入应用编码' }]}
          >
            <Input placeholder="请输入应用编码，如 WEB_PORTAL" />
          </Form.Item>
          <Form.Item
            name="systemId"
            label="所属系统"
            rules={[{ required: true, message: '请选择所属系统' }]}
          >
            <Select placeholder="请选择所属系统">
              {systems.map((s) => (
                <Select.Option key={s.id} value={s.id}>
                  {s.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="description" label="描述">
            <Input.TextArea placeholder="请输入描述" rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default ApplicationManagePage
