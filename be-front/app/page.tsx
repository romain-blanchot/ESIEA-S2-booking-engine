'use client';

import { useState, useCallback } from 'react';
import Link from 'next/link';
import { chambreApi, prixApi } from '@/lib/api';
import { useApi } from '@/lib/hooks';
import type { Chambre, ResultatCalculPrix } from '@/lib/types';

function Header() {
  return (
    <header className="absolute top-0 left-0 right-0 z-50">
      <div className="max-w-7xl mx-auto px-6 py-4">
        <div className="flex justify-between items-center">
          <Link href="/" className="text-xl font-light tracking-wide text-white">
            HOTEL & SPA
          </Link>
          <nav className="hidden md:flex items-center gap-8">
            <a href="#chambres" className="text-sm text-white/80 hover:text-white transition-colors">
              Nos Chambres
            </a>
            <a href="#reservation" className="text-sm text-white/80 hover:text-white transition-colors">
              Reserver
            </a>
            <Link href="/auth/connexion" className="text-sm text-white/80 hover:text-white transition-colors">
              Connexion
            </Link>
            <Link
              href="/auth/inscription"
              className="text-sm px-4 py-2 border border-white/30 text-white hover:bg-white hover:text-gray-900 transition-colors"
            >
              Inscription
            </Link>
          </nav>
        </div>
      </div>
    </header>
  );
}

function Hero() {
  return (
    <section className="relative h-screen bg-gray-900">
      <div
        className="absolute inset-0 bg-cover bg-center"
        style={{
          backgroundImage: 'url(https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070)',
          filter: 'brightness(0.4)',
        }}
      />
      <div className="relative h-full flex items-center justify-center text-center px-6">
        <div className="max-w-3xl">
          <p className="text-white/70 text-sm tracking-[0.3em] uppercase mb-4">
            Bienvenue
          </p>
          <h1 className="text-5xl md:text-7xl font-light text-white mb-6 leading-tight">
            Un havre de paix<br />au coeur de la ville
          </h1>
          <p className="text-white/70 text-lg mb-8 max-w-xl mx-auto">
            Decouvrez nos chambres elegantes et profitez d un sejour inoubliable
            dans notre etablissement.
          </p>
          <a
            href="#reservation"
            className="inline-block px-8 py-3 bg-white text-gray-900 text-sm font-medium hover:bg-gray-100 transition-colors"
          >
            Reserver maintenant
          </a>
        </div>
      </div>
      <div className="absolute bottom-8 left-1/2 -translate-x-1/2">
        <a href="#chambres" className="block animate-bounce">
          <svg className="w-6 h-6 text-white/50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 14l-7 7m0 0l-7-7m7 7V3" />
          </svg>
        </a>
      </div>
    </section>
  );
}

function RoomCard({ chambre }: { chambre: Chambre }) {
  const typeImages: Record<string, string> = {
    SIMPLE: 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?q=80&w=800',
    DOUBLE: 'https://images.unsplash.com/photo-1590490360182-c33d57733427?q=80&w=800',
    SUITE: 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?q=80&w=800',
    FAMILIALE: 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?q=80&w=800',
  };

  return (
    <div className="group">
      <div className="relative overflow-hidden aspect-[4/3] mb-4">
        <img
          src={typeImages[chambre.type] || typeImages.SIMPLE}
          alt={`Chambre ${chambre.numero}`}
          className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
        />
        <div className="absolute top-4 right-4">
          <span className={`px-3 py-1 text-xs font-medium ${
            chambre.disponible
              ? 'bg-green-100 text-green-800'
              : 'bg-red-100 text-red-800'
          }`}>
            {chambre.disponible ? 'Disponible' : 'Occupee'}
          </span>
        </div>
      </div>
      <div>
        <div className="flex justify-between items-start mb-2">
          <h3 className="text-lg font-medium text-gray-900">
            Chambre {chambre.numero}
          </h3>
          <p className="text-lg font-medium text-gray-900">
            {chambre.prixBase} EUR<span className="text-sm text-gray-500">/nuit</span>
          </p>
        </div>
        <p className="text-sm text-gray-500 mb-2">
          {chambre.type} - {chambre.capacite} {chambre.capacite > 1 ? 'personnes' : 'personne'}
        </p>
        {chambre.description && (
          <p className="text-sm text-gray-600 line-clamp-2">{chambre.description}</p>
        )}
      </div>
    </div>
  );
}

