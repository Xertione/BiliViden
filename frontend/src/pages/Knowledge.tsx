import { useQuery } from '@tanstack/react-query';
import { knowledgeApi } from '../features/knowledge/api';
import { KnowledgeCardItem } from '../features/knowledge/components/KnowledgeCardItem';
import { AnalysisDrawer } from '../features/analysis/components/AnalysisDrawer';
import { useUrlState } from '../hooks/useUrlState';
import { useQuery as useWorkbenchQuery } from '@tanstack/react-query';
import { workbenchApi } from '../features/workbench/api';
import { Spinner, BookOpen } from '@phosphor-icons/react';

export function Knowledge() {
  const { videoId, closeDrawer } = useUrlState();

  const {
    data: cards = [],
    isLoading,
    isError,
    error,
  } = useQuery({
    queryKey: ['knowledge-cards'],
    queryFn: knowledgeApi.listCards,
  });

  const { data: tasks = [] } = useWorkbenchQuery({
    queryKey: ['analysis-tasks'],
    queryFn: workbenchApi.listTasks,
    enabled: !!videoId,
  });
  const selectedTask = tasks.find(t => String(t.videoId) === videoId);
  const selectedTaskId = selectedTask?.id ?? null;

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-32">
        <Spinner size={24} className="animate-spin text-zinc-500" />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="px-4 py-3 rounded-lg border border-red-500/20 bg-red-500/5 text-sm text-red-400">
        Failed to load cards: {(error as Error)?.message ?? 'Unknown error'}
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-zinc-100">Knowledge</h1>
          <p className="text-sm text-zinc-500 mt-1">
            {cards.length} card{cards.length !== 1 ? 's' : ''} saved
          </p>
        </div>
      </div>

      {cards.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-24 text-zinc-500">
          <div className="w-16 h-16 rounded-full bg-zinc-800/50 flex items-center justify-center mb-5">
            <BookOpen size={28} className="text-zinc-600" />
          </div>
          <p className="text-base font-medium text-zinc-400">No cards yet</p>
          <p className="text-sm mt-1 max-w-sm text-center">
            Analyze a video from the Workbench, then save the result as a knowledge card to see it here.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {cards.map(card => (
            <KnowledgeCardItem key={card.id} card={card} />
          ))}
        </div>
      )}

      <AnalysisDrawer
        videoId={videoId}
        taskId={selectedTaskId}
        onClose={closeDrawer}
      />
    </div>
  );
}
