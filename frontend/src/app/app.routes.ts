import {Routes} from '@angular/router';

export const routes: Routes = [

  {path: '', redirectTo: 'home-page', pathMatch: 'full'},

  {
    path: 'home-page',
    loadComponent: () =>
      import('./component/home-page/homePage')
        .then(m => m.HomePage),
  },

  {
    path: 'demande-role', loadComponent: () =>
      import('./component/page-admin/page-admin.component')
        .then(m => m.PageAdminComponent),

  },

  {
    path: 'dashboard',
    loadComponent: () =>
      import('./component/dashboard/dashboard-component')
        .then(m => m.DashboardComponent),
  },

  {
    path: 'login',
    loadComponent: () =>
      import('./component/login/login-component')
        .then(m => m.LoginComponent),
  },


  {
    path: 'evenements',
    loadComponent: () =>
      import('./component/evenement/evenement-component')
        .then(m => m.EvenementComponent),
  },

  {
    path: 'evenements/new',
    loadComponent: () =>
      import('./component/evenement/evenement-creation/evenement-creation')
        .then(m => m.EvenementCreation)
  },

  {
    path: 'evenements/:id',
    loadComponent: () =>
      import('./component/evenement/evenement-detail/evenement-detail')
        .then(m => m.EvenementDetailComponent),
  },

  {
    path: 'notification',
    loadComponent: () =>
      import('./component/notification/notification.component')
        .then(m => m.NotificationComponent),
  },

  {
    path: 'utilisateur',
    loadComponent: () =>
      import('./component/utilisateur/utilisateur-component')
        .then(m => m.UtilisateurComponent),
  },

  {
    path: 'sessions',
    loadComponent: () =>
      import('./component/session/session-component')
        .then(m => m.SessionComponent),
  },

  {
    path: 'sessions/new',
    loadComponent: () =>
      import('./component/session/session-creation/session-creation')
        .then(m => m.SessionCreation)
  },

  {
    path: 'evenements/:evenementId/sessions/new',
    loadComponent: () =>
      import('./component/session/session-creation/session-creation')
        .then(m => m.SessionCreation)
  },

  {
    path: 'inscription',
    loadComponent: () =>
      import('./component/inscription/inscription-component')
        .then(m => m.InscriptionComponent),
  },

];
