import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';

import { ConvocatoriaSeguimientoCientificoComponent } from './convocatoria-seguimiento-cientifico.component';

describe('ConvocatoriaSeguimientoCientificoComponent', () => {
  let component: ConvocatoriaSeguimientoCientificoComponent;
  let fixture: ComponentFixture<ConvocatoriaSeguimientoCientificoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaSeguimientoCientificoComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FormsModule,
        ReactiveFormsModule,
        RouterTestingModule,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        ConvocatoriaActionService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaSeguimientoCientificoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
