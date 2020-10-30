import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConceptoGastoModalComponent } from './concepto-gasto-modal.component';

describe('GestionConceptosGastoModalComponent', () => {
  let component: ConceptoGastoModalComponent;
  let fixture: ComponentFixture<ConceptoGastoModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConceptoGastoModalComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConceptoGastoModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
