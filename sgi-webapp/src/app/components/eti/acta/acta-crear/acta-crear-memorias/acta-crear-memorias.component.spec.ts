import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';

import { ActaCrearMemoriasComponent } from './acta-crear-memorias.component';

describe('ActaCrearMemoriasComponent', () => {
  let component: ActaCrearMemoriasComponent;
  let fixture: ComponentFixture<ActaCrearMemoriasComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ActaCrearMemoriasComponent],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActaCrearMemoriasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
