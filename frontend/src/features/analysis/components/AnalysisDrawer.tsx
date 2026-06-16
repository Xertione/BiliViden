import { useCallback } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useAnalysisPolling } from '../hooks/useAnalysisPolling';
import { analysisApi } from '../api';
import { ArrowClockwise, Spinner, X } from '@phosphor-icons/react';

import {
  Sheet,
  SheetOverlay,
  SheetContent,
  SheetClose,
  SheetHeader,
  SheetTitle,
  SheetBody,
} from '../../../components/ui/sheet';

interface AnalysisDrawerProps {
  videoId: string | null;
  taskId: number | null;
  onClose: () => void;
}

/* ─── Skeleton loader for running state ─── */

function SkeletonBlock({ lines = 3 }: { lines?: number }) {
  return (
    <div className="space-y-3 animate-pulse">
      {Array.from({ length: lines }).map((_, i) => (
        <div
          key={i}
          className="h-4 bg-zinc-800 rounded"
          style={{ width: `${60 + Math.random() * 30}%` }}
        />
      ))}
    </div>
  );
}

/* ─── Pulsing dot for "Analyzing..." indicator ─── */

function PulsingDot() {
  return (
    <span className="inline-flex items-center gap-1.5">
      <span className="relative flex h-2 w-2">
        <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-blue-400 opacity-75" />
        <span className="relative inline-flex h-2 w-2 rounded-full bg-blue-500" />
      </span>
      Analyzing...
    </span>
  );
}

/* ─── Status badge ─── */

