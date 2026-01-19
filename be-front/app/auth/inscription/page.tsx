'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { authApi } from '@/lib/api';
import { useMutation } from '@/lib/hooks';

export default function InscriptionPage() {
  const router = useRouter();
  const [success, setSuccess] = useState(false);
  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    nom: '',
    prenom: '',
  });

  const { mutate: doRegister, loading, error } = useMutation(authApi.inscription);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (form.password !== form.confirmPassword) {
      return;
    }

    const result = await doRegister({
      username: form.username,
      email: form.email,
      password: form.password,
      nom: form.nom,
      prenom: form.prenom,
    });

    if (result) {
      setSuccess(true);
      setTimeout(() => {
        router.push('/auth/connexion');
      }, 2000);
    }
  };

  if (success) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center px-6">
        <div className="w-full max-w-sm text-center">
          <div className="mb-8">
            <Link href="/" className="text-xl font-light tracking-wide text-gray-900">
              HOTEL & SPA
            </Link>
          </div>
          <div className="bg-white p-8 border border-gray-200">
            <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg className="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h2 className="text-lg font-medium text-gray-900 mb-2">Compte cree</h2>
            <p className="text-sm text-gray-500">
              Votre compte a ete cree avec succes. Redirection vers la page de connexion...
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center px-6 py-12">
      <div className="w-full max-w-sm">
        <div className="text-center mb-8">
          <Link href="/" className="text-xl font-light tracking-wide text-gray-900">
            HOTEL & SPA
          </Link>
          <p className="text-sm text-gray-500 mt-2">Creez votre compte</p>
        </div>

        <div className="bg-white p-8 border border-gray-200">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Prenom
                </label>
                <input
                  type="text"
                  value={form.prenom}
                  onChange={(e) => setForm({ ...form, prenom: e.target.value })}
                  className="w-full px-4 py-3 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none transition-colors"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Nom
                </label>
                <input
                  type="text"
                  value={form.nom}
                  onChange={(e) => setForm({ ...form, nom: e.target.value })}
                  className="w-full px-4 py-3 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none transition-colors"
                  required
                />
              </div>
            </div>

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
                Email
              </label>
              <input
                type="email"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
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
                minLength={6}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Confirmer le mot de passe
              </label>
              <input
                type="password"
                value={form.confirmPassword}
                onChange={(e) => setForm({ ...form, confirmPassword: e.target.value })}
                className="w-full px-4 py-3 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none transition-colors"
                required
              />
              {form.confirmPassword && form.password !== form.confirmPassword && (
                <p className="mt-1 text-sm text-red-600">Les mots de passe ne correspondent pas</p>
              )}
            </div>

            {error && (
              <div className="p-3 bg-red-50 text-red-700 text-sm">
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading || form.password !== form.confirmPassword}
              className="w-full px-4 py-3 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:bg-gray-400 transition-colors"
            >
              {loading ? 'Creation...' : 'Creer mon compte'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-500">
              Deja un compte ?{' '}
              <Link href="/auth/connexion" className="text-gray-900 font-medium hover:underline">
                Se connecter
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
