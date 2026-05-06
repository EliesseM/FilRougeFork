export interface Notification {
  notificationId: number;
  titre: string;
  description: string;
  date: Date;
  isLu: boolean;
  utilisateurId: string;
  evenementId?: number;
  sessionId?: number;
}
