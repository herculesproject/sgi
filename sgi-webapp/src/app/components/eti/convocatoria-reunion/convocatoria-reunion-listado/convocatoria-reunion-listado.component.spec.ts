import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvocatoriaReunionListadoComponent } from './convocatoria-reunion-listado.component';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { FormBuilder } from '@angular/forms';
import { SnackBarService } from '@core/services/snack-bar.service';

describe('ConvocatoriaReunionListadoComponent', () => {
  let component: ConvocatoriaReunionListadoComponent;
  let fixture: ComponentFixture<ConvocatoriaReunionListadoComponent>;

  beforeEach(async(() => {
    // Mock formBuilder
    const formBuilderSpy: jasmine.SpyObj<FormBuilder> = jasmine.createSpyObj(FormBuilder.name,
      TestUtils.getOwnMethodNames(FormBuilder.prototype));

    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() },
        { provide: FormBuilder, useValue: formBuilderSpy }
      ],
      declarations: [ConvocatoriaReunionListadoComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaReunionListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
