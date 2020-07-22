import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { ActaCrearAsistentesComponent } from './acta-crear-asistentes.component';

describe('ActaCrearAsistentesComponent', () => {
  let component: ActaCrearAsistentesComponent;
  let fixture: ComponentFixture<ActaCrearAsistentesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        ActaCrearAsistentesComponent,
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActaCrearAsistentesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
