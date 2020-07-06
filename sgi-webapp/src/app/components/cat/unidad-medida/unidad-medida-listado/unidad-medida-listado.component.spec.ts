import {HttpClientTestingModule} from '@angular/common/http/testing';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterTestingModule} from '@angular/router/testing';
import {SnackBarService} from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import {MaterialDesignModule} from '@material/material-design.module';
import {NGXLogger} from 'ngx-logger';

import {UnidadMedidaListadoComponent} from './unidad-medida-listado.component';

describe('UnidadMedidaListadoComponent', () => {
  let component: UnidadMedidaListadoComponent;
  let fixture: ComponentFixture<UnidadMedidaListadoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        TestUtils.getIdiomas(),
        RouterTestingModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
      ],
      declarations: [UnidadMedidaListadoComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnidadMedidaListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
