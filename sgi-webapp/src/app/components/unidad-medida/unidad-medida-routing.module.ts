import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UnidadMedidaListadoComponent } from './unidad-medida-listado/unidad-medida-listado.component';


const routes: Routes = [
  { path: '', redirectTo: '../unidadesMedida', pathMatch: 'full' },
  {
    path: '../unidadesMedida',
    children: [
      { path: '', component: UnidadMedidaListadoComponent },
    ]
  }
];


@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UnidadMedidaRoutingModule { }
