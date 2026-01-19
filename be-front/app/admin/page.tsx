'use client';

import { useState, useCallback } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { chambreApi, saisonApi, reservationApi, paymentApi } from '@/lib/api';
import { useApi, useMutation, useAuth } from '@/lib/hooks';
import type { Chambre, Saison, Reservation, Payment } from '@/lib/types';

// Tab types
type TabId = 'dashboard' | 'chambres' | 'saisons' | 'reservations' | 'paiements';

// Header Component
function AdminHeader() {
  const { user, logout, isAuthenticated } = useAuth();
  const router = useRouter();

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <h1 className="text-2xl font-light text-gray-900 mb-4">Acces restreint</h1>
          <p className="text-gray-500 mb-6">Vous devez etre connecte pour acceder a l administration.</p>
          <Link
            href="/auth/connexion"
            className="px-6 py-3 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
          >
            Se connecter
          </Link>
        </div>
      </div>
    );
  }

  return (
    <header className="bg-white border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-6 py-4">
        <div className="flex justify-between items-center">
          <div className="flex items-center gap-6">
            <Link href="/" className="text-xl font-light tracking-wide text-gray-900">
              HOTEL & SPA
            </Link>
            <span className="text-gray-300">|</span>
            <span className="text-sm text-gray-500">Administration</span>
          </div>
          <div className="flex items-center gap-4">
            <span className="text-sm text-gray-600">{user?.prenom} {user?.nom}</span>
            <button
              onClick={() => { logout(); router.push('/'); }}
              className="text-sm text-gray-500 hover:text-gray-900 transition-colors"
            >
              Deconnexion
            </button>
          </div>
        </div>
      </div>
    </header>
  );
}

