import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';

export function MainLayout() {
  return (
    <div className="min-h-screen bg-zinc-950 text-zinc-100 flex">
      <Sidebar />
      <main className="flex-1 ml-64 min-h-screen overflow-y-auto bg-zinc-950 relative">
        {/* Sub-navigation or header could go here in future tasks */}
        <div className="p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
