import { Routes } from '@angular/router';

import { SolicitudForm } from './components/solicitud-form/solicitud-form';
import { BuscarSolicitud } from './components/buscar-solicitud/buscar-solicitud';
import { SolicitudesRecientes } from './components/solicitudes-recientes/solicitudes-recientes';

export const routes: Routes = [

  {
    path: 'solicitudes/nueva',
    component: SolicitudForm
  },

  {
    path: 'solicitudes/consultar',
    component: BuscarSolicitud
  },

  {
    path: 'solicitudes/historial',
    component: SolicitudesRecientes
  },

  {
    path: '',
    redirectTo: 'solicitudes/nueva',
    pathMatch: 'full'
  },

  {
    path: '**',
    redirectTo: 'solicitudes/nueva'
  }

];