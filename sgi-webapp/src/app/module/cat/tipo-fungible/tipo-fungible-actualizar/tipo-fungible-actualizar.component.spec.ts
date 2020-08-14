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
import { NavbarComponent } from '@block/navbar/navbar.component';
import { NGXLogger } from 'ngx-logger';
import i18n from 'src/assets/i18n/es.json';

import { TipoFungibleListadoComponent } from '../tipo-fungible-listado/tipo-fungible-listado.component';
import { TipoFungibleActualizarComponent } from './tipo-fungible-actualizar.component';

describe('TipoFungibleActualizarComponent', () => {
  let component: TipoFungibleActualizarComponent;
  let fixture: ComponentFixture<TipoFungibleActualizarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TipoFungibleActualizarComponent,
        NavbarComponent,
        TipoFungibleListadoComponent,
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
        MaterialDesignModule,
        BrowserAnimationsModule,
        FlexLayoutModule,
        ReactiveFormsModule,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TipoFungibleActualizarComponent);
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
      expect(titulo.innerHTML).toBe(i18n.cat['tipo-fungible'].actualizar.titulo);
    });
  });
});
