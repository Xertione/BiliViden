import { useQuery } from '@tanstack/react-query';
import { analysisApi, type TaskDetailResponse } from '../api';
import type { AnalysisStatus } from '../../workbench/api';

/**
 * Smart polling hook for a single analysis task.
 *
 * Polls every 2 seconds when the task is PENDING or RUNNING.
 * Stops polling when:
 *   - Task reaches terminal state (SUCCESS, FAILED, CANCELED)
 *   - The drawer is closed (no taskId provided)
 *
 * @param taskId — the task ID to poll (null/undefined to disable)
 * @returns the query result with the task detail
 */
export function useAnalysisPolling(taskId: number | null | undefined) {
  const isActive =
    taskId !== null &&
    taskId !== undefined &&
    taskId !== 0;

  return useQuery<TaskDetailResponse>({
    queryKey: ['analysis-task-detail', taskId],
    queryFn: () => analysisApi.getTaskDetail(taskId!),
    enabled: isActive,
    refetchInterval: (query) => {
      if (!isActive) return false;

      const status = query.state.data?.status as AnalysisStatus | undefined;

      // Poll only while task is in progress
      if (status === 'PENDING' || status === 'RUNNING' || status === 'RETRYING') {
        return 2000; // 2 seconds
      }

      return false; // Stop polling on terminal states
    },
    // Don't refetch on window focus to avoid unnecessary requests
    refetchOnWindowFocus: false,
    // Keep stale data visible while re-fetching
    staleTime: 0,
  });
}
