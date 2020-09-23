import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MemoriasListadoComponent } from './memorias-listado.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { MatDialog } from '@angular/material/dialog';
import { Overlay } from '@angular/cdk/overlay';

describe('MemoriasListadoComponent', () => {
  let component: MemoriasListadoComponent;
  let fixture: ComponentFixture<MemoriasListadoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MemoriasListadoComponent],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        MatDialog,
        Overlay
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriasListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
