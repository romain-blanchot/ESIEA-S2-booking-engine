// Service API pour communiquer avec le backend

import type {
  Chambre,
  Saison,
  Reservation,
  Payment,
  InscriptionRequest,
  ConnexionRequest,
  ConnexionResponse,
  CalculPrixRequest,
  ResultatCalculPrix,
  DisponibiliteResponse,
  ReservationCreateRequest,
  PaymentCreateRequest,
} from './types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:80';

async function fetchApi<T>(endpoint: string, options?: RequestInit): Promise<T> {
  const url = `${API_BASE_URL}${endpoint}`;
  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(`API Error ${response.status}: ${errorText}`);
  }

  if (response.status === 204) {
    return null as T;
  }

  return response.json();
}

// ============== CHAMBRES ==============

export const chambreApi = {
  getAll: () => fetchApi<Chambre[]>('/chambres'),

  getById: (id: number) => fetchApi<Chambre>(`/chambres/${id}`),

  getDisponibles: () => fetchApi<Chambre[]>('/chambres/disponibles'),

  getByType: (type: string) => fetchApi<Chambre[]>(`/chambres/type/${type}`),

  create: (chambre: Omit<Chambre, 'id'>) =>
    fetchApi<Chambre>('/chambres', {
      method: 'POST',
      body: JSON.stringify(chambre),
    }),

  update: (id: number, chambre: Chambre) =>
    fetchApi<Chambre>(`/chambres/${id}`, {
      method: 'PUT',
      body: JSON.stringify(chambre),
    }),

  delete: (id: number) =>
    fetchApi<void>(`/chambres/${id}`, { method: 'DELETE' }),

  checkDisponibilite: (id: number, dateDebut: string, dateFin: string) =>
    fetchApi<DisponibiliteResponse>(
      `/chambres/${id}/disponibilite?dateDebut=${dateDebut}&dateFin=${dateFin}`
    ),

  getDisponiblesPourPeriode: (dateDebut: string, dateFin: string) =>
    fetchApi<Chambre[]>(
      `/chambres/disponibles/periode?dateDebut=${dateDebut}&dateFin=${dateFin}`
    ),
};

// ============== SAISONS ==============

export const saisonApi = {
  getAll: () => fetchApi<Saison[]>('/saisons'),

  getById: (id: number) => fetchApi<Saison>(`/saisons/${id}`),

  create: (saison: Omit<Saison, 'id'>) =>
    fetchApi<Saison>('/saisons', {
      method: 'POST',
      body: JSON.stringify(saison),
    }),

  update: (id: number, saison: Saison) =>
    fetchApi<Saison>(`/saisons/${id}`, {
      method: 'PUT',
      body: JSON.stringify(saison),
    }),

  delete: (id: number) =>
    fetchApi<void>(`/saisons/${id}`, { method: 'DELETE' }),
};

// ============== RESERVATIONS ==============

export const reservationApi = {
  getAll: () => fetchApi<Reservation[]>('/reservations'),

  getById: (id: number) => fetchApi<Reservation>(`/reservations/${id}`),

  getByStatus: (status: string) =>
    fetchApi<Reservation[]>(`/reservations/status/${status}`),

  getByChambre: (chambreId: number) =>
    fetchApi<Reservation[]>(`/reservations/chambre/${chambreId}`),

  getByUtilisateur: (utilisateurId: number) =>
    fetchApi<Reservation[]>(`/reservations/utilisateur/${utilisateurId}`),

  create: (reservation: ReservationCreateRequest) =>
    fetchApi<Reservation>('/reservations', {
      method: 'POST',
      body: JSON.stringify(reservation),
    }),

  update: (id: number, reservation: { dateDebut: string; dateFin: string; status: string }) =>
    fetchApi<Reservation>(`/reservations/${id}`, {
      method: 'PUT',
      body: JSON.stringify(reservation),
    }),

  cancel: (id: number, reason?: string) =>
    fetchApi<Reservation>(`/reservations/${id}/cancel${reason ? `?reason=${encodeURIComponent(reason)}` : ''}`, {
      method: 'PUT',
    }),

  delete: (id: number) =>
    fetchApi<void>(`/reservations/${id}`, { method: 'DELETE' }),
};

// ============== PAYMENTS ==============

export const paymentApi = {
  getAll: () => fetchApi<Payment[]>('/payments'),

  getById: (id: number) => fetchApi<Payment>(`/payments/${id}`),

  getByReservation: (reservationId: number) =>
    fetchApi<Payment[]>(`/payments/reservation/${reservationId}`),

  create: (payment: PaymentCreateRequest) =>
    fetchApi<Payment>('/payments', {
      method: 'POST',
      body: JSON.stringify(payment),
    }),

  update: (id: number, payment: { paymentMethod: string; status: string }) =>
    fetchApi<Payment>(`/payments/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payment),
    }),

  delete: (id: number) =>
    fetchApi<void>(`/payments/${id}`, { method: 'DELETE' }),
};

// ============== PRIX ==============

export const prixApi = {
  calculer: (request: CalculPrixRequest) =>
    fetchApi<ResultatCalculPrix>('/prix/calculer', {
      method: 'POST',
      body: JSON.stringify(request),
    }),
};

// ============== AUTH ==============

export const authApi = {
  inscription: (request: InscriptionRequest) =>
    fetchApi<{ message: string; username: string }>('/auth/inscription', {
      method: 'POST',
      body: JSON.stringify(request),
    }),

  connexion: (request: ConnexionRequest) =>
    fetchApi<ConnexionResponse>('/auth/connexion', {
      method: 'POST',
      body: JSON.stringify(request),
    }),
};
