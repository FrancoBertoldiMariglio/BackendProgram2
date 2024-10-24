import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'backendProgram2App.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'venta',
    data: { pageTitle: 'backendProgram2App.venta.home.title' },
    loadChildren: () => import('./venta/venta.routes'),
  },
  {
    path: 'dispositivo',
    data: { pageTitle: 'backendProgram2App.dispositivo.home.title' },
    loadChildren: () => import('./dispositivo/dispositivo.routes'),
  },
  {
    path: 'caracteristica',
    data: { pageTitle: 'backendProgram2App.caracteristica.home.title' },
    loadChildren: () => import('./caracteristica/caracteristica.routes'),
  },
  {
    path: 'personalizacion',
    data: { pageTitle: 'backendProgram2App.personalizacion.home.title' },
    loadChildren: () => import('./personalizacion/personalizacion.routes'),
  },
  {
    path: 'opcion',
    data: { pageTitle: 'backendProgram2App.opcion.home.title' },
    loadChildren: () => import('./opcion/opcion.routes'),
  },
  {
    path: 'adicional',
    data: { pageTitle: 'backendProgram2App.adicional.home.title' },
    loadChildren: () => import('./adicional/adicional.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
