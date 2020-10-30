import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConceptoGastoListadoComponent } from './concepto-gasto-listado.component';

describe('ConceptoGastoListadoComponent', () => {
  let component: ConceptoGastoListadoComponent;
  let fixture: ComponentFixture<ConceptoGastoListadoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConceptoGastoListadoComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConceptoGastoListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
