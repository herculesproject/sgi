import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectorModuloComponent } from './selector-modulo.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { MatDialogRef } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';

describe('SelectorModuloComponent', () => {
  let component: SelectorModuloComponent;
  let fixture: ComponentFixture<SelectorModuloComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SelectorModuloComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        RouterTestingModule,
        FormsModule,
        ReactiveFormsModule,
        SgiAuthModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: MatDialogRef, useValue: {} },
        SgiAuthService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectorModuloComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
