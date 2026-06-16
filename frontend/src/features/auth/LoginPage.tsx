import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Eye, EyeSlash, Fingerprint, User, UserPlus } from '@phosphor-icons/react';
import { authApi } from './api';

export default function LoginPage() {
  const [isLogin, setIsLogin] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    username: '',
    password: '',
    nickname: '',
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      if (isLogin) {
        const res = await authApi.login({
          username: formData.username,
          password: formData.password,
        });
        localStorage.setItem('auth_token', res.data.token);
        navigate('/');
      } else {
        await authApi.register({
          username: formData.username,
          password: formData.password,
          nickname: formData.nickname,
        });
        setIsLogin(true);
        setError('注册成功，请登录');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || '操作失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-950 p-4">
      <div className="w-full max-w-sm space-y-8">
        {/* Header */}
        <div className="text-center">
          <div className="mx-auto mb-6 flex h-12 w-12 items-center justify-center rounded-xl bg-zinc-900 ring-1 ring-zinc-800">
            <Fingerprint className="h-6 w-6 text-zinc-100" />
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-white">
            {isLogin ? '欢迎回来' : '创建账号'}
          </h1>
          <p className="mt-2 text-sm text-zinc-400">
            {isLogin ? '使用你的账号登录 Bili AI Agent' : '开启你的视频知识库'}
          </p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="space-y-4 rounded-2xl bg-zinc-900/50 p-6 ring-1 ring-zinc-800">
            {/* Username */}
            <div className="space-y-1.5">
              <label className="text-xs font-medium uppercase tracking-wider text-zinc-500">
                用户名
              </label>
              <div className="relative">
                <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                  <User className="h-4 w-4 text-zinc-500" />
                </div>
                <input
                  type="text"
                  required
                  value={formData.username}
                  onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                  className="block w-full rounded-lg bg-zinc-900 py-2.5 pl-10 pr-3 text-sm text-white placeholder-zinc-600 ring-1 ring-zinc-800 transition-all focus:outline-none focus:ring-2 focus:ring-zinc-700"
                  placeholder="your-username"
                />
              </div>
            </div>

            {/* Nickname (Register Only) */}
            {!isLogin && (
              <div className="space-y-1.5">
                <label className="text-xs font-medium uppercase tracking-wider text-zinc-500">
                  昵称
                </label>
                <div className="relative">
                  <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                    <UserPlus className="h-4 w-4 text-zinc-500" />
                  </div>
                  <input
                    type="text"
                    required
                    value={formData.nickname}
                    onChange={(e) => setFormData({ ...formData, nickname: e.target.value })}
                    className="block w-full rounded-lg bg-zinc-900 py-2.5 pl-10 pr-3 text-sm text-white placeholder-zinc-600 ring-1 ring-zinc-800 transition-all focus:outline-none focus:ring-2 focus:ring-zinc-700"
                    placeholder="你的昵称"
                  />
                </div>
              </div>
            )}

            {/* Password */}
            <div className="space-y-1.5">
              <label className="text-xs font-medium uppercase tracking-wider text-zinc-500">
                密码
              </label>
              <div className="relative">
                <input
                  type={showPassword ? 'text' : 'password'}
                  required
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  className="block w-full rounded-lg bg-zinc-900 py-2.5 pl-3 pr-10 text-sm text-white placeholder-zinc-600 ring-1 ring-zinc-800 transition-all focus:outline-none focus:ring-2 focus:ring-zinc-700"
                  placeholder="••••••••"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute inset-y-0 right-0 flex items-center pr-3 text-zinc-500 hover:text-zinc-300"
                >
                  {showPassword ? <EyeSlash className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                </button>
              </div>
            </div>
          </div>

          {error && (
            <div className="text-sm text-red-500 text-center">{error}</div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full cursor-pointer rounded-lg bg-white py-2.5 text-sm font-semibold text-zinc-950 transition-colors hover:bg-zinc-200 disabled:opacity-50"
          >
            {loading ? '处理中...' : isLogin ? '登录' : '创建账号'}
          </button>
        </form>

        {/* Footer */}
        <div className="text-center">
          <button
            onClick={() => setIsLogin(!isLogin)}
            className="text-sm text-zinc-400 hover:text-white transition-colors"
          >
            {isLogin ? '还没有账号？立即注册' : '已有账号？返回登录'}
          </button>
        </div>
      </div>
    </div>
  );
}
