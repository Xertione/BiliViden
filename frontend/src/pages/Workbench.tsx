import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useCallback, useState } from 'react';
import { workbenchApi, type AnalysisTask } from '../features/workbench/api';
import { VideoGrid } from '../features/workbench/components/VideoGrid';
import { AnalysisDrawer } from '../features/analysis/components/AnalysisDrawer';
import { useUrlState } from '../hooks/useUrlState';
import { ArrowClockwise, Spinner } from '@phosphor-icons/react';

export function Workbench() {
  const queryClient = useQueryClient();
  const [isRefreshing, setIsRefreshing] = useState(false);
  const { videoId, openDrawer, closeDrawer } = useUrlState();

  const {
    data: tasks = [],
    isLoading,
    isError,
    error,
  } = useQuery({
    queryKey: ['analysis-tasks'],
    queryFn: workbenchApi.listTasks,
    refetchInterval: 15000, // Poll every 15s for status updates
  });

  const syncMutation = useMutation({
    mutationFn: workbenchApi.syncVideos,
    onSuccess: () => {
      // After sync, refetch the task list
      queryClient.invalidateQueries({ queryKey: ['analysis-tasks'] });
    },
  });

  // Derive the task ID for the currently selected video
  const selectedTask = tasks.find(t => String(t.videoId) === videoId);
  const selectedTaskId = selectedTask?.id ?? null;

  const handleSync = useCallback(() => {
    syncMutation.mutate();
  }, [syncMutation]);

  const handleCardClick = useCallback((task: AnalysisTask) => {
    openDrawer(String(task.videoId), 'analysis', 'workbench');
  }, [openDrawer]);

  const handleRefresh = useCallback(async () => {
    setIsRefreshing(true);
    await queryClient.invalidateQueries({ queryKey: ['analysis-tasks'] });
    setIsRefreshing(false);
  }, [queryClient]);

  return (
    <div className="flex flex-col h-full gap-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-zinc-100">Workbench</h1>
          <p className="text-sm text-zinc-500 mt-1">
            {tasks.length} video{tasks.length !== 1 ? 's' : ''} synced
          </p>
        </div>

        <div className="flex items-center gap-3">
          {/* Refresh button */}
          <button
            onClick={handleRefresh}
            disabled={isRefreshing}
            className="p-2 rounded-lg border border-zinc-800 bg-zinc-900/50 text-zinc-400 hover:text-zinc-300 hover:border-zinc-700 transition-all duration-200 disabled:opacity-50"
            title="Refresh"
          >
            <ArrowClockwise
              size={18}
              className={isRefreshing ? 'animate-spin' : ''}
            />
          </button>

          {/* Sync button */}
          <button
            onClick={handleSync}
            disabled={syncMutation.isPending}
            className="inline-flex items-center gap-2 px-4 py-2 rounded-lg bg-brand-blue text-white text-sm font-medium hover:bg-blue-600 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {syncMutation.isPending ? (
              <>
                <Spinner size={16} className="animate-spin" />
                Syncing...
              </>
            ) : (
              <>
                <ArrowClockwise size={16} />
                Sync Videos
              </>
            )}
          </button>
        </div>
      </div>

      {/* Sync status feedback */}
      {syncMutation.isSuccess && (
        <div className="px-4 py-3 rounded-lg border border-emerald-500/20 bg-emerald-500/5 text-sm text-emerald-400">
          Sync complete. New videos added.
        </div>
      )}
      {syncMutation.isError && (
        <div className="px-4 py-3 rounded-lg border border-red-500/20 bg-red-500/5 text-sm text-red-400">
          Sync failed. Please check your Bilibili binding.
        </div>
      )}

      {/* Error state */}
      {isError && (
        <div className="px-4 py-3 rounded-lg border border-red-500/20 bg-red-500/5 text-sm text-red-400">
          Failed to load tasks: {(error as Error)?.message ?? 'Unknown error'}
        </div>
      )}

      {/* Video grid */}
      <VideoGrid
        tasks={tasks}
        onCardClick={handleCardClick}
        isLoading={isLoading}
      />

      {/* Analysis Drawer */}
      <AnalysisDrawer
        videoId={videoId}
        taskId={selectedTaskId}
        onClose={closeDrawer}
      />
    </div>
  );
}
