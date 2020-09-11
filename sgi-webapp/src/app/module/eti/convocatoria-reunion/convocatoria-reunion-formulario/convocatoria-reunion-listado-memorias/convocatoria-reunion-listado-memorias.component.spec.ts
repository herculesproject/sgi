import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvocatoriaReunionListadoMemoriasComponent } from './convocatoria-reunion-listado-memorias.component';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';

describe('ConvocatoriaReunionListadoMemoriasComponent', () => {
  let component: ConvocatoriaReunionListadoMemoriasComponent;
  let fixture: ComponentFixture<ConvocatoriaReunionListadoMemoriasComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaReunionListadoMemoriasComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaReunionListadoMemoriasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