function RoomsSection() {
  const { data: chambres, loading, error } = useApi(
    useCallback(() => chambreApi.getAll(), [])
  );

  if (loading) {
    return (
      <section id="chambres" className="py-24 px-6">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <p className="text-gray-500 text-sm tracking-[0.2em] uppercase mb-2">Decouvrez</p>
            <h2 className="text-4xl font-light text-gray-900">Nos Chambres</h2>
          </div>
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {[1, 2, 3].map((i) => (
              <div key={i} className="animate-pulse">
                <div className="aspect-[4/3] bg-gray-200 mb-4" />
                <div className="h-6 bg-gray-200 w-1/2 mb-2" />
                <div className="h-4 bg-gray-200 w-1/3" />
              </div>
            ))}
          </div>
        </div>
      </section>
    );
  }

  if (error) {
    return (
      <section id="chambres" className="py-24 px-6">
        <div className="max-w-7xl mx-auto text-center">
          <p className="text-red-600">Erreur lors du chargement des chambres</p>
        </div>
      </section>
    );
  }

  return (
    <section id="chambres" className="py-24 px-6">
      <div className="max-w-7xl mx-auto">
        <div className="text-center mb-16">
          <p className="text-gray-500 text-sm tracking-[0.2em] uppercase mb-2">Decouvrez</p>
          <h2 className="text-4xl font-light text-gray-900">Nos Chambres</h2>
        </div>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {chambres?.map((chambre) => (
            <RoomCard key={chambre.id} chambre={chambre} />
          ))}
        </div>
        {(!chambres || chambres.length === 0) && (
          <p className="text-center text-gray-500">Aucune chambre disponible pour le moment.</p>
        )}
      </div>
    </section>
  );
}

