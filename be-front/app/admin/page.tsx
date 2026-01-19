'use client';

import { useState, useCallback, useEffect } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { chambreApi, saisonApi, reservationApi, paymentApi } from '@/lib/api';
import { useApi, useMutation, useAuth } from '@/lib/hooks';
import type { Chambre, Saison } from '@/lib/types';

// Tab types
type TabId = 'dashboard' | 'chambres' | 'saisons' | 'reservations' | 'paiements';

// Access Denied Component
function AccessDenied({ reason }: { reason: 'not_authenticated' | 'not_admin' }) {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="text-center max-w-sm">
        <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg className="w-8 h-8 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>
        <h1 className="text-2xl font-light text-gray-900 mb-4">Acces restreint</h1>
        <p className="text-gray-500 mb-6">
          {reason === 'not_authenticated'
            ? 'Vous devez etre connecte pour acceder a l administration.'
            : 'Cette page est reservee aux administrateurs. Votre compte n a pas les permissions necessaires.'}
        </p>
        <div className="space-y-3">
          {reason === 'not_authenticated' ? (
            <Link
              href="/connexion"
              className="block w-full px-6 py-3 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
            >
              Se connecter
            </Link>
          ) : (
            <Link
              href="/"
              className="block w-full px-6 py-3 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
            >
              Retour a l accueil
            </Link>
          )}
        </div>
      </div>
    </div>
  );
}

