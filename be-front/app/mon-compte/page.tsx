'use client';

import { Suspense, useState, useCallback, useEffect } from 'react';
import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { reservationApi, chambreApi, paymentApi } from '@/lib/api';
import { useApi, useMutation, useAuth } from '@/lib/hooks';
import type { Reservation, Chambre, Payment } from '@/lib/types';

// Header Component
function UserHeader() {
  const { user, logout } = useAuth();
  const router = useRouter();
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <header className="bg-white border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-4">
        <div className="flex justify-between items-center">
          <div className="flex items-center gap-4 sm:gap-6">
            <Link href="/" className="text-lg sm:text-xl font-light tracking-wide text-gray-900">
              HOTEL & SPA
            </Link>
            <span className="hidden sm:inline text-gray-300">|</span>
            <span className="hidden sm:inline text-sm text-gray-500">Mon compte</span>
          </div>
          {/* Desktop menu */}
          <div className="hidden sm:flex items-center gap-4">
            <span className="text-sm text-gray-600">{user?.prenom || user?.username}</span>
            <button
              onClick={() => { logout(); router.push('/'); }}
              className="text-sm text-gray-500 hover:text-gray-900 transition-colors"
            >
              Deconnexion
            </button>
          </div>
          {/* Mobile menu button */}
          <button
            onClick={() => setMenuOpen(!menuOpen)}
            className="sm:hidden p-2 text-gray-600"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              {menuOpen ? (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              ) : (
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
              )}
            </svg>
          </button>
        </div>
        {/* Mobile menu */}
        {menuOpen && (
          <div className="sm:hidden mt-4 pt-4 border-t border-gray-100">
            <div className="flex items-center justify-between mb-4">
              <span className="text-sm text-gray-600">{user?.prenom || user?.username}</span>
            </div>
            <button
              onClick={() => { logout(); router.push('/'); }}
              className="w-full text-left text-sm text-gray-500 hover:text-gray-900 transition-colors"
            >
              Deconnexion
            </button>
          </div>
        )}
      </div>
    </header>
  );
}

// Access Denied Component
function AccessDenied() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="text-center max-w-sm">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
        </div>
        <h1 className="text-2xl font-light text-gray-900 mb-4">Connexion requise</h1>
        <p className="text-gray-500 mb-6">
          Connectez-vous pour acceder a votre espace personnel et voir vos reservations.
        </p>
        <Link
          href="/connexion"
          className="inline-block w-full px-6 py-3 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
        >
          Se connecter
        </Link>
      </div>
    </div>
  );
}

// Profile Card Component
function ProfileCard() {
  const { user } = useAuth();

  return (
    <div className="bg-white border border-gray-200 p-6">
      <div className="flex items-center gap-4">
        <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center">
          <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
        </div>
        <div>
          <h2 className="text-lg font-medium text-gray-900">
            {user?.prenom && user?.nom ? `${user.prenom} ${user.nom}` : user?.username}
          </h2>
          <p className="text-sm text-gray-500">{user?.email}</p>
        </div>
      </div>
    </div>
  );
}

