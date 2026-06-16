import { ChatPage } from '../features/chat/ChatPage';
import { AnalysisDrawer } from '../features/analysis/components/AnalysisDrawer';
import { useUrlState } from '../hooks/useUrlState';
import { useQuery } from '@tanstack/react-query';
import { workbenchApi } from '../features/workbench/api';

export function Chat() {
  const { videoId, closeDrawer } = useUrlState();

  // Derive taskId from the selected video (if the drawer is opened from a citation)
  const { data: tasks = [] } = useQuery({
    queryKey: ['analysis-tasks'],
    queryFn: workbenchApi.listTasks,
    refetchInterval: 15000,
  });

  const selectedTask = tasks.find(t => String(t.videoId) === videoId);
  const selectedTaskId = selectedTask?.id ?? null;

  return (
    <div className="flex flex-col h-full">
      {/* Header */}
      <div className="flex-shrink-0 px-1 pb-0">
        <h1 className="text-2xl font-semibold text-zinc-100">Chat</h1>
        <p className="text-sm text-zinc-500 mt-1">
          Ask questions based on your knowledge cards
        </p>
      </div>

      {/* Chat content fills remaining height */}
      <div className="flex-1 min-h-0 -mx-6">
        <ChatPage />
      </div>

      {/* Analysis Drawer for source citations */}
      <AnalysisDrawer
        videoId={videoId}
        taskId={selectedTaskId}
        onClose={closeDrawer}
      />
    </div>
  );
}
