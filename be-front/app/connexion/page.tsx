'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { authApi, reservationApi } from '@/lib/api';
import { useAuth, useMutation } from '@/lib/hooks';

interface PendingReservation {
  chambreId: number;
  dateDebut: string;
  dateFin: string;
  paymentMethod?: string;
  prixTotal?: number;
  numeroChambre?: string;
  typeChambre?: string;
}

function getStoredPendingReservation(): PendingReservation | null {
  if (typeof window === 'undefined') return null;
  const pendingStr = localStorage.getItem('pendingReservation');
  if (pendingStr) {
    try {
      return JSON.parse(pendingStr);
    } catch {
      return null;
    }
  }
  return null;
}

export default function ConnexionPage() {
  const router = useRouter();
  const { login, isAuthenticated, user, logout } = useAuth();
  const [form, setForm] = useState({ username: '', password: '' });
  const [pendingReservation] = useState<PendingReservation | null>(getStoredPendingReservation);
  const [reservationError, setReservationError] = useState<string | null>(null);
  const [reservationLoading, setReservationLoading] = useState(false);

  const { mutate: doLogin, loading, error } = useMutation(authApi.connexion);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setReservationError(null);
    const result = await doLogin(form);
    if (result) {
      // Store user data first
      const userData = {
        id: result.id,
        username: result.username,
        email: result.email,
        role: result.role,
        prenom: result.prenom,
        nom: result.nom,
      };

      // Check for pending reservation BEFORE calling login()
      const pendingReservationStr = localStorage.getItem('pendingReservation');
      if (pendingReservationStr) {
        setReservationLoading(true);
        try {
          const pendingReservation = JSON.parse(pendingReservationStr);
          // Create the reservation
          await reservationApi.create({
            chambreId: pendingReservation.chambreId,
            utilisateurId: result.id,
            dateDebut: pendingReservation.dateDebut,
            dateFin: pendingReservation.dateFin,
            paymentMethod: pendingReservation.paymentMethod,
          });
          // Clear the pending reservation
          localStorage.removeItem('pendingReservation');
          // Now login and redirect
          login(userData);
          router.push('/mon-compte?reservation=success');
          return;
        } catch (err) {
          setReservationLoading(false);
          const errorMessage = err instanceof Error ? err.message : 'Erreur inconnue';
          if (errorMessage.includes('409') || errorMessage.toLowerCase().includes('disponible')) {
            setReservationError('Cette chambre n\'est plus disponible pour les dates selectionnees. Veuillez choisir d\'autres dates.');
          } else {
            setReservationError('Erreur lors de la creation de la reservation: ' + errorMessage);
          }
          // Don't clear pending reservation - let user retry or go back to change dates
          // But still login the user
          login(userData);
          return;
        }
      }

      // No pending reservation - just login and redirect
      login(userData);
      if (result.role === 'ADMIN') {
        router.push('/admin');
      } else {
        router.push('/mon-compte');
      }
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
              {user?.role === 'ADMIN' && (
                <Link
                  href="/admin"
                  className="block w-full px-4 py-3 bg-gray-900 text-white text-sm font-medium text-center hover:bg-gray-800 transition-colors"
                >
                  Administration
                </Link>
              )}
              <Link
                href="/mon-compte"
                className="block w-full px-4 py-3 bg-gray-900 text-white text-sm font-medium text-center hover:bg-gray-800 transition-colors"
              >
                Mon compte
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

        {pendingReservation && (
          <div className="bg-blue-50 border border-blue-200 p-4 mb-6">
            <p className="text-sm font-medium text-blue-800 mb-1">
              Reservation en attente
            </p>
            <p className="text-sm text-blue-700">
              Chambre {pendingReservation.numeroChambre || pendingReservation.chambreId}
              {pendingReservation.typeChambre && ` (${pendingReservation.typeChambre})`}
            </p>
            <p className="text-sm text-blue-700">
              Du {new Date(pendingReservation.dateDebut).toLocaleDateString('fr-FR')} au {new Date(pendingReservation.dateFin).toLocaleDateString('fr-FR')}
            </p>
            {pendingReservation.prixTotal && (
              <p className="text-sm font-medium text-blue-800 mt-1">
                Total: {pendingReservation.prixTotal.toFixed(2)} EUR
              </p>
            )}
            <p className="text-xs text-blue-600 mt-2">
              Connectez-vous pour finaliser votre reservation
            </p>
          </div>
        )}

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

            {reservationError && (
              <div className="p-3 bg-red-50 border border-red-200 text-red-700 text-sm">
                <p className="font-medium mb-1">Erreur de reservation</p>
                <p>{reservationError}</p>
                <Link href="/" className="inline-block mt-2 text-red-800 underline text-xs">
                  Retourner a l accueil pour choisir d autres dates
                </Link>
              </div>
            )}

            <button
              type="submit"
              disabled={loading || reservationLoading}
              className="w-full px-4 py-3 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:bg-gray-400 transition-colors"
            >
              {reservationLoading ? 'Creation de la reservation...' : loading ? 'Connexion...' : 'Se connecter'}
            </button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-gray-500">
              Pas encore de compte ?{' '}
              <Link href="/inscription" className="text-gray-900 font-medium hover:underline">
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
