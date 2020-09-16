import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EquipoInvestigadorCrearModalComponent } from './equipo-investigador-crear-modal.component';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FlexModule } from '@angular/flex-layout';
import { ReactiveFormsModule } from '@angular/forms';
import { NGXLogger } from 'ngx-logger';
import { MatDialogRef } from '@angular/material/dialog';

describe('EquipoInvestigadorCrearModalComponent', () => {
  let component: EquipoInvestigadorCrearModalComponent;
  let fixture: ComponentFixture<EquipoInvestigadorCrearModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EquipoInvestigadorCrearModalComponent],
      imports: [
        RouterTestingModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        BrowserAnimationsModule,
        HttpClientTestingModule,
        FlexModule,
        ReactiveFormsModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: MatDialogRef, useValue: {} }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EquipoInvestigadorCrearModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