// Dashboard Tab
function DashboardTab() {
  const { data: chambres } = useApi(useCallback(() => chambreApi.getAll(), []));
  const { data: reservations } = useApi(useCallback(() => reservationApi.getAll(), []));
  const { data: paiements } = useApi(useCallback(() => paymentApi.getAll(), []));
  const { data: saisons } = useApi(useCallback(() => saisonApi.getAll(), []));

  const stats = {
    totalChambres: chambres?.length || 0,
    chambresDisponibles: chambres?.filter(c => c.disponible).length || 0,
    reservationsActives: reservations?.filter(r => r.status === 'CONFIRMED' || r.status === 'PENDING').length || 0,
    paiementsEnAttente: paiements?.filter(p => p.status === 'PENDING').length || 0,
    totalRevenu: paiements?.filter(p => p.status === 'COMPLETED').reduce((sum, p) => sum + p.amount, 0) || 0,
    saisonsActives: saisons?.length || 0,
  };

  return (
    <div className="space-y-8">
      <div>
        <h2 className="text-2xl font-light text-gray-900 mb-6">Tableau de bord</h2>
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          <div className="bg-white p-6 border border-gray-200">
            <p className="text-sm text-gray-500 mb-1">Chambres</p>
            <p className="text-3xl font-light text-gray-900">{stats.totalChambres}</p>
          </div>
          <div className="bg-white p-6 border border-gray-200">
            <p className="text-sm text-gray-500 mb-1">Disponibles</p>
            <p className="text-3xl font-light text-green-600">{stats.chambresDisponibles}</p>
          </div>
          <div className="bg-white p-6 border border-gray-200">
            <p className="text-sm text-gray-500 mb-1">Reservations</p>
            <p className="text-3xl font-light text-gray-900">{stats.reservationsActives}</p>
          </div>
          <div className="bg-white p-6 border border-gray-200">
            <p className="text-sm text-gray-500 mb-1">Paiements en attente</p>
            <p className="text-3xl font-light text-orange-600">{stats.paiementsEnAttente}</p>
          </div>
          <div className="bg-white p-6 border border-gray-200">
            <p className="text-sm text-gray-500 mb-1">Revenu total</p>
            <p className="text-3xl font-light text-gray-900">{stats.totalRevenu.toFixed(0)} EUR</p>
          </div>
          <div className="bg-white p-6 border border-gray-200">
            <p className="text-sm text-gray-500 mb-1">Saisons</p>
            <p className="text-3xl font-light text-gray-900">{stats.saisonsActives}</p>
          </div>
        </div>
      </div>

      {/* Recent reservations */}
      <div>
        <h3 className="text-lg font-medium text-gray-900 mb-4">Reservations recentes</h3>
        <div className="bg-white border border-gray-200">
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
                      {r.status}
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
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-light text-gray-900">Chambres</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="px-4 py-2 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
        >
          {showForm ? 'Annuler' : 'Nouvelle chambre'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="bg-white p-6 border border-gray-200 space-y-4">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Numero</label>
              <input
                type="text"
                value={form.numero}
                onChange={(e) => setForm({ ...form, numero: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Type</label>
              <select
                value={form.type}
                onChange={(e) => setForm({ ...form, type: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none bg-white"
              >
                <option value="SIMPLE">Simple</option>
                <option value="DOUBLE">Double</option>
                <option value="SUITE">Suite</option>
                <option value="FAMILIALE">Familiale</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Prix/nuit (EUR)</label>
              <input
                type="number"
                value={form.prixBase}
                onChange={(e) => setForm({ ...form, prixBase: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Capacite</label>
              <input
                type="number"
                value={form.capacite}
                onChange={(e) => setForm({ ...form, capacite: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                required
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
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
            className="px-4 py-2 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:bg-gray-400 transition-colors"
          >
            {editingChambre ? 'Mettre a jour' : 'Creer'}
          </button>
        </form>
      )}

      <div className="bg-white border border-gray-200">
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
    setShowForm(false);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await createReservation({
      chambreId: Number(form.chambreId),
      utilisateurId: Number(form.utilisateurId),
      dateDebut: form.dateDebut,
      dateFin: form.dateFin,
    });
    resetForm();
    refetch();
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
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Chambre</label>
              <select
                value={form.chambreId}
                onChange={(e) => setForm({ ...form, chambreId: e.target.value })}
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
                onChange={(e) => setForm({ ...form, dateDebut: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date depart</label>
              <input
                type="date"
                value={form.dateFin}
                onChange={(e) => setForm({ ...form, dateFin: e.target.value })}
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
            Creer
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
                    {r.status}
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

// Paiements Tab
function PaiementsTab() {
  const { data: paiements, loading, error, refetch } = useApi(
    useCallback(() => paymentApi.getAll(), [])
  );
  const { data: reservations } = useApi(useCallback(() => reservationApi.getAll(), []));
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    reservationId: '',
    amount: '',
    paymentMethod: 'CARTE',
  });

  const { mutate: createPayment, loading: creating } = useMutation(paymentApi.create);

  const resetForm = () => {
    setForm({ reservationId: '', amount: '', paymentMethod: 'CARTE' });
    setShowForm(false);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await createPayment({
      reservationId: Number(form.reservationId),
      amount: Number(form.amount),
      paymentMethod: form.paymentMethod,
    });
    resetForm();
    refetch();
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'FAILED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-light text-gray-900">Paiements</h2>
        <button
          onClick={() => setShowForm(!showForm)}
          className="px-4 py-2 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
        >
          {showForm ? 'Annuler' : 'Nouveau paiement'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="bg-white p-6 border border-gray-200 space-y-4">
          <div className="grid grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Reservation</label>
              <select
                value={form.reservationId}
                onChange={(e) => setForm({ ...form, reservationId: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none bg-white"
                required
              >
                <option value="">Selectionnez</option>
                {reservations?.filter(r => r.status !== 'CANCELLED').map((r) => (
                  <option key={r.id} value={r.id}>
                    #{r.id} - Chambre {r.chambreId}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Montant (EUR)</label>
              <input
                type="number"
                value={form.amount}
                onChange={(e) => setForm({ ...form, amount: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none"
                required
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Methode</label>
              <select
                value={form.paymentMethod}
                onChange={(e) => setForm({ ...form, paymentMethod: e.target.value })}
                className="w-full px-3 py-2 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none bg-white"
              >
                <option value="CARTE">Carte bancaire</option>
                <option value="ESPECES">Especes</option>
                <option value="VIREMENT">Virement</option>
              </select>
            </div>
          </div>
          <button
            type="submit"
            disabled={creating}
            className="px-4 py-2 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:bg-gray-400 transition-colors"
          >
            Creer
          </button>
        </form>
      )}

      <div className="bg-white border border-gray-200">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Reservation</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Montant</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Methode</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th className="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
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
            {paiements?.map((p) => (
              <tr key={p.id}>
                <td className="px-4 py-3 text-sm font-medium text-gray-900">#{p.id}</td>
                <td className="px-4 py-3 text-sm text-gray-600">#{p.reservationId}</td>
                <td className="px-4 py-3 text-sm font-medium text-gray-900">{p.amount} EUR</td>
                <td className="px-4 py-3 text-sm text-gray-600">{p.paymentMethod}</td>
                <td className="px-4 py-3 text-sm text-gray-600">
                  {p.paymentDate ? new Date(p.paymentDate).toLocaleDateString('fr-FR') : '-'}
                </td>
                <td className="px-4 py-3">
                  <span className={`inline-block px-2 py-1 text-xs font-medium ${getStatusColor(p.status)}`}>
                    {p.status}
                  </span>
                </td>
              </tr>
            ))}
            {paiements?.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-8 text-center text-gray-500">Aucun paiement</td>
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
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <AdminHeader />;
  }

  const tabs: { id: TabId; label: string }[] = [
    { id: 'dashboard', label: 'Tableau de bord' },
    { id: 'chambres', label: 'Chambres' },
    { id: 'saisons', label: 'Saisons' },
    { id: 'reservations', label: 'Reservations' },
    { id: 'paiements', label: 'Paiements' },
  ];

  return (
    <div className="min-h-screen bg-gray-50">
      <AdminHeader />

      <div className="max-w-7xl mx-auto px-6 py-8">
        {/* Tabs */}
        <div className="border-b border-gray-200 mb-8">
          <nav className="flex gap-8">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`pb-4 text-sm font-medium transition-colors ${
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
        {activeTab === 'dashboard' && <DashboardTab />}
        {activeTab === 'chambres' && <ChambresTab />}
        {activeTab === 'saisons' && <SaisonsTab />}
        {activeTab === 'reservations' && <ReservationsTab />}
        {activeTab === 'paiements' && <PaiementsTab />}
      </div>
    </div>
  );
}
