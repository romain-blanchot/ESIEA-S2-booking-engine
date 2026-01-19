// Types pour les entites du backend

export interface Chambre {
  id: number;
  numero: string;
  type: string;
  prixBase: number;
  capacite: number;
  description: string;
  disponible: boolean;
}

export interface Saison {
  id: number;
  nom: string;
  dateDebut: string;
  dateFin: string;
  coefficientPrix: number;
}

export interface Reservation {
  id: number;
  chambreId: number;
  utilisateurId: number;
  dateDebut: string;
  dateFin: string;
  status: ReservationStatus;
  createdAt: string;
  cancelledAt: string | null;
}

export type ReservationStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';

export interface Payment {
  id: number;
  reservationId: number;
  amount: number;
  paymentMethod: string;
  status: PaymentStatus;
  paymentDate: string;
}

export type PaymentStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'REFUNDED';

export interface Utilisateur {
  id: number;
  username: string;
  email: string;
  role: string;
  prenom?: string;
  nom?: string;
}

// DTOs pour les requetes

export interface InscriptionRequest {
  username: string;
  password: string;
  email: string;
  nom?: string;
  prenom?: string;
}

export interface ConnexionRequest {
  username: string;
  password: string;
}

export interface ConnexionResponse {
  message: string;
  id: number;
  username: string;
  email: string;
  role: string;
  prenom?: string;
  nom?: string;
}

export interface CalculPrixRequest {
  chambreId: number;
  dateDebut: string;
  dateFin: string;
}

export interface DetailJour {
  date: string;
  saison: string;
  coefficient: number;
  prix: number;
}

export interface ResultatCalculPrix {
  numeroChambre: string;
  typeChambre: string;
  dateDebut: string;
  dateFin: string;
  nombreNuits: number;
  prixBaseParNuit: number;
  coefficientSaisonnier: number;
  prixTotal: number;
  detailsParJour: DetailJour[];
}

export interface DisponibiliteResponse {
  chambreId: number;
  dateDebut: string;
  dateFin: string;
  disponible: boolean;
}

export interface ReservationCreateRequest {
  chambreId: number;
  utilisateurId: number;
  dateDebut: string;
  dateFin: string;
  paymentMethod?: string;
}

export interface PaymentCreateRequest {
  reservationId: number;
  amount: number;
  paymentMethod: string;
}
