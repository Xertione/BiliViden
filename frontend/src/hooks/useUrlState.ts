import { useSearchParams } from 'react-router-dom';
import { useCallback } from 'react';

/**
 * Protocol: ?video={id}&panel={analysis|card}&source={chat|workbench|knowledge}
 */
export function useUrlState() {
  const [searchParams, setSearchParams] = useSearchParams();

  const videoId = searchParams.get('video');
  const panel = searchParams.get('panel');
  const source = searchParams.get('source');

  const openDrawer = useCallback((id: string, panelType: 'analysis' | 'card', currentSource: string) => {
    setSearchParams(prev => {
      const next = new URLSearchParams(prev);
      next.set('video', id);
      next.set('panel', panelType);
      next.set('source', currentSource);
      return next;
    });
  }, [setSearchParams]);

  const closeDrawer = useCallback(() => {
    setSearchParams(prev => {
      const next = new URLSearchParams(prev);
      next.delete('video');
      next.delete('panel');
      next.delete('source');
      return next;
    });
  }, [setSearchParams]);

  return {
    videoId,
    panel,
    source,
    isDrawerOpen: !!videoId,
    openDrawer,
    closeDrawer
  };
}
