import * as React from 'react';
import { cn } from '../../lib/utils';

interface SheetContextValue {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

const SheetContext = React.createContext<SheetContextValue | null>(null);

function useSheet() {
  const context = React.useContext(SheetContext);
  if (!context) {
    throw new Error('Sheet components must be used within a Sheet component');
  }
  return context;
}

/**
 * Sheet (Drawer) — slides in from the right side of the viewport.
 *
 * Usage:
 *   <Sheet open={isOpen} onOpenChange={setIsOpen}>
 *     <SheetOverlay />
 *     <SheetContent>
 *       <SheetHeader>
 *         <SheetTitle>Title</SheetTitle>
 *         <SheetDescription>Description</SheetDescription>
 *       </SheetHeader>
 *       <div>Content</div>
 *     </SheetContent>
 *   </Sheet>
 */

interface SheetProps {
  children: React.ReactNode;
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
}

export function Sheet({ children, open = false, onOpenChange }: SheetProps) {
  const [internalOpen, setInternalOpen] = React.useState(open);

  const isControlled = onOpenChange !== undefined;
  const isOpen = isControlled ? open : internalOpen;

  const handleOpenChange = React.useCallback(
    (value: boolean) => {
      if (isControlled) {
        onOpenChange(value);
      } else {
        setInternalOpen(value);
      }
    },
    [isControlled, onOpenChange]
  );

  return (
    <SheetContext.Provider value={{ open: isOpen, onOpenChange: handleOpenChange }}>
      {children}
    </SheetContext.Provider>
  );
}

/* ─── Overlay ─── */

export function SheetOverlay({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  const { open, onOpenChange } = useSheet();

  // Close on overlay click
  const handleClick = React.useCallback(() => {
    onOpenChange(false);
  }, [onOpenChange]);

  if (!open) return null;

  return (
    <div
      className={cn(
        'fixed inset-0 z-50 bg-black/60 backdrop-blur-sm transition-opacity duration-300',
        className
      )}
      onClick={handleClick}
      aria-hidden="true"
      {...props}
    />
  );
}

/* ─── Close button ─── */

export function SheetClose({ className, children, ...props }: React.ButtonHTMLAttributes<HTMLButtonElement>) {
  const { onOpenChange } = useSheet();

  return (
    <button
      className={cn(
        'absolute top-4 right-4 rounded-sm opacity-70 ring-offset-zinc-900 transition-opacity hover:opacity-100 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2',
        className
      )}
      onClick={() => onOpenChange(false)}
      {...props}
    >
      {children || (
        <svg width="15" height="15" viewBox="0 0 15 15" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M11.7816 4.03157C12.0062 3.80702 12.0062 3.44295 11.7816 3.2184C11.5571 2.99385 11.193 2.99385 10.9685 3.2184L7.50005 6.68682L4.03164 3.2184C3.80708 2.99385 3.44301 2.99385 3.21846 3.2184C2.99391 3.44295 2.99391 3.80702 3.21846 4.03157L6.68688 7.49999L3.21846 10.9684C2.99391 11.193 2.99391 11.557 3.21846 11.7816C3.44301 12.0061 3.80708 12.0061 4.03164 11.7816L7.50005 8.31316L10.9685 11.7816C11.193 12.0061 11.5571 12.0061 11.7816 11.7816C12.0062 11.557 12.0062 11.193 11.7816 10.9684L8.31322 7.49999L11.7816 4.03157Z" fill="currentColor" fillRule="evenodd" clipRule="evenodd" />
        </svg>
      )}
      <span className="sr-only">Close</span>
    </button>
  );
}

/* ─── Content (the actual drawer panel) ─── */

interface SheetContentProps {
  children: React.ReactNode;
  className?: string;
  side?: 'left' | 'right';
  /** Called whenever the sheet should close (overlay click, Esc, X button) */
}

export function SheetContent({
  children,
  className,
  side = 'right',
}: SheetContentProps) {
  const { open, onOpenChange } = useSheet();
  const contentRef = React.useRef<HTMLDivElement>(null);
  const previousActiveElement = React.useRef<Element | null>(null);

  // Focus trap & Esc key
  React.useEffect(() => {
    if (!open) return;

    previousActiveElement.current = document.activeElement;

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        onOpenChange(false);
        return;
      }

      // Focus trap
      if (e.key === 'Tab' && contentRef.current) {
        const focusableElements = contentRef.current.querySelectorAll<HTMLElement>(
          'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        );
        const first = focusableElements[0];
        const last = focusableElements[focusableElements.length - 1];

        if (e.shiftKey) {
          if (document.activeElement === first) {
            e.preventDefault();
            last?.focus();
          }
        } else {
          if (document.activeElement === last) {
            e.preventDefault();
            first?.focus();
          }
        }
      }
    };

    document.addEventListener('keydown', handleKeyDown);

    // Focus the content panel
    setTimeout(() => {
      contentRef.current?.focus();
    }, 0);

    // Prevent body scroll
    const originalOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.body.style.overflow = originalOverflow;
      // Restore focus
      if (previousActiveElement.current instanceof HTMLElement) {
        previousActiveElement.current.focus();
      }
    };
  }, [open, onOpenChange]);

  if (!open) return null;

  const sideClasses = side === 'right'
    ? 'right-0 border-l border-zinc-800'
    : 'left-0 border-r border-zinc-800';

  return (
    <div
      ref={contentRef}
      tabIndex={-1}
      role="dialog"
      aria-modal="true"
      className={cn(
        'fixed top-0 z-[60] h-full w-full sm:max-w-md',
        'bg-zinc-900/95 backdrop-blur-xl shadow-2xl',
        'flex flex-col',
        'animate-in-slide-right',
        'outline-none',
        sideClasses,
        className
      )}
    >
      {children}
    </div>
  );
}

/* ─── Header ─── */

export function SheetHeader({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return <div className={cn('flex flex-col gap-1.5 p-6 pb-0', className)} {...props} />;
}

/* ─── Title ─── */

export function SheetTitle({ className, ...props }: React.HTMLAttributes<HTMLHeadingElement>) {
  return (
    <h2
      className={cn('text-lg font-semibold text-zinc-100', className)}
      {...props}
    />
  );
}

/* ─── Description ─── */

export function SheetDescription({ className, ...props }: React.HTMLAttributes<HTMLParagraphElement>) {
  return (
    <p
      className={cn('text-sm text-zinc-500', className)}
      {...props}
    />
  );
}

/* ─── Body (scrollable content area) ─── */

export function SheetBody({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn('flex-1 overflow-y-auto p-6 pt-4', className)}
      {...props}
    />
  );
}
