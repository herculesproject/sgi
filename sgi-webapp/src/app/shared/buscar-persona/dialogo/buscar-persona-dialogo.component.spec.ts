import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BuscarPersonaDialogoComponent } from './buscar-persona-dialogo.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { HeaderComponent } from '@block/header/header.component';

describe('BuscarPersonaDialogoComponent', () => {
  let component: BuscarPersonaDialogoComponent;
  let fixture: ComponentFixture<BuscarPersonaDialogoComponent>;

  beforeEach(async(() => {
    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(
      NGXLogger.name,
      TestUtils.getOwnMethodNames(NGXLogger.prototype)
    );

    const mockDialogRef = {
      close: jasmine.createSpy('close'),
    };

    // Mock MAT_DIALOG
    const matDialogData = {};

    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        MatDialogModule,
        TestUtils.getIdiomas(),
      ],
      providers: [
        {
          provide: MatDialogRef,
          useValue: mockDialogRef,
        },
        { provide: NGXLogger, useValue: loggerSpy },
        { provide: MAT_DIALOG_DATA, useValue: matDialogData },
      ],
      declarations: [BuscarPersonaDialogoComponent, HeaderComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BuscarPersonaDialogoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
