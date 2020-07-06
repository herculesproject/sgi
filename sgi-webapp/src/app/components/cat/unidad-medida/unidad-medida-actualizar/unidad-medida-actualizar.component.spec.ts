import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { MenuSecundarioComponent } from '@shared/componentes-layout/menu-secundario/menu-secundario.component';
import { NGXLogger } from 'ngx-logger';
import i18n from 'src/assets/i18n/es.json';

import { UnidadMedidaListadoComponent } from '../unidad-medida-listado/unidad-medida-listado.component';
import { UnidadMedidaActualizarComponent } from './unidad-medida-actualizar.component';

describe('UnidadMedidaActualizarComponent', () => {
  let component: UnidadMedidaActualizarComponent;
  let fixture: ComponentFixture<UnidadMedidaActualizarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        UnidadMedidaActualizarComponent,
        MenuSecundarioComponent,
        UnidadMedidaListadoComponent,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        {
          provide: SnackBarService,
          useValue: TestUtils.getSnackBarServiceSpy(),
        },
      ],
      imports: [
        TestUtils.getIdiomas(),
        RouterTestingModule.withRoutes([]),
        HttpClientTestingModule,
        CommonModule,
        MaterialDesignModule.forRoot(),
        BrowserAnimationsModule,
        FlexLayoutModule,
        ReactiveFormsModule,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnidadMedidaActualizarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('Debe crear el componente', () => {
    expect(component).toBeTruthy();
    expect(component.desactivarAceptar).toBeFalse();
    const botones = fixture.debugElement.queryAll(By.css('button'));
    expect(botones.length).toBe(2);
    fixture.whenStable().then(() => {
      const titulo = fixture.debugElement.query(By.css('h2')).nativeElement;
      expect(titulo.innerHTML).toBe(i18n['unidad-medida'].actualizar.titulo);
    });
  });
});
