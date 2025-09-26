import axios from 'axios'
import { ElMessage } from 'element-plus'

// 服务端口映射
const servicePortMap = {
  '/user': '8081',
  '/flower': '8082',
  '/log': '8086'
}

// 获取服务端口
const getServicePort = (url: string): string => {
  for (const [path, port] of Object.entries(servicePortMap)) {
    if (url.startsWith(path)) {
      return port
    }
  }
  return '8081' // 默认用户服务
}

// 创建axios实例
const request = axios.create({
  timeout: 5000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 根据URL路径动态设置baseURL
    const port = getServicePort(config.url || '')
    config.baseURL = `http://localhost:${port}/api`

    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const { data } = response
    if (data.code === 200) {
      return data.data
    } else {
      ElMessage.error(data.message || '请求失败')
      return Promise.reject(new Error(data.message))
    }
  },
  error => {
    console.error('Request error details:', {
      message: error.message,
      response: error.response,
      status: error.response?.status,
      data: error.response?.data
    })

    // 处理不同的错误状态码
    if (error.response) {
      const { status, data } = error.response
      let errorMessage = '未知错误'

      switch (status) {
        case 400:
          // 业务异常，显示具体错误信息
          errorMessage = data?.message || data?.error || '请求参数错误'
          console.log('400 error details:', { errorMessage, status, data })
          ElMessage.error(errorMessage)
          // 创建一个带有 isHandled 标记的错误，避免Login组件重复处理
          const handledError = new Error(errorMessage) as any
          handledError.isHandled = true
          return Promise.reject(handledError)

        case 401:
          errorMessage = '登录已过期，请重新登录'
          localStorage.removeItem('token')
          ElMessage.error(errorMessage)
          window.location.href = '/login'
          return Promise.reject(new Error(errorMessage))

        case 403:
          errorMessage = '权限不足'
          ElMessage.error(errorMessage)
          return Promise.reject(new Error(errorMessage))

        case 404:
          errorMessage = '请求的资源不存在'
          ElMessage.error(errorMessage)
          return Promise.reject(new Error(errorMessage))

        case 500:
          errorMessage = '服务器内部错误'
          ElMessage.error(errorMessage)
          return Promise.reject(new Error(errorMessage))

        default:
          errorMessage = data?.message || data?.error || `请求失败 (${status})`
          ElMessage.error(errorMessage)
          return Promise.reject(new Error(errorMessage))
      }
    } else if (error.request) {
      // 网络错误
      const errorMessage = '网络连接失败，请检查网络设置'
      ElMessage.error(errorMessage)
      return Promise.reject(new Error(errorMessage))
    } else {
      const errorMessage = error.message || '未知错误'
      ElMessage.error(errorMessage)
      return Promise.reject(new Error(errorMessage))
    }
  }
)

export default request