function ReservationSection() {
  const { data: chambres } = useApi(useCallback(() => chambreApi.getAll(), []));
  const [dateDebut, setDateDebut] = useState('');
  const [dateFin, setDateFin] = useState('');
  const [chambreId, setChambreId] = useState('');
  const [resultat, setResultat] = useState<ResultatCalculPrix | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleCalcul = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!dateDebut || !dateFin || !chambreId) return;

    setLoading(true);
    setError(null);
    setResultat(null);

    try {
      const result = await prixApi.calculer({
        chambreId: Number(chambreId),
        dateDebut,
        dateFin,
      });
      setResultat(result);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur lors du calcul');
    } finally {
      setLoading(false);
    }
  };

  const chambreSelectionnee = chambres?.find(c => c.id === Number(chambreId));

  return (
    <section id="reservation" className="py-24 px-6 bg-gray-50">
      <div className="max-w-4xl mx-auto">
        <div className="text-center mb-16">
          <p className="text-gray-500 text-sm tracking-[0.2em] uppercase mb-2">Planifiez</p>
          <h2 className="text-4xl font-light text-gray-900">Votre Sejour</h2>
        </div>

        <div className="bg-white p-8 shadow-sm">
          <form onSubmit={handleCalcul} className="space-y-6">
            <div className="grid md:grid-cols-3 gap-6">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Date d arrivee
                </label>
                <input
                  type="date"
                  value={dateDebut}
                  onChange={(e) => setDateDebut(e.target.value)}
                  min={new Date().toISOString().split('T')[0]}
                  className="w-full px-4 py-3 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none transition-colors"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Date de depart
                </label>
                <input
                  type="date"
                  value={dateFin}
                  onChange={(e) => setDateFin(e.target.value)}
                  min={dateDebut || new Date().toISOString().split('T')[0]}
                  className="w-full px-4 py-3 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none transition-colors"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Chambre
                </label>
                <select
                  value={chambreId}
                  onChange={(e) => setChambreId(e.target.value)}
                  className="w-full px-4 py-3 border border-gray-200 focus:border-gray-900 focus:ring-0 outline-none transition-colors bg-white"
                  required
                >
                  <option value="">Selectionnez</option>
                  {chambres?.filter(c => c.disponible).map((chambre) => (
                    <option key={chambre.id} value={chambre.id}>
                      {chambre.numero} - {chambre.type} ({chambre.prixBase} EUR/nuit)
                    </option>
                  ))}
                </select>
              </div>
            </div>
            <div className="flex justify-center">
              <button
                type="submit"
                disabled={loading}
                className="px-8 py-3 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 disabled:bg-gray-400 transition-colors"
              >
                {loading ? 'Calcul en cours...' : 'Calculer le prix'}
              </button>
            </div>
          </form>

          {error && (
            <div className="mt-6 p-4 bg-red-50 text-red-700 text-sm">
              {error}
            </div>
          )}

          {resultat && (
            <div className="mt-8 pt-8 border-t border-gray-100">
              <div className="text-center mb-6">
                <p className="text-sm text-gray-500 mb-1">Prix total pour {resultat.nombreNuits} nuit(s)</p>
                <p className="text-4xl font-light text-gray-900">{resultat.prixTotal.toFixed(2)} EUR</p>
              </div>

              {resultat.detailsParJour && resultat.detailsParJour.length > 0 && (
                <div className="mt-6">
                  <p className="text-sm font-medium text-gray-700 mb-3">Detail par nuit</p>
                  <div className="space-y-2 max-h-48 overflow-y-auto">
                    {resultat.detailsParJour.map((detail, index) => (
                      <div key={index} className="flex justify-between items-center py-2 border-b border-gray-50 text-sm">
                        <div>
                          <span className="text-gray-900">{new Date(detail.date).toLocaleDateString('fr-FR')}</span>
                          {detail.saison && (
                            <span className="ml-2 text-gray-500">({detail.saison})</span>
                          )}
                        </div>
                        <div className="text-right">
                          <span className="text-gray-900">{detail.prix.toFixed(2)} EUR</span>
                          {detail.coefficient !== 1 && (
                            <span className="ml-2 text-xs text-gray-500">x{detail.coefficient}</span>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              <div className="mt-6 flex justify-center">
                <Link
                  href={`/auth/inscription?redirect=/`}
                  className="px-8 py-3 bg-gray-900 text-white text-sm font-medium hover:bg-gray-800 transition-colors"
                >
                  Reserver cette chambre
                </Link>
              </div>
            </div>
          )}
        </div>
      </div>
    </section>
  );
}

function Footer() {
  return (
    <footer className="bg-gray-900 text-white py-16 px-6">
      <div className="max-w-7xl mx-auto">
        <div className="grid md:grid-cols-4 gap-12">
          <div className="md:col-span-2">
            <h3 className="text-xl font-light tracking-wide mb-4">HOTEL & SPA</h3>
            <p className="text-gray-400 text-sm leading-relaxed max-w-md">
              Un etablissement d exception au coeur de la ville.
              Decouvrez nos chambres elegantes et nos services haut de gamme
              pour un sejour inoubliable.
            </p>
          </div>
          <div>
            <h4 className="text-sm font-medium uppercase tracking-wider mb-4">Contact</h4>
            <ul className="space-y-2 text-sm text-gray-400">
              <li>12 Avenue des Champs</li>
              <li>75008 Paris, France</li>
              <li>+33 1 23 45 67 89</li>
              <li>contact@hotel-spa.fr</li>
            </ul>
          </div>
          <div>
            <h4 className="text-sm font-medium uppercase tracking-wider mb-4">Navigation</h4>
            <ul className="space-y-2 text-sm text-gray-400">
              <li><a href="#" className="hover:text-white transition-colors">Accueil</a></li>
              <li><a href="#chambres" className="hover:text-white transition-colors">Nos Chambres</a></li>
              <li><a href="#reservation" className="hover:text-white transition-colors">Reserver</a></li>
              <li><Link href="/admin" className="hover:text-white transition-colors">Administration</Link></li>
            </ul>
          </div>
        </div>
        <div className="border-t border-gray-800 mt-12 pt-8 text-center text-sm text-gray-500">
          2024 Hotel & Spa. Tous droits reserves.
        </div>
      </div>
    </footer>
  );
}

export default function HomePage() {
  return (
    <div className="min-h-screen">
      <Header />
      <Hero />
      <RoomsSection />
      <ReservationSection />
      <Footer />
    </div>
  );
}