function StatusBadge({ status }: { status: string }) {
  const config: Record<string, { label: string; class: string }> = {
    PENDING: { label: 'Pending', class: 'text-zinc-400 bg-zinc-800/50' },
    RUNNING: { label: 'Running', class: 'text-blue-400 bg-blue-500/10' },
    SUCCESS: { label: 'Complete', class: 'text-emerald-400 bg-emerald-500/10' },
    FAILED: { label: 'Failed', class: 'text-red-400 bg-red-500/10' },
    CANCELED: { label: 'Canceled', class: 'text-zinc-500 bg-zinc-800/50' },
    RETRYING: { label: 'Retrying', class: 'text-amber-400 bg-amber-500/10' },
  };

  const c = config[status] ?? { label: status, class: 'text-zinc-400 bg-zinc-800/50' };

  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-mono ${c.class}`}>
      {c.label}
    </span>
  );
}

/* ─── Drawer Component ─── */

export function AnalysisDrawer({ videoId, taskId, onClose }: AnalysisDrawerProps) {
  const queryClient = useQueryClient();

  const {
    data: taskDetail,
    isLoading,
    isError,
    error,
  } = useAnalysisPolling(taskId);

  // Retry mutation
  const retryMutation = useMutation({
    mutationFn: () => analysisApi.retryTask(taskId!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['analysis-task-detail', taskId] });
      queryClient.invalidateQueries({ queryKey: ['analysis-tasks'] });
    },
  });

  const handleRetry = useCallback(() => {
    if (taskId) {
      retryMutation.mutate();
    }
  }, [taskId, retryMutation]);

  // Invalidate the workbench task list when task reaches terminal state
  const status = taskDetail?.status;
  if (status === 'SUCCESS' || status === 'FAILED' || status === 'CANCELED') {
    // Lazily invalidate — this runs on render but only once per status change
    // since queryClient caches the invalidation
    setTimeout(() => {
      queryClient.invalidateQueries({ queryKey: ['analysis-tasks'] });
    }, 0);
  }

  const isOpen = videoId !== null;

  return (
    <Sheet open={isOpen} onOpenChange={(open) => { if (!open) onClose(); }}>
      <SheetOverlay />

      <SheetContent side="right">
        {/* Header with close button */}
        <SheetHeader>
          <div className="flex items-start justify-between">
            <SheetTitle>
              Video #{videoId ?? taskDetail?.videoId ?? '--'}
            </SheetTitle>
            <SheetClose>
              <X size={18} weight="bold" />
            </SheetClose>
          </div>
        </SheetHeader>

        <SheetBody>
          {/* ── Overview Section (eyebrow) ── */}
          <div className="mb-8">
            <h3 className="text-[11px] uppercase tracking-[0.18em] font-mono text-zinc-500 mb-4">
              Overview
            </h3>

            {isLoading && (
              <div className="space-y-3">
                <SkeletonBlock lines={2} />
              </div>
            )}

            {isError && (
              <div className="px-3 py-3 rounded-lg border border-red-500/20 bg-red-500/5 text-sm text-red-400">
                Failed to load task: {(error as Error)?.message ?? 'Unknown error'}
              </div>
            )}

            {taskDetail && (
              <div className="space-y-3">
                {/* Status badge */}
                <div className="flex items-center gap-3">
                  <StatusBadge status={taskDetail.status} />
                  {taskDetail.analysisType && (
                    <span className="text-[11px] uppercase tracking-wider text-zinc-500 font-mono">
                      {taskDetail.analysisType}
                    </span>
                  )}
                </div>

                {/* Metadata */}
                <div className="flex flex-col gap-1.5 text-sm text-zinc-400">
                  {taskDetail.modelName && (
                    <div className="flex items-center gap-2">
                      <span className="text-zinc-600 font-mono text-[11px] uppercase tracking-wider">Model</span>
                      <span className="font-mono text-xs">{taskDetail.modelName}</span>
                    </div>
                  )}
                  {taskDetail.startedAt && (
                    <div className="flex items-center gap-2">
                      <span className="text-zinc-600 font-mono text-[11px] uppercase tracking-wider">Started</span>
                      <span className="text-xs">{new Date(taskDetail.startedAt).toLocaleString()}</span>
                    </div>
                  )}
                  {taskDetail.finishedAt && (
                    <div className="flex items-center gap-2">
                      <span className="text-zinc-600 font-mono text-[11px] uppercase tracking-wider">Finished</span>
                      <span className="text-xs">{new Date(taskDetail.finishedAt).toLocaleString()}</span>
                    </div>
                  )}
                </div>

                {/* Running indicator */}
                {(taskDetail.status === 'RUNNING' || taskDetail.status === 'PENDING') && (
                  <div className="mt-3">
                    <span className="font-mono text-blue-400 text-sm">
                      <PulsingDot />
                    </span>
                  </div>
                )}

                {/* Failed state */}
                {taskDetail.status === 'FAILED' && taskDetail.errorMessage && (
                  <div className="mt-3 px-4 py-3 rounded-lg border border-red-500/20 bg-red-500/5">
                    <p className="text-sm text-red-400 font-medium mb-2">Analysis Failed</p>
                    <p className="text-sm text-red-300/80 font-mono text-xs leading-relaxed">
                      {taskDetail.errorMessage}
                    </p>
                    <button
                      onClick={handleRetry}
                      disabled={retryMutation.isPending}
                      className="mt-3 inline-flex items-center gap-2 px-3 py-1.5 rounded-md bg-red-500/10 border border-red-500/20 text-red-400 text-xs font-medium hover:bg-red-500/20 transition-colors duration-200 disabled:opacity-50"
                    >
                      {retryMutation.isPending ? (
                        <>
                          <Spinner size={14} className="animate-spin" />
                          Retrying...
                        </>
                      ) : (
                        <>
                          <ArrowClockwise size={14} />
                          Retry
                        </>
                      )}
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* ── Analysis Section ── */}
          {taskDetail && taskDetail.result && (
            <div className="mb-8">
              <h3 className="text-[11px] uppercase tracking-[0.18em] font-mono text-zinc-500 mb-4">
                Analysis
              </h3>

              <div className="space-y-5">
                {/* Summary */}
                {taskDetail.result.summary && (
                  <div>
                    <p className="text-sm text-zinc-300 leading-relaxed">
                      {taskDetail.result.summary}
                    </p>
                  </div>
                )}

                {/* Core Points */}
                {taskDetail.result.corePoints && taskDetail.result.corePoints.length > 0 && (
                  <div className="space-y-2.5">
                    {taskDetail.result.corePoints.map((point, index) => (
                      <div
                        key={index}
                        className="border-l-2 border-blue-500/50 pl-4"
                      >
                        <p className="text-sm text-zinc-300 leading-relaxed">{point}</p>
                      </div>
                    ))}
                  </div>
                )}

                {/* Keywords */}
                {taskDetail.result.keywords && taskDetail.result.keywords.length > 0 && (
                  <div className="flex flex-wrap gap-2">
                    {taskDetail.result.keywords.map((keyword, index) => (
                      <span
                        key={index}
                        className="px-2.5 py-1 rounded-md bg-blue-500/5 border border-blue-500/15 text-xs text-blue-400 font-medium"
                      >
                        {keyword}
                      </span>
                    ))}
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Skeleton while RUNNING with no result yet */}
          {taskDetail && (taskDetail.status === 'RUNNING' || taskDetail.status === 'PENDING') && !taskDetail.result && (
            <div className="mb-8">
              <h3 className="text-[11px] uppercase tracking-[0.18em] font-mono text-zinc-500 mb-4">
                Analysis
              </h3>
              <SkeletonBlock lines={5} />
            </div>
          )}

          {/* ── Citations Section ── */}
          {taskDetail?.result && (
            <div className="mb-6">
              <h3 className="text-[11px] uppercase tracking-[0.18em] font-mono text-zinc-500 mb-4">
                Citations
              </h3>

              <div className="flex flex-wrap gap-2">
                {taskDetail.result.modelName && (
                  <span className="inline-flex items-center px-2.5 py-1 rounded-full bg-zinc-800/80 border border-zinc-700/50 text-[11px] font-mono text-zinc-400">
                    model: {taskDetail.result.modelName}
                  </span>
                )}
                {taskDetail.result.promptVersion && (
                  <span className="inline-flex items-center px-2.5 py-1 rounded-full bg-zinc-800/80 border border-zinc-700/50 text-[11px] font-mono text-zinc-400">
                    prompt: v{taskDetail.result.promptVersion}
                  </span>
                )}
                {taskDetail.createdAt && (
                  <span className="inline-flex items-center px-2.5 py-1 rounded-full bg-zinc-800/80 border border-zinc-700/50 text-[11px] font-mono text-zinc-400">
                    {new Date(taskDetail.createdAt).toLocaleDateString()}
                  </span>
                )}
              </div>
            </div>
          )}
        </SheetBody>
      </SheetContent>
    </Sheet>
  );
}
