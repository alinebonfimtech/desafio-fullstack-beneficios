import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/beneficio-list/beneficio-list.component').then(m => m.BeneficioListComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
