import { apiClient } from '../../api/client';

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

export type AnalysisStatus = 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'CANCELED' | 'RETRYING';

export interface AnalysisTask {
  id: number;
  userId: number;
  videoId: number;
  analysisType: string;
  status: AnalysisStatus;
  retryCount: number;
  errorMessage: string | null;
  modelName: string | null;
  promptVersion: string | null;
  startedAt: string | null;
  finishedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface SyncResponse {
  biliUid: string;
  counts: Record<string, number>;
  syncedAt: string;
}

export interface SyncRequest {
  biliUid: string;
  cookieSnapshot: string;
}

export const workbenchApi = {
  listTasks: async (): Promise<AnalysisTask[]> => {
    const response = await apiClient.get<ApiResponse<AnalysisTask[]>>('/analysis/tasks');
    return response.data.data;
  },

  syncVideos: async (): Promise<SyncResponse> => {
    const response = await apiClient.post<ApiResponse<SyncResponse>>('/bili/sync');
    return response.data.data;
  },

  createTask: async (videoId: number, analysisType: string): Promise<string> => {
    const response = await apiClient.post<ApiResponse<string>>('/analysis/tasks', {
      videoId,
      analysisType,
    });
    return response.data.data;
  },
};
