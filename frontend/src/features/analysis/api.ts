import { apiClient } from '../../api/client';
import type { ApiResponse, AnalysisTask, AnalysisStatus } from '../workbench/api';

export interface AnalysisResultItem {
  id: number;
  taskId: number;
  videoId: number;
  analysisType: string;
  summary: string;
  corePoints: string[];
  keywords: string[];
  rawResponse: string | null;
  modelName: string;
  promptVersion: string;
  createdAt: string;
}

export interface TaskDetailResponse {
  id: number;
  userId: number;
  videoId: number;
  analysisType: string;
  status: AnalysisStatus;
  retryCount: number;
  errorMessage: string | null;
  modelName: string | null;
  promptVersion: string | null;
  result: AnalysisResultItem | null;
  startedAt: string | null;
  finishedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export const analysisApi = {
  /** Get single task detail (includes result data) */
  getTaskDetail: async (taskId: number): Promise<TaskDetailResponse> => {
    const response = await apiClient.get<ApiResponse<TaskDetailResponse>>(`/analysis/tasks/${taskId}`);
    return response.data.data;
  },

  /** List tasks (all tasks for current user) */
  listTasks: async (): Promise<AnalysisTask[]> => {
    const response = await apiClient.get<ApiResponse<AnalysisTask[]>>('/analysis/tasks');
    return response.data.data;
  },

  /** Create a new analysis task */
  createTask: async (videoId: number, analysisType: string): Promise<string> => {
    const response = await apiClient.post<ApiResponse<string>>('/analysis/tasks', {
      videoId,
      analysisType,
    });
    return response.data.data;
  },

  /** Retry a failed task */
  retryTask: async (taskId: number): Promise<string> => {
    const response = await apiClient.post<ApiResponse<string>>(`/analysis/tasks/${taskId}/retry`);
    return response.data.data;
  },
};
