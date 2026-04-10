import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from '@/context/AuthContext'
import MainLayout from '@/components/Layout/MainLayout'
import LoginPage from '@/pages/Auth/LoginPage'
import DashboardPage from '@/pages/DashboardPage'
import UserManagePage from '@/pages/System/UserManagePage'
import RoleManagePage from '@/pages/System/RoleManagePage'
import DomainManagePage from '@/pages/System/DomainManagePage'
import SystemManagePage from '@/pages/System/SystemManagePage'
import ApplicationManagePage from '@/pages/System/ApplicationManagePage'

// Protected Route wrapper
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth()
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />
}

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<DashboardPage />} />
        <Route path="system/users" element={<UserManagePage />} />
        <Route path="system/roles" element={<RoleManagePage />} />
        <Route path="system/domains" element={<DomainManagePage />} />
        <Route path="system/systems" element={<SystemManagePage />} />
        <Route path="system/applications" element={<ApplicationManagePage />} />
      </Route>
    </Routes>
  )
}

export default App
