import {Injectable, OnDestroy} from '@angular/core';
import {Client, IMessage, StompSubscription} from '@stomp/stompjs';
import {Subject} from 'rxjs';
import {environnement} from '../../environnement/environnement';

@Injectable({providedIn: 'root'})
export class WebSocketService implements OnDestroy {

  private client: Client;
  private subscriptions = new Map<string, StompSubscription>();

  constructor() {
    this.client = new Client({
      brokerURL: `${environnement.wsBaseUrl}/ws-notifications`,
      reconnectDelay: 5000,
      onConnect: () => console.log('[WS] Connecté ✅'),
      onDisconnect: () => console.log('[WS] Déconnecté ❌'),
      onStompError: (frame) => console.log('[WS] Erreur :', frame)
    });

    this.client.activate();
  }

  /**
   * S'abonne à un topic STOMP et retourne un Subject qui émet les messages reçus.
   * Si déjà abonné à ce topic, retourne sans recréer l'abonnement.
   */
  subscribe(topic: string): Subject<string> {
    const subject = new Subject<string>();

    const onConnect = () => {
      if (this.subscriptions.has(topic)) return;

      const sub = this.client.subscribe(topic, (message: IMessage) => {
        subject.next(message.body);
      });

      this.subscriptions.set(topic, sub);
    };

    if (this.client.connected) {
      onConnect();
    } else {
      this.client.onConnect = onConnect;
    }

    return subject;
  }

  unsubscribe(topic: string): void {
    const sub = this.subscriptions.get(topic);
    if (sub) {
      sub.unsubscribe();
      this.subscriptions.delete(topic);
    }
  }

  ngOnDestroy(): void {
    this.client.deactivate();
  }
}
