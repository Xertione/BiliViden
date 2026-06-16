import { useCallback } from 'react';
import { useUrlState } from '../../../hooks/useUrlState';

interface SourceCitationProps {
  source: string;
}

/**
 * Parses a source string like "video:BV1demo001" or "card:1" or "analysis-task:1"
 * into { type, id }.
 */
function parseSource(source: string): { type: string; id: string } {
  const colonIdx = source.indexOf(':');
  if (colonIdx === -1) {
    return { type: 'unknown', id: source };
  }
  return {
    type: source.slice(0, colonIdx),
    id: source.slice(colonIdx + 1),
  };
}

/**
 * A small mono-style pill that displays a single source reference.
 * Clicking it opens the AnalysisDrawer via URL state.
 */
export function SourceCitation({ source }: SourceCitationProps) {
  const { openDrawer } = useUrlState();
  const { type, id } = parseSource(source);

  const handleClick = useCallback(() => {
    // For cards and analysis-tasks, pass the id as the video param.
    // The drawer will look up the associated video/task.
    openDrawer(id, 'analysis', 'chat');
  }, [id, openDrawer]);

  return (
    <button
      onClick={handleClick}
      className="inline-flex items-center px-2.5 py-1 rounded-full bg-zinc-800/50 border border-zinc-700/50 text-[11px] font-mono text-zinc-400 hover:border-blue-500/30 hover:text-blue-400 transition-colors cursor-pointer"
    >
      {type}:{id}
    </button>
  );
}
