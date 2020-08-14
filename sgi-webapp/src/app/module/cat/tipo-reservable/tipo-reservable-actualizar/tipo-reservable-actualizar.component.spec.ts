import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { MatRadioModule } from '@angular/material/radio';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { NavbarComponent } from '@block/navbar/navbar.component';
import { NGXLogger } from 'ngx-logger';
import i18n from 'src/assets/i18n/es.json';

import { TipoReservableListadoComponent } from '../tipo-reservable-listado/tipo-reservable-listado.component';
import { TipoReservableActualizarComponent } from './tipo-reservable-actualizar.component';


describe('TipoReservableActualizarComponent', () => {
  let component: TipoReservableActualizarComponent;
  let fixture: ComponentFixture<TipoReservableActualizarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TipoReservableActualizarComponent,
        NavbarComponent,
        TipoReservableListadoComponent,
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
        MatRadioModule
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TipoReservableActualizarComponent);
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
      expect(titulo.innerHTML).toBe(i18n.cat['tipo-reservable'].actualizar.titulo);
    });
  });
});
