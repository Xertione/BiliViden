import { NavLink, useNavigate } from 'react-router-dom';
import {
  SquaresFour,
  ChatTeardropDots,
  BookOpen,
  UserCircle,
  Lightning,
  SignOut
} from '@phosphor-icons/react';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

const navItems = [
  { icon: SquaresFour, label: '工作台', path: '/workbench' },
  { icon: ChatTeardropDots, label: '智能问答', path: '/chat' },
  { icon: BookOpen, label: '知识库', path: '/knowledge' },
  { icon: UserCircle, label: '个人中心', path: '/profile' },
];

export function Sidebar() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('auth_token');
    navigate('/login');
  };

  return (
    <aside className="fixed left-0 top-0 z-40 h-screen w-64 border-r border-zinc-800 bg-zinc-950 flex flex-col">
      <div className="p-6 flex items-center gap-3">
        <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center">
          <Lightning weight="fill" className="text-white text-xl" />
        </div>
        <span className="font-bold text-lg tracking-tight text-zinc-100">Bili Agent</span>
      </div>

      <nav className="flex-1 px-4 space-y-1">
        {navItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) => cn(
              "flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all duration-200 group",
              isActive
                ? "bg-zinc-900 text-blue-400"
                : "text-zinc-400 hover:bg-zinc-900 hover:text-zinc-200"
            )}
          >
            <item.icon
              size={22}
              weight="duotone"
              className={cn(
                "transition-colors",
                "group-hover:text-zinc-100"
              )}
            />
            <span className="font-medium text-sm">{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="p-4 border-t border-zinc-800 space-y-4">
        <button
          onClick={handleLogout}
          className="flex w-full items-center gap-3 px-3 py-2 text-zinc-400 hover:text-zinc-100 transition-colors text-sm font-medium"
        >
          <SignOut size={20} />
          退出登录
        </button>

        <div className="flex items-center gap-3 px-3 py-2 text-zinc-500">
          <div className="w-2 h-2 rounded-full bg-green-500 shadow-[0_0_8px_rgba(34,197,94,0.6)]" />
          <span className="text-xs font-medium uppercase tracking-wider">Service Online</span>
        </div>
      </div>
    </aside>
  );
}
