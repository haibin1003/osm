import React, { useState, useEffect } from 'react'
import { Table, Button, Space, Modal, Form, Input, Select, message, Popconfirm } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import { systemApi, domainApi } from '@/services/api'
import type { SystemVO, CreateSystemRequest, DomainVO } from '@/services/types'

const SystemManagePage: React.FC = () => {
  const [data, setData] = useState<SystemVO[]>([])
  const [domains, setDomains] = useState<DomainVO[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [form] = Form.useForm()

  const loadData = async () => {
    setLoading(true)
    try {
      const response = await systemApi.list()
      setData(response.data)
    } catch (error) {
      // Error handled by interceptor
    } finally {
      setLoading(false)
    }
  }

  const loadDomains = async () => {
    try {
      const response = await domainApi.list()
      setDomains(response.data)
    } catch (error) {
      // Error handled by interceptor
    }
  }

  useEffect(() => {
    loadData()
    loadDomains()
  }, [])

  const handleAdd = () => {
    setEditingId(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: SystemVO) => {
    setEditingId(record.id)
    form.setFieldsValue({
      name: record.name,
      description: record.description,
      domainId: record.domainId,
      code: record.code,
    })
    setModalVisible(true)
  }

  const handleDelete = async (id: number) => {
    try {
      await systemApi.delete(id)
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
        await systemApi.update(editingId, values)
        message.success('更新成功')
      } else {
        await systemApi.create(values as CreateSystemRequest)
        message.success('创建成功')
      }
      setModalVisible(false)
      loadData()
    } catch (error) {
      // Validation or API error
    }
  }

  const columns: ColumnsType<SystemVO> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '系统名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '系统编码',
      dataIndex: 'code',
      key: 'code',
    },
    {
      title: '所属域ID',
      dataIndex: 'domainId',
      key: 'domainId',
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
          新建系统
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
      />

      <Modal
        title={editingId ? '编辑系统' : '新建系统'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={500}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="系统名称"
            rules={[{ required: true, message: '请输入系统名称' }]}
          >
            <Input placeholder="请输入系统名称" />
          </Form.Item>
          <Form.Item
            name="code"
            label="系统编码"
            rules={[{ required: true, message: '请输入系统编码' }]}
          >
            <Input placeholder="请输入系统编码，如 ORDER_SYS" />
          </Form.Item>
          <Form.Item
            name="domainId"
            label="所属域"
            rules={[{ required: true, message: '请选择所属域' }]}
          >
            <Select placeholder="请选择所属域">
              {domains.map((d) => (
                <Select.Option key={d.id} value={d.id}>
                  {d.name}
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

export default SystemManagePage
