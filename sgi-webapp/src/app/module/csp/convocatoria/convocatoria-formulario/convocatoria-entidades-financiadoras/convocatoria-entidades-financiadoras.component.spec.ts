import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FlexModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { NGXLogger } from 'ngx-logger';
import { ConvocatoriaActionService } from '../../convocatoria.action.service';

import { ConvocatoriaEntidadesFinanciadorasComponent } from './convocatoria-entidades-financiadoras.component';

describe('ConvocatoriaEntidadesFinanciadorasComponent', () => {
  let component: ConvocatoriaEntidadesFinanciadorasComponent;
  let fixture: ComponentFixture<ConvocatoriaEntidadesFinanciadorasComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ConvocatoriaEntidadesFinanciadorasComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
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
    fixture = TestBed.createComponent(ConvocatoriaEntidadesFinanciadorasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
