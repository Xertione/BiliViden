import { apiClient } from '../../api/client';

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface User {
  userId: number;
  username: string;
  nickname: string;
  status: string;
}

export interface LoginResponse {
  token: string;
}

export interface RegisterRequest {
  username: string;
  password?: string;
  nickname: string;
}

export interface LoginRequest {
  username: string;
  password?: string;
}

export const authApi = {
  register: async (data: RegisterRequest) => {
    const response = await apiClient.post<ApiResponse<string>>('/auth/register', data);
    return response.data;
  },

  login: async (data: LoginRequest) => {
    const response = await apiClient.post<ApiResponse<LoginResponse>>('/auth/login', data);
    return response.data;
  },

  getMe: async () => {
    const response = await apiClient.get<ApiResponse<User>>('/auth/me');
    return response.data;
  },
};
