import { apiClient } from '../../api/client';
import type { ApiResponse } from '../workbench/api';

export interface QaAskRequest {
  question: string;
}

export interface QaAskResponse {
  answer: string;
  sourceRefs: string[];
}

export const chatApi = {
  ask: async (question: string): Promise<QaAskResponse> => {
    const response = await apiClient.post<ApiResponse<QaAskResponse>>('/qa/ask', {
      question,
    });
    return response.data.data;
  },
};
