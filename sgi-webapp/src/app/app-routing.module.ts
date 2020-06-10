import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RootComponent } from './components/root/root.component';

/**
 * Definimos las urls de la aplicación
 */
const routes: Routes = [
  {
    path: 'agrupacionesServicios',
    component: RootComponent,
  },
  {
    path: 'horarios',
    component: RootComponent,
  },
  {
    path: 'productos',
    component: RootComponent,
  },
  {
    path: 'solicitudes',
    component: RootComponent,
  },
  {
    path: 'tiposFungible',
    component: RootComponent,
  },
  {
    path: 'tiposReservables',
    component: RootComponent,
  },
  {
    path: 'unidadesMedida',
    component: RootComponent,
  },
  /**
   * Si no reconoce la url
   */
  { path: '**', component: RootComponent },
];

/**
 * Módulo para definir las url de entrada de la aplicación
 */
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
