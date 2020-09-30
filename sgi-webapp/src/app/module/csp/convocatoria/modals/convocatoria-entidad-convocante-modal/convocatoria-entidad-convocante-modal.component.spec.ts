import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvocatoriaEntidadConvocanteaModalComponent } from './convocatoria-entidad-convocante-modal.component';

describe('ConvocatoriaEntidadConvocanteaModalComponent', () => {
  let component: ConvocatoriaEntidadConvocanteaModalComponent;
  let fixture: ComponentFixture<ConvocatoriaEntidadConvocanteaModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConvocatoriaEntidadConvocanteaModalComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaEntidadConvocanteaModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
