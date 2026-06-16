import { apiClient } from '../../api/client';
import type { ApiResponse } from '../workbench/api';

export interface KnowledgeCard {
  id: number;
  userId: number;
  videoId: number;
  analysisTaskId: number;
  analysisResultId: number;
  title: string;
  summary: string;
  keyPointsJson: string;
  tagsJson: string;
  createdAt: string;
  updatedAt: string;
}

export const knowledgeApi = {
  listCards: async (): Promise<KnowledgeCard[]> => {
    const response = await apiClient.get<ApiResponse<KnowledgeCard[]>>('/knowledge/cards');
    return response.data.data;
  },

  getCard: async (cardId: number): Promise<KnowledgeCard> => {
    const response = await apiClient.get<ApiResponse<KnowledgeCard>>(`/knowledge/cards/${cardId}`);
    return response.data.data;
  },
};