// Header Component
function AdminHeader() {
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
            <span className="hidden sm:inline text-sm text-gray-500">Administration</span>
          </div>
          {/* Desktop menu */}
          <div className="hidden sm:flex items-center gap-4">
            <span className="text-sm text-gray-600">{user?.prenom} {user?.nom}</span>
            <span className="text-xs px-2 py-1 bg-blue-100 text-blue-800 rounded">ADMIN</span>
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
              <span className="text-sm text-gray-600">{user?.prenom} {user?.nom}</span>
              <span className="text-xs px-2 py-1 bg-blue-100 text-blue-800 rounded">ADMIN</span>
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

// Dashboard Tab
function DashboardTab({ refreshKey }: { refreshKey: number }) {
  const { data: chambres, refetch: refetchChambres } = useApi(useCallback(() => chambreApi.getAll(), []));
  const { data: reservations, refetch: refetchReservations } = useApi(useCallback(() => reservationApi.getAll(), []));
  const { data: paiements, refetch: refetchPaiements } = useApi(useCallback(() => paymentApi.getAll(), []));
  const { data: saisons, refetch: refetchSaisons } = useApi(useCallback(() => saisonApi.getAll(), []));

  // Refetch all data when refreshKey changes
  useEffect(() => {
    if (refreshKey > 0) {
      refetchChambres();
      refetchReservations();
      refetchPaiements();
      refetchSaisons();
    }
  }, [refreshKey, refetchChambres, refetchReservations, refetchPaiements, refetchSaisons]);

  const stats = {
    totalChambres: chambres?.length || 0,
    chambresDisponibles: chambres?.filter(c => c.disponible).length || 0,
    reservationsActives: reservations?.filter(r => r.status === 'CONFIRMED' || r.status === 'PENDING').length || 0,
    paiementsEnAttente: paiements?.filter(p => p.status === 'PENDING').length || 0,
    totalRevenu: paiements?.filter(p => p.status === 'CONFIRMED').reduce((sum, p) => sum + p.amount, 0) || 0,
    saisonsActives: saisons?.length || 0,
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'CONFIRMED': return 'Confirmee';
      case 'PENDING': return 'Paiement en attente';
      case 'CANCELLED': return 'Annulee';
      default: return status;
    }
  };

  return (
    <div className="space-y-6 sm:space-y-8">
      <div>
        <h2 className="text-xl sm:text-2xl font-light text-gray-900 mb-4 sm:mb-6">Tableau de bord</h2>
        <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3 sm:gap-4">
          <div className="bg-white p-4 sm:p-6 border border-gray-200">
            <p className="text-xs sm:text-sm text-gray-500 mb-1">Chambres</p>
            <p className="text-2xl sm:text-3xl font-light text-gray-900">{stats.totalChambres}</p>
          </div>
          <div className="bg-white p-4 sm:p-6 border border-gray-200">
            <p className="text-xs sm:text-sm text-gray-500 mb-1">Disponibles</p>
            <p className="text-2xl sm:text-3xl font-light text-green-600">{stats.chambresDisponibles}</p>
          </div>
          <div className="bg-white p-4 sm:p-6 border border-gray-200">
            <p className="text-xs sm:text-sm text-gray-500 mb-1">Reservations</p>
            <p className="text-2xl sm:text-3xl font-light text-gray-900">{stats.reservationsActives}</p>
          </div>
          <div className="bg-white p-4 sm:p-6 border border-gray-200">
            <p className="text-xs sm:text-sm text-gray-500 mb-1">En attente</p>
            <p className="text-2xl sm:text-3xl font-light text-orange-600">{stats.paiementsEnAttente}</p>
          </div>
          <div className="bg-white p-4 sm:p-6 border border-gray-200">
            <p className="text-xs sm:text-sm text-gray-500 mb-1">Revenu</p>
            <p className="text-2xl sm:text-3xl font-light text-gray-900">{stats.totalRevenu.toFixed(0)} EUR</p>
          </div>
          <div className="bg-white p-4 sm:p-6 border border-gray-200">
            <p className="text-xs sm:text-sm text-gray-500 mb-1">Saisons</p>
            <p className="text-2xl sm:text-3xl font-light text-gray-900">{stats.saisonsActives}</p>
          </div>
        </div>
      </div>

      {/* Recent reservations - card view on mobile, table on desktop */}
      <div>
        <h3 className="text-lg font-medium text-gray-900 mb-4">Reservations recentes</h3>
        {/* Mobile card view */}
        <div className="sm:hidden space-y-3">
          {reservations?.slice(0, 5).map((r) => (
            <div key={r.id} className="bg-white p-4 border border-gray-200">
              <div className="flex justify-between items-start mb-2">
                <span className="text-sm font-medium text-gray-900">#{r.id}</span>
                <span className={`inline-block px-2 py-1 text-xs font-medium ${
                  r.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' :
                  r.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                  'bg-red-100 text-red-800'
                }`}>
                  {getStatusLabel(r.status)}
                </span>
              </div>
              <p className="text-sm text-gray-600">Chambre {r.chambreId}</p>
              <p className="text-xs text-gray-500 mt-1">
                {new Date(r.dateDebut).toLocaleDateString('fr-FR')} - {new Date(r.dateFin).toLocaleDateString('fr-FR')}
              </p>
            </div>
          ))}
          {(!reservations || reservations.length === 0) && (
            <div className="bg-white p-8 border border-gray-200 text-center text-gray-500">
              Aucune reservation
            </div>
          )}
        </div>
        {/* Desktop table view */}
        <div className="hidden sm:block bg-white border border-gray-200 overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Chambre</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Dates</th>
                <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {reservations?.slice(0, 5).map((r) => (
                <tr key={r.id}>
                  <td className="px-4 py-3 text-sm text-gray-900">#{r.id}</td>
                  <td className="px-4 py-3 text-sm text-gray-600">Chambre {r.chambreId}</td>
                  <td className="px-4 py-3 text-sm text-gray-600">
                    {new Date(r.dateDebut).toLocaleDateString('fr-FR')} - {new Date(r.dateFin).toLocaleDateString('fr-FR')}
                  </td>
                  <td className="px-4 py-3">
                    <span className={`inline-block px-2 py-1 text-xs font-medium ${
                      r.status === 'CONFIRMED' ? 'bg-green-100 text-green-800' :
                      r.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-red-100 text-red-800'
                    }`}>
                      {getStatusLabel(r.status)}
                    </span>
                  </td>
                </tr>
              ))}
              {(!reservations || reservations.length === 0) && (
                <tr>
                  <td colSpan={4} className="px-4 py-8 text-center text-gray-500">
                    Aucune reservation
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

// Chambres Tab
function ChambresTab() {
  const { data: chambres, loading, error, refetch } = useApi(
    useCallback(() => chambreApi.getAll(), [])
  );
  const [showForm, setShowForm] = useState(false);
  const [editingChambre, setEditingChambre] = useState<Chambre | null>(null);
  const [form, setForm] = useState({
    numero: '',
    type: 'SIMPLE',
    prixBase: '',
    capacite: '',
    description: '',
    disponible: true,
  });

  const { mutate: createChambre, loading: creating } = useMutation(chambreApi.create);
  const { mutate: updateChambre, loading: updating } = useMutation(
    (data: Chambre) => chambreApi.update(data.id, data)
  );
  const { mutate: deleteChambre } = useMutation(chambreApi.delete);

  const resetForm = () => {
    setForm({ numero: '', type: 'SIMPLE', prixBase: '', capacite: '', description: '', disponible: true });
    setEditingChambre(null);
    setShowForm(false);
  };

  const handleEdit = (chambre: Chambre) => {
    setForm({
      numero: chambre.numero,
      type: chambre.type,
      prixBase: chambre.prixBase.toString(),
      capacite: chambre.capacite.toString(),
      description: chambre.description || '',
      disponible: chambre.disponible,
    });
    setEditingChambre(chambre);
    setShowForm(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const data = {
      numero: form.numero,
      type: form.type,
      prixBase: Number(form.prixBase),
      capacite: Number(form.capacite),
      description: form.description || '',
      disponible: form.disponible,
    };

    if (editingChambre) {
      await updateChambre({ ...data, id: editingChambre.id } as Chambre);
    } else {
      await createChambre(data as Omit<Chambre, 'id'>);
    }
    resetForm();
    refetch();
  };

  const handleDelete = async (id: number) => {
    if (confirm('Supprimer cette chambre ?')) {
      await deleteChambre(id);
      refetch();
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h2 className="text-xl sm:text-2xl font-light text-gray-900">Chambres</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="w-full sm:w-auto px-4 py-2 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
        >
          {showForm ? 'Annuler' : 'Nouvelle chambre'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="bg-white p-4 sm:p-6 border border-gray-200 space-y-4">
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 sm:gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Numero</label>
              <input
                type="text"
                value={form.numero}
                onChange={(e) => setForm({ ...form, numero: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none text-sm"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
              <select
                value={form.type}
                onChange={(e) => setForm({ ...form, type: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none bg-white text-sm"
              >
                <option value="SIMPLE">Simple</option>
                <option value="DOUBLE">Double</option>
                <option value="SUITE">Suite</option>
                <option value="FAMILIALE">Familiale</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Prix/nuit</label>
              <input
                type="number"
                value={form.prixBase}
                onChange={(e) => setForm({ ...form, prixBase: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none text-sm"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Capacite</label>
              <input
                type="number"
                value={form.capacite}
                onChange={(e) => setForm({ ...form, capacite: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none text-sm"
                required
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none text-sm"
              rows={2}
            />
          </div>
          <div className="flex items-center gap-2">
            <input
              type="checkbox"
              id="disponible"
              checked={form.disponible}
              onChange={(e) => setForm({ ...form, disponible: e.target.checked })}
              className="h-4 w-4"
            />
            <label htmlFor="disponible" className="text-sm text-gray-700">Disponible</label>
          </div>
          <button
            type="submit"
            disabled={creating || updating}
            className="w-full sm:w-auto px-4 py-2 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:bg-gray-400 transition-colors"
          >
            {editingChambre ? 'Mettre a jour' : 'Creer'}
          </button>
        </form>
      )}

      {/* Mobile card view */}
      <div className="sm:hidden space-y-3">
        {loading && <div className="bg-white p-8 border border-gray-200 text-center text-gray-500">Chargement...</div>}
        {error && <div className="bg-white p-8 border border-gray-200 text-center text-red-500">Erreur: {error}</div>}
        {chambres?.map((chambre) => (
          <div key={chambre.id} className="bg-white p-4 border border-gray-200">
            <div className="flex justify-between items-start mb-2">
              <div>
                <span className="text-sm font-medium text-gray-900">Chambre {chambre.numero}</span>
                <span className="ml-2 text-xs text-gray-500">{chambre.type}</span>
              </div>
              <span className={`inline-block px-2 py-1 text-xs font-medium ${
                chambre.disponible ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
              }`}>
                {chambre.disponible ? 'Dispo' : 'Occupee'}
              </span>
            </div>
            <div className="text-sm text-gray-600 mb-3">
              <span>{chambre.prixBase} EUR/nuit</span>
              <span className="mx-2">-</span>
              <span>{chambre.capacite} pers.</span>
            </div>
            <div className="flex gap-4">
              <button onClick={() => handleEdit(chambre)} className="text-sm text-gray-600">Modifier</button>
              <button onClick={() => handleDelete(chambre.id)} className="text-sm text-red-600">Supprimer</button>
            </div>
          </div>
        ))}
        {chambres?.length === 0 && (
          <div className="bg-white p-8 border border-gray-200 text-center text-gray-500">Aucune chambre</div>
        )}
      </div>

      {/* Desktop table view */}
      <div className="hidden sm:block bg-white border border-gray-200 overflow-x-auto">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Numero</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Prix</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Capacite</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
              <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-gray-500">Chargement...</td>
              </tr>
            )}
            {error && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-red-500">Erreur: {error}</td>
              </tr>
            )}
            {chambres?.map((chambre) => (
              <tr key={chambre.id}>
                <td className="px-4 py-3 text-sm font-medium text-gray-900">{chambre.numero}</td>
                <td className="px-4 py-3 text-sm text-gray-600">{chambre.type}</td>
                <td className="px-4 py-3 text-sm text-gray-600">{chambre.prixBase} EUR</td>
                <td className="px-4 py-3 text-sm text-gray-600">{chambre.capacite}</td>
                <td className="px-4 py-3">
                  <span className={`inline-block px-2 py-1 text-xs font-medium ${
                    chambre.disponible ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                  }`}>
                    {chambre.disponible ? 'Disponible' : 'Occupee'}
                  </span>
                </td>
                <td className="px-4 py-3 text-right space-x-2">
                  <button
                    onClick={() => handleEdit(chambre)}
                    className="text-sm text-gray-600 hover:text-gray-900"
                  >
                    Modifier
                  </button>
                  <button
                    onClick={() => handleDelete(chambre.id)}
                    className="text-sm text-red-600 hover:text-red-900"
                  >
                    Supprimer
                  </button>
                </td>
              </tr>
            ))}
            {chambres?.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-gray-500">Aucune chambre</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// Saisons Tab
function SaisonsTab() {
  const { data: saisons, loading, error, refetch } = useApi(
    useCallback(() => saisonApi.getAll(), [])
  );
  const [showForm, setShowForm] = useState(false);
  const [editingSaison, setEditingSaison] = useState<Saison | null>(null);
  const [form, setForm] = useState({
    nom: '',
    dateDebut: '',
    dateFin: '',
    coefficientPrix: '',
  });

  const { mutate: createSaison, loading: creating } = useMutation(saisonApi.create);
  const { mutate: updateSaison, loading: updating } = useMutation(
    (data: Saison) => saisonApi.update(data.id, data)
  );
  const { mutate: deleteSaison } = useMutation(saisonApi.delete);

  const resetForm = () => {
    setForm({ nom: '', dateDebut: '', dateFin: '', coefficientPrix: '' });
    setEditingSaison(null);
    setShowForm(false);
  };

  const handleEdit = (saison: Saison) => {
    setForm({
      nom: saison.nom,
      dateDebut: saison.dateDebut,
      dateFin: saison.dateFin,
      coefficientPrix: saison.coefficientPrix.toString(),
    });
    setEditingSaison(saison);
    setShowForm(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const data = {
      nom: form.nom,
      dateDebut: form.dateDebut,
      dateFin: form.dateFin,
      coefficientPrix: Number(form.coefficientPrix),
    };

    if (editingSaison) {
      await updateSaison({ ...data, id: editingSaison.id } as Saison);
    } else {
      await createSaison(data as Omit<Saison, 'id'>);
    }
    resetForm();
    refetch();
  };

  const handleDelete = async (id: number) => {
    if (confirm('Supprimer cette saison ?')) {
      await deleteSaison(id);
      refetch();
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-light text-gray-900">Saisons tarifaires</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="px-4 py-2 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
        >
          {showForm ? 'Annuler' : 'Nouvelle saison'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="bg-white p-6 border border-gray-200 space-y-4">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nom</label>
              <input
                type="text"
                value={form.nom}
                onChange={(e) => setForm({ ...form, nom: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date debut</label>
              <input
                type="date"
                value={form.dateDebut}
                onChange={(e) => setForm({ ...form, dateDebut: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date fin</label>
              <input
                type="date"
                value={form.dateFin}
                onChange={(e) => setForm({ ...form, dateFin: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Coefficient</label>
              <input
                type="number"
                step="0.1"
                value={form.coefficientPrix}
                onChange={(e) => setForm({ ...form, coefficientPrix: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                placeholder="1.0"
                required
              />
            </div>
          </div>
          <button
            type="submit"
            disabled={creating || updating}
            className="px-4 py-2 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:bg-gray-400 transition-colors"
          >
            {editingSaison ? 'Mettre a jour' : 'Creer'}
          </button>
        </form>
      )}

      <div className="bg-white border border-gray-200">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nom</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Periode</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Coefficient</th>
              <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading && (
              <tr>
                <td colSpan={4} className="px-4 py-8 text-center text-gray-500">Chargement...</td>
              </tr>
            )}
            {error && (
              <tr>
                <td colSpan={4} className="px-4 py-8 text-center text-red-500">Erreur: {error}</td>
              </tr>
            )}
            {saisons?.map((saison) => (
              <tr key={saison.id}>
                <td className="px-4 py-3 text-sm font-medium text-gray-900">{saison.nom}</td>
                <td className="px-4 py-3 text-sm text-gray-600">
                  {new Date(saison.dateDebut).toLocaleDateString('fr-FR')} - {new Date(saison.dateFin).toLocaleDateString('fr-FR')}
                </td>
                <td className="px-4 py-3">
                  <span className={`inline-block px-2 py-1 text-xs font-medium ${
                    saison.coefficientPrix > 1 ? 'bg-red-100 text-red-800' :
                    saison.coefficientPrix < 1 ? 'bg-green-100 text-green-800' :
                    'bg-gray-100 text-gray-800'
                  }`}>
                    x{saison.coefficientPrix}
                  </span>
                </td>
                <td className="px-4 py-3 text-right space-x-2">
                  <button
                    onClick={() => handleEdit(saison)}
                    className="text-sm text-gray-600 hover:text-gray-900"
                  >
                    Modifier
                  </button>
                  <button
                    onClick={() => handleDelete(saison.id)}
                    className="text-sm text-red-600 hover:text-red-900"
                  >
                    Supprimer
                  </button>
                </td>
              </tr>
            ))}
            {saisons?.length === 0 && (
              <tr>
                <td colSpan={4} className="px-4 py-8 text-center text-gray-500">Aucune saison</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// Reservations Tab
function ReservationsTab() {
  const { data: reservations, loading, error, refetch } = useApi(
    useCallback(() => reservationApi.getAll(), [])
  );
  const { data: chambres } = useApi(useCallback(() => chambreApi.getAll(), []));
  const [showForm, setShowForm] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const [form, setForm] = useState({
    chambreId: '',
    utilisateurId: '',
    dateDebut: '',
    dateFin: '',
  });

  const { mutate: createReservation, loading: creating } = useMutation(reservationApi.create);
  const { mutate: cancelReservation } = useMutation(
    ({ id, motif }: { id: number; motif: string }) => reservationApi.cancel(id, motif)
  );

  const resetForm = () => {
    setForm({ chambreId: '', utilisateurId: '', dateDebut: '', dateFin: '' });
    setFormError(null);
    setShowForm(false);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);
    try {
      await createReservation({
        chambreId: Number(form.chambreId),
        utilisateurId: Number(form.utilisateurId),
        dateDebut: form.dateDebut,
        dateFin: form.dateFin,
      });
      resetForm();
      refetch();
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erreur inconnue';
      if (errorMessage.includes('409') || errorMessage.toLowerCase().includes('disponible')) {
        setFormError('Cette chambre n\'est pas disponible pour les dates selectionnees. Veuillez choisir d\'autres dates ou une autre chambre.');
      } else if (errorMessage.includes('400')) {
        setFormError('Donnees invalides. Verifiez que la date de depart est apres la date d\'arrivee.');
      } else {
        setFormError('Erreur lors de la creation de la reservation: ' + errorMessage);
      }
    }
  };

  const handleCancel = async (id: number) => {
    const motif = prompt('Motif d annulation:');
    if (motif) {
      await cancelReservation({ id, motif });
      refetch();
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'CONFIRMED': return 'bg-green-100 text-green-800';
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'CANCELLED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'CONFIRMED': return 'Confirmee';
      case 'PENDING': return 'Paiement en attente';
      case 'CANCELLED': return 'Annulee';
      default: return status;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-light text-gray-900">Reservations</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="px-4 py-2 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
        >
          {showForm ? 'Annuler' : 'Nouvelle reservation'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="bg-white p-6 border border-gray-200 space-y-4">
          {formError && (
            <div className="p-4 bg-red-50 border border-red-200 text-red-700 text-sm">
              {formError}
            </div>
          )}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Chambre</label>
              <select
                value={form.chambreId}
                onChange={(e) => { setForm({ ...form, chambreId: e.target.value }); setFormError(null); }}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none bg-white"
                required
              >
                <option value="">Selectionnez</option>
                {chambres?.filter(c => c.disponible).map((chambre) => (
                  <option key={chambre.id} value={chambre.id}>
                    {chambre.numero} - {chambre.type}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">ID Utilisateur</label>
              <input
                type="number"
                value={form.utilisateurId}
                onChange={(e) => setForm({ ...form, utilisateurId: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date arrivee</label>
              <input
                type="date"
                value={form.dateDebut}
                onChange={(e) => { setForm({ ...form, dateDebut: e.target.value }); setFormError(null); }}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date depart</label>
              <input
                type="date"
                value={form.dateFin}
                onChange={(e) => { setForm({ ...form, dateFin: e.target.value }); setFormError(null); }}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                required
              />
            </div>
          </div>
          <button
            type="submit"
            disabled={creating}
            className="px-4 py-2 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:bg-gray-400 transition-colors"
          >
            {creating ? 'Creation...' : 'Creer'}
          </button>
        </form>
      )}

      <div className="bg-white border border-gray-200">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Chambre</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Client</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Dates</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
              <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-gray-500">Chargement...</td>
              </tr>
            )}
            {error && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-red-500">Erreur: {error}</td>
              </tr>
            )}
            {reservations?.map((r) => (
              <tr key={r.id}>
                <td className="px-4 py-3 text-sm font-medium text-gray-900">#{r.id}</td>
                <td className="px-4 py-3 text-sm text-gray-600">
                  {chambres?.find(c => c.id === r.chambreId)?.numero || `#${r.chambreId}`}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600">#{r.utilisateurId}</td>
                <td className="px-4 py-3 text-sm text-gray-600">
                  {new Date(r.dateDebut).toLocaleDateString('fr-FR')} - {new Date(r.dateFin).toLocaleDateString('fr-FR')}
                </td>
                <td className="px-4 py-3">
                  <span className={`inline-block px-2 py-1 text-xs font-medium ${getStatusColor(r.status)}`}>
                    {getStatusLabel(r.status)}
                  </span>
                </td>
                <td className="px-4 py-3 text-right">
                  {r.status !== 'CANCELLED' && (
                    <button
                      onClick={() => handleCancel(r.id)}
                      className="text-sm text-red-600 hover:text-red-900"
                    >
                      Annuler
                    </button>
                  )}
                </td>
              </tr>
            ))}
            {reservations?.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-gray-500">Aucune reservation</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// Paiements Tab - Gestion des statuts de paiement
function PaiementsTab() {
  const { data: paiements, loading, error, refetch } = useApi(
    useCallback(() => paymentApi.getAll(), [])
  );
  const { data: reservations } = useApi(useCallback(() => reservationApi.getAll(), []));
  const { data: chambres } = useApi(useCallback(() => chambreApi.getAll(), []));
  const [editingPayment, setEditingPayment] = useState<number | null>(null);
  const [newStatus, setNewStatus] = useState('');

  const { mutate: updatePayment, loading: updating } = useMutation(
    ({ id, data }: { id: number; data: { paymentMethod: string; status: string } }) =>
      paymentApi.update(id, data)
  );

  const handleStatusChange = async (paymentId: number, currentMethod: string) => {
    if (!newStatus) return;
    await updatePayment({
      id: paymentId,
      data: { paymentMethod: currentMethod, status: newStatus }
    });
    setEditingPayment(null);
    setNewStatus('');
    refetch();
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'CONFIRMED': return 'bg-green-100 text-green-800';
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'CANCELLED': return 'bg-gray-100 text-gray-800';
      case 'REFUNDED': return 'bg-purple-100 text-purple-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'PENDING': return 'En attente';
      case 'CONFIRMED': return 'Confirme';
      case 'CANCELLED': return 'Annule';
      case 'REFUNDED': return 'Rembourse';
      default: return status;
    }
  };

  const getMethodLabel = (method: string) => {
    switch (method) {
      case 'CARTE': return 'Carte bancaire';
      case 'ESPECES': return 'Especes';
      case 'VIREMENT': return 'Virement';
      default: return method;
    }
  };

  const getReservationInfo = (reservationId: number) => {
    const reservation = reservations?.find(r => r.id === reservationId);
    if (!reservation) return null;
    const chambre = chambres?.find(c => c.id === reservation.chambreId);
    return { reservation, chambre };
  };

  // Stats
  const stats = {
    total: paiements?.length || 0,
    pending: paiements?.filter(p => p.status === 'PENDING').length || 0,
    confirmed: paiements?.filter(p => p.status === 'CONFIRMED').length || 0,
    cancelled: paiements?.filter(p => p.status === 'CANCELLED').length || 0,
    refunded: paiements?.filter(p => p.status === 'REFUNDED').length || 0,
    totalAmount: paiements?.filter(p => p.status === 'CONFIRMED')
      .reduce((sum, p) => sum + p.amount, 0) || 0,
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-light text-gray-900 mb-4">Gestion des paiements</h2>
        <p className="text-sm text-gray-500 mb-6">
          Gerez les statuts des paiements associes aux reservations. Les paiements sont crees automatiquement lors d une reservation.
        </p>
      </div>

      {/* Stats cards */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-3">
        <div className="bg-white p-4 border border-gray-200">
          <p className="text-xs text-gray-500 mb-1">Total</p>
          <p className="text-2xl font-light text-gray-900">{stats.total}</p>
        </div>
        <div className="bg-white p-4 border border-gray-200">
          <p className="text-xs text-gray-500 mb-1">En attente</p>
          <p className="text-2xl font-light text-yellow-600">{stats.pending}</p>
        </div>
        <div className="bg-white p-4 border border-gray-200">
          <p className="text-xs text-gray-500 mb-1">Confirmes</p>
          <p className="text-2xl font-light text-green-600">{stats.confirmed}</p>
        </div>
        <div className="bg-white p-4 border border-gray-200">
          <p className="text-xs text-gray-500 mb-1">Annules</p>
          <p className="text-2xl font-light text-gray-600">{stats.cancelled}</p>
        </div>
        <div className="bg-white p-4 border border-gray-200">
          <p className="text-xs text-gray-500 mb-1">Rembourses</p>
          <p className="text-2xl font-light text-purple-600">{stats.refunded}</p>
        </div>
        <div className="bg-white p-4 border border-gray-200">
          <p className="text-xs text-gray-500 mb-1">Revenu</p>
          <p className="text-2xl font-light text-gray-900">{stats.totalAmount.toFixed(0)} EUR</p>
        </div>
      </div>

      {/* Payments table */}
      <div className="bg-white border border-gray-200 overflow-x-auto">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Reservation</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Methode</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
              <th className="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-100">
            {loading && (
              <tr>
                <td colSpan={7} className="px-4 py-8 text-center text-gray-500">Chargement...</td>
              </tr>
            )}
            {error && (
              <tr>
                <td colSpan={7} className="px-4 py-8 text-center text-red-500">Erreur: {error}</td>
              </tr>
            )}
            {paiements?.map((p) => {
              const info = getReservationInfo(p.reservationId);
              return (
                <tr key={p.id}>
                  <td className="px-4 py-3 text-sm font-medium text-gray-900">#{p.id}</td>
                  <td className="px-4 py-3 text-sm text-gray-600">
                    <div>
                      <span className="font-medium">Res. #{p.reservationId}</span>
                      {info && (
                        <span className="text-xs text-gray-400 ml-2">
                          Chambre {info.chambre?.numero || info.reservation.chambreId}
                        </span>
                      )}
                    </div>
                    {info && (
                      <div className="text-xs text-gray-400">
                        {new Date(info.reservation.dateDebut).toLocaleDateString('fr-FR')} - {new Date(info.reservation.dateFin).toLocaleDateString('fr-FR')}
                      </div>
                    )}
                  </td>
                  <td className="px-4 py-3 text-sm font-medium text-gray-900">{p.amount.toFixed(2)} EUR</td>
                  <td className="px-4 py-3 text-sm text-gray-600">{getMethodLabel(p.paymentMethod)}</td>
                  <td className="px-4 py-3 text-sm text-gray-600">
                    {p.paymentDate ? new Date(p.paymentDate).toLocaleDateString('fr-FR') : '-'}
                  </td>
                  <td className="px-4 py-3">
                    {editingPayment === p.id ? (
                      <select
                        value={newStatus}
                        onChange={(e) => setNewStatus(e.target.value)}
                        className="px-2 py-1 text-xs border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none bg-white"
                        autoFocus
                      >
                        <option value="">Choisir...</option>
                        <option value="PENDING">En attente</option>
                        <option value="CONFIRMED">Confirme</option>
                        <option value="CANCELLED">Annule</option>
                        <option value="REFUNDED">Rembourse</option>
                      </select>
                    ) : (
                      <span className={`inline-block px-2 py-1 text-xs font-medium ${getStatusColor(p.status)}`}>
                        {getStatusLabel(p.status)}
                      </span>
                    )}
                  </td>
                  <td className="px-4 py-3 text-right">
                    {editingPayment === p.id ? (
                      <div className="flex justify-end gap-2">
                        <button
                          onClick={() => handleStatusChange(p.id, p.paymentMethod)}
                          disabled={!newStatus || updating}
                          className="text-sm text-green-600 hover:text-green-900 disabled:text-gray-400"
                        >
                          Valider
                        </button>
                        <button
                          onClick={() => { setEditingPayment(null); setNewStatus(''); }}
                          className="text-sm text-gray-600 hover:text-gray-900"
                        >
                          Annuler
                        </button>
                      </div>
                    ) : (
                      <button
                        onClick={() => { setEditingPayment(p.id); setNewStatus(p.status); }}
                        className="text-sm text-gray-600 hover:text-gray-900"
                      >
                        Modifier statut
                      </button>
                    )}
                  </td>
                </tr>
              );
            })}
            {paiements?.length === 0 && (
              <tr>
                <td colSpan={7} className="px-4 py-8 text-center text-gray-500">Aucun paiement</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// Main Admin Page
export default function AdminPage() {
  const [activeTab, setActiveTab] = useState<TabId>('dashboard');
  const [dashboardRefreshKey, setDashboardRefreshKey] = useState(0);
  const { isAuthenticated, user } = useAuth();

  // Check authentication
  if (!isAuthenticated) {
    return <AccessDenied reason="not_authenticated" />;
  }

  // Check admin role
  if (user?.role !== 'ADMIN') {
    return <AccessDenied reason="not_admin" />;
  }

  const tabs: { id: TabId; label: string }[] = [
    { id: 'dashboard', label: 'Tableau de bord' },
    { id: 'chambres', label: 'Chambres' },
    { id: 'saisons', label: 'Saisons' },
    { id: 'reservations', label: 'Reservations' },
    { id: 'paiements', label: 'Paiements' },
  ];

  const handleTabChange = (tabId: TabId) => {
    setActiveTab(tabId);
    // Refresh dashboard data when switching to it
    if (tabId === 'dashboard') {
      setDashboardRefreshKey(prev => prev + 1);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <AdminHeader />

      <div className="max-w-7xl mx-auto px-4 sm:px-6 py-6 sm:py-8">
        {/* Tabs - horizontal scroll on mobile */}
        <div className="border-b border-gray-200 mb-6 sm:mb-8 overflow-x-auto">
          <nav className="flex gap-4 sm:gap-8 min-w-max">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => handleTabChange(tab.id)}
                className={`pb-3 sm:pb-4 text-sm font-medium transition-colors whitespace-nowrap ${
                  activeTab === tab.id
                    ? 'text-gray-900 border-b-2 border-gray-900'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </nav>
        </div>

        {/* Tab Content */}
        {activeTab === 'dashboard' && <DashboardTab refreshKey={dashboardRefreshKey} />}
        {activeTab === 'chambres' && <ChambresTab />}
        {activeTab === 'saisons' && <SaisonsTab />}
        {activeTab === 'reservations' && <ReservationsTab />}
        {activeTab === 'paiements' && <PaiementsTab />}
      </div>
    </div>
  );
}
