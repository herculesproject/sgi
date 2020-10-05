import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MemoriaDocumentacionSeguimientosModalComponent } from './memoria-documentacion-seguimientos-modal.component';

describe('MemoriaDocumentacionSeguimientosModalComponent', () => {
  let component: MemoriaDocumentacionSeguimientosModalComponent;
  let fixture: ComponentFixture<MemoriaDocumentacionSeguimientosModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MemoriaDocumentacionSeguimientosModalComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriaDocumentacionSeguimientosModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
