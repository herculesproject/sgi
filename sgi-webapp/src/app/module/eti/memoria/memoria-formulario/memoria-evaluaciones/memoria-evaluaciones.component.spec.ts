import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MemoriaEvaluacionesComponent } from './memoria-evaluaciones.component';

describe('MemoriaEvaluacionesComponent', () => {
  let component: MemoriaEvaluacionesComponent;
  let fixture: ComponentFixture<MemoriaEvaluacionesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MemoriaEvaluacionesComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriaEvaluacionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