// Reservation Card Component
function ReservationCard({
  reservation,
  chambre,
  payment,
  onCancel
}: {
  reservation: Reservation;
  chambre?: Chambre;
  payment?: Payment;
  onCancel: (id: number) => void;
}) {
  const getStatusInfo = (status: string) => {
    switch (status) {
      case 'CONFIRMED':
        return { label: 'Confirmee', color: 'bg-green-100 text-green-800', icon: 'M5 13l4 4L19 7' };
      case 'PENDING':
        return { label: 'En attente', color: 'bg-yellow-100 text-yellow-800', icon: 'M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z' };
      case 'CANCELLED':
        return { label: 'Annulee', color: 'bg-red-100 text-red-800', icon: 'M6 18L18 6M6 6l12 12' };
      case 'COMPLETED':
        return { label: 'Terminee', color: 'bg-gray-100 text-gray-800', icon: 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z' };
      default:
        return { label: status, color: 'bg-gray-100 text-gray-800', icon: '' };
    }
  };

  const statusInfo = getStatusInfo(reservation.status);
  const nights = Math.ceil((new Date(reservation.dateFin).getTime() - new Date(reservation.dateDebut).getTime()) / (1000 * 60 * 60 * 24));
  const isPast = new Date(reservation.dateFin) < new Date();
  const canCancel = reservation.status === 'PENDING' || (reservation.status === 'CONFIRMED' && !isPast);

  return (
    <div className="bg-white border border-gray-200 overflow-hidden">
      {/* Header with status */}
      <div className="px-4 sm:px-6 py-4 border-b border-gray-100 flex justify-between items-center">
        <div className="flex items-center gap-3">
          <span className="text-sm font-medium text-gray-900">Reservation #{reservation.id}</span>
          <span className={`inline-flex items-center px-2.5 py-0.5 text-xs font-medium ${statusInfo.color}`}>
            {statusInfo.label}
          </span>
        </div>
        {canCancel && (
          <button
            onClick={() => onCancel(reservation.id)}
            className="text-sm text-red-600 hover:text-red-800"
          >
            Annuler
          </button>
        )}
      </div>

      {/* Content */}
      <div className="p-4 sm:p-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          {/* Room info */}
          <div>
            <p className="text-xs text-gray-500 uppercase tracking-wide mb-1">Chambre</p>
            {chambre ? (
              <div>
                <p className="text-sm font-medium text-gray-900">Chambre {chambre.numero}</p>
                <p className="text-sm text-gray-600">{chambre.type} - {chambre.capacite} personne(s)</p>
              </div>
            ) : (
              <p className="text-sm text-gray-600">Chambre #{reservation.chambreId}</p>
            )}
          </div>

          {/* Dates */}
          <div>
            <p className="text-xs text-gray-500 uppercase tracking-wide mb-1">Dates du sejour</p>
            <p className="text-sm font-medium text-gray-900">
              {new Date(reservation.dateDebut).toLocaleDateString('fr-FR', { weekday: 'short', day: 'numeric', month: 'short' })}
              {' - '}
              {new Date(reservation.dateFin).toLocaleDateString('fr-FR', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' })}
            </p>
            <p className="text-sm text-gray-600">{nights} nuit(s)</p>
          </div>
        </div>

        {/* Payment info */}
        {payment && (
          <div className="mt-4 pt-4 border-t border-gray-100">
            <div className="flex justify-between items-center">
              <div>
                <p className="text-xs text-gray-500 uppercase tracking-wide mb-1">Paiement</p>
                <p className="text-sm text-gray-600">
                  {payment.paymentMethod === 'CARTE' ? 'Carte bancaire' :
                   payment.paymentMethod === 'ESPECES' ? 'Especes' : 'Virement'}
                </p>
              </div>
              <div className="text-right">
                <p className="text-lg font-medium text-gray-900">{payment.amount} EUR</p>
                <span className={`inline-block px-2 py-0.5 text-xs font-medium ${
                  payment.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' :
                  payment.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                  payment.status === 'REFUNDED' ? 'bg-blue-100 text-blue-800' :
                  'bg-gray-100 text-gray-800'
                }`}>
                  {payment.status === 'CONFIRMED' ? 'Paye' :
                   payment.status === 'PENDING' ? 'En attente' :
                   payment.status === 'REFUNDED' ? 'Rembourse' : 'Annule'}
                </span>
              </div>
            </div>
          </div>
        )}

        {/* Reservation date */}
        <div className="mt-4 pt-4 border-t border-gray-100">
          <p className="text-xs text-gray-400">
            Reservee le {new Date(reservation.createdAt).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' })}
          </p>
        </div>
      </div>
    </div>
  );
}

// Empty State Component
function EmptyReservations() {
  return (
    <div className="bg-white border border-gray-200 p-8 sm:p-12 text-center">
      <div className="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
        <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
      </div>
      <h3 className="text-lg font-medium text-gray-900 mb-2">Aucune reservation</h3>
      <p className="text-sm text-gray-500 mb-6 max-w-sm mx-auto">
        Vous n avez pas encore effectue de reservation. Decouvrez nos chambres et reservez votre prochain sejour.
      </p>
      <Link
        href="/#chambres"
        className="inline-block px-6 py-3 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
      >
        Voir nos chambres
      </Link>
    </div>
  );
}

// Main Page Content Component
function MonCompteContent() {
  const { user, isAuthenticated } = useAuth();
  const [filter, setFilter] = useState<'all' | 'upcoming' | 'past'>('all');
  const searchParams = useSearchParams();

  // Check for reservation success from login redirect
  const reservationSuccess = searchParams.get('reservation') === 'success';
  const [showSuccessMessage, setShowSuccessMessage] = useState(reservationSuccess);

  // Clear URL parameter and auto-hide success message
  useEffect(() => {
    if (reservationSuccess) {
      window.history.replaceState({}, '', '/mon-compte');
      const timer = setTimeout(() => setShowSuccessMessage(false), 5000);
      return () => clearTimeout(timer);
    }
  }, [reservationSuccess]);

  // Fetch user's reservations
  const userId = user?.id;
  const { data: reservations, loading: loadingReservations, refetch } = useApi(
    useCallback(() => {
      if (userId) {
        return reservationApi.getByUtilisateur(userId);
      }
      return Promise.resolve([]);
    }, [userId])
  );

  // Fetch all chambres for display
  const { data: chambres } = useApi(
    useCallback(() => chambreApi.getAll(), [])
  );

  // Fetch all payments
  const { data: payments } = useApi(
    useCallback(() => paymentApi.getAll(), [])
  );

  // Cancel mutation
  const { mutate: cancelReservation } = useMutation(
    ({ id, motif }: { id: number; motif: string }) => reservationApi.cancel(id, motif)
  );

  const handleCancel = async (id: number) => {
    const motif = prompt('Motif d annulation (optionnel):') || 'Annulation par le client';
    if (motif !== null) {
      await cancelReservation({ id, motif });
      refetch();
    }
  };

  // Check authentication
  if (!isAuthenticated) {
    return <AccessDenied />;
  }

  // Filter reservations
  const today = new Date();
  const filteredReservations = reservations?.filter(r => {
    if (filter === 'upcoming') {
      return new Date(r.dateDebut) >= today && r.status !== 'CANCELLED';
    }
    if (filter === 'past') {
      return new Date(r.dateFin) < today || r.status === 'COMPLETED';
    }
    return true;
  }) || [];

  // Get chambre and payment for a reservation
  const getChambre = (chambreId: number) => chambres?.find(c => c.id === chambreId);
  const getPayment = (reservationId: number) => payments?.find(p => p.reservationId === reservationId);

  return (
    <div className="min-h-screen bg-gray-50">
      <UserHeader />

      {/* Success notification */}
      {showSuccessMessage && (
        <div className="bg-green-50 border-b border-green-200">
          <div className="max-w-4xl mx-auto px-4 sm:px-6 py-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <svg className="w-5 h-5 text-green-600 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                </svg>
                <p className="text-sm text-green-800">
                  Votre reservation a ete enregistree avec succes !
                </p>
              </div>
              <button
                onClick={() => setShowSuccessMessage(false)}
                className="text-green-600 hover:text-green-800"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          </div>
        </div>
      )}

      <div className="max-w-4xl mx-auto px-4 sm:px-6 py-6 sm:py-8">
        {/* Welcome section */}
        <div className="mb-6 sm:mb-8">
          <h1 className="text-2xl sm:text-3xl font-light text-gray-900 mb-2">
            Bonjour{user?.prenom ? `, ${user.prenom}` : ''}
          </h1>
          <p className="text-gray-500">Gerez vos reservations et suivez vos sejours.</p>
        </div>

        {/* Profile card */}
        <div className="mb-6 sm:mb-8">
          <ProfileCard />
        </div>

        {/* Reservations section */}
        <div>
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-4">
            <h2 className="text-xl font-medium text-gray-900">Mes reservations</h2>
            <div className="flex gap-2">
              <button
                onClick={() => setFilter('all')}
                className={`px-3 py-1.5 text-sm font-medium transition-colors ${
                  filter === 'all'
                    ? 'bg-gray-900 text-white'
                    : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
                }`}
              >
                Toutes
              </button>
              <button
                onClick={() => setFilter('upcoming')}
                className={`px-3 py-1.5 text-sm font-medium transition-colors ${
                  filter === 'upcoming'
                    ? 'bg-gray-900 text-white'
                    : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
                }`}
              >
                A venir
              </button>
              <button
                onClick={() => setFilter('past')}
                className={`px-3 py-1.5 text-sm font-medium transition-colors ${
                  filter === 'past'
                    ? 'bg-gray-900 text-white'
                    : 'bg-white text-gray-600 border border-gray-200 hover:bg-gray-50'
                }`}
              >
                Passees
              </button>
            </div>
          </div>

          {loadingReservations ? (
            <div className="space-y-4">
              {[1, 2].map(i => (
                <div key={i} className="bg-white border border-gray-200 p-6 animate-pulse">
                  <div className="h-4 bg-gray-200 w-1/4 mb-4"></div>
                  <div className="h-4 bg-gray-200 w-1/2 mb-2"></div>
                  <div className="h-4 bg-gray-200 w-1/3"></div>
                </div>
              ))}
            </div>
          ) : filteredReservations.length === 0 ? (
            <EmptyReservations />
          ) : (
            <div className="space-y-4">
              {filteredReservations.map(reservation => (
                <ReservationCard
                  key={reservation.id}
                  reservation={reservation}
                  chambre={getChambre(reservation.chambreId)}
                  payment={getPayment(reservation.id)}
                  onCancel={handleCancel}
                />
              ))}
            </div>
          )}
        </div>

        {/* Quick action */}
        <div className="mt-8 pt-8 border-t border-gray-200">
          <div className="bg-gray-900 text-white p-6 sm:p-8">
            <h3 className="text-lg font-medium mb-2">Envie de revenir ?</h3>
            <p className="text-gray-300 text-sm mb-4">
              Decouvrez nos chambres disponibles et reservez votre prochain sejour.
            </p>
            <Link
              href="/#reservation"
              className="inline-block px-6 py-3 bg-white text-gray-900 text-sm font-medium hover:bg-gray-100 transition-colors"
            >
              Nouvelle reservation
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

// Page Component with Suspense boundary for useSearchParams
export default function MonComptePage() {
  return (
    <Suspense fallback={
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-gray-500">Chargement...</div>
      </div>
    }>
      <MonCompteContent />
    </Suspense>
  );
}
