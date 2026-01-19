'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { authApi } from '@/lib/api';
import { useAuth, useMutation } from '@/lib/hooks';

export default function ConnexionPage() {
  const router = useRouter();
  const { login, isAuthenticated, user, logout } = useAuth();
  const [form, setForm] = useState({ username: '', password: '' });

  const { mutate: doLogin, loading, error } = useMutation(authApi.connexion);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const result = await doLogin(form);
    if (result) {
      login({
        username: result.username,
        email: result.email,
        role: result.role,
        prenom: result.prenom,
        nom: result.nom,
      });
      router.push('/admin');
    }
  };

  if (isAuthenticated) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center px-6">
        <div className="w-full max-w-sm">
          <div className="text-center mb-8">
            <Link href="/" className="text-xl font-light tracking-wide text-gray-900">
              HOTEL & SPA
            </Link>
          </div>
          <div className="bg-white p-8 border border-gray-200">
            <div className="text-center mb-6">
              <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
              </div>
              <h2 className="text-lg font-medium text-gray-900">{user?.prenom} {user?.nom}</h2>
              <p className="text-sm text-gray-500">{user?.email}</p>
            </div>
            <div className="space-y-3">
              <Link
                href="/admin"
                className="block w-full px-4 py-3 bg-gray-900 text-white text-sm font-medium text-center hover:bg-gray-800 transition-colors"
              >
                Acceder a l administration
              </Link>
              <button
                onClick={() => { logout(); router.push('/'); }}
                className="block w-full px-4 py-3 border border-gray-200 text-gray-700 text-sm font-medium text-center hover:bg-gray-50 transition-colors"
              >
                Se deconnecter
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-6">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <Link href="/" className="text-xl font-light tracking-wide text-gray-900">
            HOTEL & SPA
          </Link>
          <p className="text-sm text-gray-500 mt-2">Connectez-vous a votre compte</p>
        </div>

        <div className="bg-white p-8 border border-gray-200">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Nom d utilisateur
              </label>
              <input
                type="text"
                value={form.username}
                onChange={(e) => setForm({ ...form, username: e.target.value })}
                className="w-full px-4 py-3 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none transition-colors"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Mot de passe
              </label>
              <input
                type="password"
                value={form.password}
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                className="w-full px-4 py-3 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none transition-colors"
                required
              />
            </div>

            {error && (
              <div className="p-3 bg-red-50 text-red-700 text-sm">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="w-full px-4 py-3 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:bg-gray-400 transition-colors"
            >
              {loading ? 'Connexion...' : 'Se connecter'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-500">
              Pas encore de compte ?{' '}
              <Link href="/auth/inscription" className="text-gray-900 font-medium hover:underline">
                S inscrire
              </Link>
            </p>
          </div>
        </div>

        <div className="mt-6 text-center">
          <Link href="/" className="text-sm text-gray-500 hover:text-gray-900 transition-colors">
            Retour a l accueil
          </Link>
        </div>
      </div>
    </div>
  );
}
