import { useState, useRef, useEffect, useCallback } from 'react';
import { useMutation } from '@tanstack/react-query';
import { chatApi, type QaAskResponse } from './api';
import { SourceCitation } from './components/SourceCitation';
import { Spinner, PaperPlaneRight } from '@phosphor-icons/react';

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  sourceRefs?: string[];
}

let messageIdCounter = 0;
function nextId(): string {
  messageIdCounter += 1;
  return `msg-${messageIdCounter}`;
}

/* ─── Typing indicator ─── */

function TypingIndicator() {
  return (
    <div className="flex items-start gap-3">
      <div className="flex items-center gap-1 px-4 py-3">
        <span className="w-1.5 h-1.5 rounded-full bg-zinc-600 animate-bounce" style={{ animationDelay: '0ms' }} />
        <span className="w-1.5 h-1.5 rounded-full bg-zinc-600 animate-bounce" style={{ animationDelay: '150ms' }} />
        <span className="w-1.5 h-1.5 rounded-full bg-zinc-600 animate-bounce" style={{ animationDelay: '300ms' }} />
      </div>
    </div>
  );
}

/* ─── Welcome empty state ─── */

function WelcomeState() {
  return (
    <div className="flex-1 flex items-center justify-center">
      <div className="text-center max-w-md px-6">
        <div className="w-12 h-12 mx-auto mb-5 rounded-xl bg-brand-blue/10 border border-brand-blue/20 flex items-center justify-center">
          <PaperPlaneRight size={24} className="text-brand-blue" />
        </div>
        <h2 className="text-lg font-medium text-zinc-100 mb-2">Ask about your videos</h2>
        <p className="text-sm text-zinc-500 leading-relaxed">
          Your knowledge cards are ready. Ask questions about the content you have analyzed —
          answers will include references to the source videos and cards.
        </p>
      </div>
    </div>
  );
}

/* ─── Error banner ─── */

function ErrorBanner({ message, onRetry }: { message: string; onRetry: () => void }) {
  return (
    <div className="px-4 py-3 mx-4 my-2 rounded-lg border border-red-500/20 bg-red-500/5 text-sm text-red-400 flex items-center justify-between">
      <span>{message}</span>
      <button
        onClick={onRetry}
        className="ml-3 px-3 py-1 rounded-md bg-red-500/10 border border-red-500/20 text-red-400 text-xs font-medium hover:bg-red-500/20 transition-colors"
      >
        Retry
      </button>
    </div>
  );
}

/* ─── ChatPage ─── */

export function ChatPage() {
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputValue, setInputValue] = useState('');
  const inputRef = useRef<HTMLTextAreaElement>(null);
  const listRef = useRef<HTMLDivElement>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const askMutation = useMutation({
    mutationFn: (question: string) => chatApi.ask(question),
    onSuccess: (data: QaAskResponse) => {
      setMessages(prev => [
        ...prev,
        {
          id: nextId(),
          role: 'assistant',
          content: data.answer,
          sourceRefs: data.sourceRefs,
        },
      ]);
      setErrorMessage(null);
    },
    onError: (err: Error) => {
      setErrorMessage(err?.message ?? 'Failed to get answer. Please try again.');
    },
  });

  // Auto-scroll to bottom when messages or loading state changes
  useEffect(() => {
    if (listRef.current) {
      listRef.current.scrollTop = listRef.current.scrollHeight;
    }
  }, [messages, askMutation.isPending]);

  const handleSend = useCallback(() => {
    const question = inputValue.trim();
    if (!question || askMutation.isPending) return;

    setMessages(prev => [
      ...prev,
      { id: nextId(), role: 'user', content: question },
    ]);
    setInputValue('');
    setErrorMessage(null);

    askMutation.mutate(question);
  }, [inputValue, askMutation]);

  const handleKeyDown = useCallback(
    (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
      // Send on Enter (without Shift), newline on Shift+Enter
      if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        handleSend();
      }
    },
    [handleSend],
  );

  const handleRetry = useCallback(() => {
    // Retry the last user message
    const lastUserMsg = [...messages].reverse().find(m => m.role === 'user');
    if (lastUserMsg) {
      // Remove the last assistant message (the failed one) and retry
      setMessages(prev => {
        const idx = prev.findLastIndex(m => m.role === 'assistant');
        return idx >= 0 ? prev.slice(0, idx) : prev;
      });
      setErrorMessage(null);
      askMutation.mutate(lastUserMsg.content);
    }
  }, [messages, askMutation]);

  const hasMessages = messages.length > 0;
  const isLoading = askMutation.isPending;

  return (
    <div className="flex flex-col h-full">
      {/* Scrollable message list */}
      <div
        ref={listRef}
        className="flex-1 overflow-y-auto scroll-smooth"
      >
        {!hasMessages && !isLoading && <WelcomeState />}

        {hasMessages && (
          <div className="flex flex-col gap-2 px-4 pt-4 pb-4">
            {messages.map(msg => (
              <div
                key={msg.id}
                className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                <div className={`max-w-[70%] ${msg.role === 'user' ? '' : 'pr-8'}`}>
                  {/* Message block */}
                  <div
                    className={`px-4 py-3 text-sm leading-relaxed ${
                      msg.role === 'user'
                        ? 'bg-zinc-800 text-zinc-100 rounded-xl'
                        : 'bg-transparent text-zinc-200 rounded-xl'
                    }`}
                  >
                    {msg.content}
                  </div>

                  {/* Source citations beneath AI messages */}
                  {msg.role === 'assistant' && msg.sourceRefs && msg.sourceRefs.length > 0 && (
                    <div className="flex flex-wrap gap-1.5 mt-2 px-4">
                      {msg.sourceRefs.map((ref, idx) => (
                        <SourceCitation key={`${msg.id}-ref-${idx}`} source={ref} />
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Typing indicator while loading */}
        {isLoading && <TypingIndicator />}

        {/* Error message with retry */}
        {errorMessage && (
          <ErrorBanner message={errorMessage} onRetry={handleRetry} />
        )}
      </div>

      {/* Input area */}
      <div className="border-t border-zinc-800/80 px-4 py-4">
        <div className="flex items-end gap-3">
          <textarea
            ref={inputRef}
            value={inputValue}
            onChange={e => setInputValue(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Ask a question..."
            rows={1}
            disabled={isLoading}
            className="flex-1 resize-none bg-zinc-900 ring-1 ring-zinc-800 focus:ring-zinc-700 rounded-xl px-4 py-3 text-sm text-zinc-100 placeholder-zinc-500 outline-none transition-all duration-200 disabled:opacity-50"
            style={{ minHeight: 44, maxHeight: 120 }}
          />
          <button
            onClick={handleSend}
            disabled={!inputValue.trim() || isLoading}
            className="flex-shrink-0 w-11 h-11 rounded-xl bg-brand-blue text-white flex items-center justify-center hover:bg-blue-600 transition-colors duration-200 disabled:opacity-30 disabled:cursor-not-allowed"
          >
            {isLoading ? (
              <Spinner size={18} className="animate-spin" />
            ) : (
              <PaperPlaneRight size={18} />
            )}
          </button>
        </div>
      </div>
    </div>
  );
}
