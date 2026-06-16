import { type AnalysisTask } from '../api';
import { ArrowCircleUp, CheckCircle, PlayCircle, WarningCircle } from '@phosphor-icons/react';

interface VideoCardProps {
  task: AnalysisTask;
  onClick: (task: AnalysisTask) => void;
}

const statusConfig: Record<string, { label: string; icon: React.ReactNode; borderColor: string; badgeClass: string }> = {
  PENDING: {
    label: 'Pending',
    icon: <PlayCircle size={20} weight="regular" />,
    borderColor: 'border-zinc-700',
    badgeClass: 'text-zinc-400 bg-zinc-800/50',
  },
  RUNNING: {
    label: 'Analysis Started...',
    icon: <ArrowCircleUp size={20} weight="regular" />,
    borderColor: 'border-blue-500/30',
    badgeClass: 'text-blue-400 bg-blue-500/10',
  },
  SUCCESS: {
    label: 'View Analysis',
    icon: <CheckCircle size={20} weight="fill" className="text-emerald-400" />,
    borderColor: 'border-emerald-500/30',
    badgeClass: 'text-emerald-400 bg-emerald-500/10',
  },
  FAILED: {
    label: 'Analysis Failed',
    icon: <WarningCircle size={20} weight="fill" className="text-red-400" />,
    borderColor: 'border-red-500/30',
    badgeClass: 'text-red-400 bg-red-500/10',
  },
  CANCELED: {
    label: 'Canceled',
    icon: <WarningCircle size={20} weight="regular" />,
    borderColor: 'border-zinc-700',
    badgeClass: 'text-zinc-500 bg-zinc-800/50',
  },
  RETRYING: {
    label: 'Retrying...',
    icon: <ArrowCircleUp size={20} weight="regular" />,
    borderColor: 'border-amber-500/30',
    badgeClass: 'text-amber-400 bg-amber-500/10',
  },
};

export function VideoCard({ task, onClick }: VideoCardProps) {
  const config = statusConfig[task.status] ?? statusConfig.PENDING;

  return (
    <div
      className={`group relative flex flex-col rounded-lg border ${config.borderColor} bg-zinc-900/50 overflow-hidden cursor-pointer transition-all duration-200 hover:border-zinc-700 hover:bg-zinc-900/80`}
      onClick={() => onClick(task)}
    >
      {/* Thumbnail area - 16:9 aspect ratio */}
      <div className="relative w-full aspect-video bg-zinc-800 overflow-hidden">
        {/* Placeholder gradient when no cover */}
        <div className="absolute inset-0 bg-gradient-to-br from-zinc-800 via-zinc-850 to-zinc-900" />

        {/* Status badge */}
        <div className="absolute top-3 left-3 z-10">
          <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-xs font-medium ${config.badgeClass}`}>
            {config.icon}
            {config.label}
          </span>
        </div>
      </div>

      {/* Single-pixel progress bar for RUNNING status */}
      {task.status === 'RUNNING' && (
        <div className="relative w-full h-[1px] bg-zinc-800 overflow-hidden">
          <div className="absolute inset-0 w-full h-full bg-blue-500 animate-progress-pulse" />
        </div>
      )}

      {/* Video info */}
      <div className="flex flex-col p-4 gap-1.5">
        <h3 className="text-sm font-medium text-zinc-100 line-clamp-2 leading-snug group-hover:text-blue-400 transition-colors duration-200">
          Video #{task.videoId}
        </h3>

        <div className="flex items-center gap-2 mt-auto">
          <span className="text-[11px] text-zinc-500 uppercase tracking-wider">
            {task.analysisType || 'general'}
          </span>
          {task.errorMessage && task.status === 'FAILED' && (
            <span className="text-[11px] text-red-400/70 truncate ml-auto" title={task.errorMessage}>
              {task.errorMessage.length > 30 ? `${task.errorMessage.slice(0, 30)}...` : task.errorMessage}
            </span>
          )}
          {task.status === 'SUCCESS' && (
            <span className="text-xs text-emerald-400 ml-auto">
              View
            </span>
          )}
        </div>
      </div>
    </div>
  );
}
