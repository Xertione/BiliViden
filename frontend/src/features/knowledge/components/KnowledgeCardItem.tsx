import { type KnowledgeCard } from '../api';
import { useUrlState } from '../../../hooks/useUrlState';
import { useCallback } from 'react';

interface KnowledgeCardProps {
  card: KnowledgeCard;
}

export function KnowledgeCardItem({ card }: KnowledgeCardProps) {
  const { openDrawer } = useUrlState();

  const handleClick = useCallback(() => {
    openDrawer(String(card.videoId), 'card', 'knowledge');
  }, [card.videoId, openDrawer]);

  let tags: string[] = [];
  try {
    tags = JSON.parse(card.tagsJson);
  } catch {}

  let keyPoints: string[] = [];
  try {
    keyPoints = JSON.parse(card.keyPointsJson);
  } catch {}

  return (
    <div
      onClick={handleClick}
      className="group rounded-xl border border-zinc-800 bg-zinc-900/40 p-5 hover:border-zinc-700 hover:bg-zinc-900/60 transition-all duration-200 cursor-pointer"
    >
      <div className="flex items-start justify-between gap-4 mb-3">
        <h3 className="text-sm font-semibold text-zinc-100 group-hover:text-blue-400 transition-colors leading-snug line-clamp-2">
          {card.title}
        </h3>
        <span className="shrink-0 text-[10px] font-mono text-zinc-600">
          #{card.videoId}
        </span>
      </div>

      <p className="text-sm text-zinc-400 leading-relaxed line-clamp-3 mb-4">
        {card.summary}
      </p>

      {keyPoints.length > 0 && (
        <ul className="space-y-1 mb-4">
          {keyPoints.slice(0, 3).map((point, i) => (
            <li key={i} className="flex items-start gap-2 text-xs text-zinc-500">
              <span className="mt-[5px] w-1 h-1 rounded-full bg-zinc-700 shrink-0" />
              {point}
            </li>
          ))}
        </ul>
      )}

      {tags.length > 0 && (
        <div className="flex flex-wrap gap-1.5">
          {tags.map((tag, i) => (
            <span
              key={i}
              className="px-2 py-0.5 rounded-md bg-blue-500/5 border border-blue-500/10 text-[10px] font-mono text-blue-400/70"
            >
              {tag}
            </span>
          ))}
        </div>
      )}
    </div>
  );
}
