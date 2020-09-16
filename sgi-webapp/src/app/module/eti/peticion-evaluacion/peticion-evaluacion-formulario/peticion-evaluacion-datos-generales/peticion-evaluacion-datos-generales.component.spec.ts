import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PeticionEvaluacionDatosGeneralesComponent } from './peticion-evaluacion-datos-generales.component';

describe('PeticionEvaluacionDatosGeneralesComponent', () => {
  let component: PeticionEvaluacionDatosGeneralesComponent;
  let fixture: ComponentFixture<PeticionEvaluacionDatosGeneralesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PeticionEvaluacionDatosGeneralesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PeticionEvaluacionDatosGeneralesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
