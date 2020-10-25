import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MemoriaFormularioComponent } from './memoria-formulario.component';

describe('MemoriaFormularioComponent', () => {
  let component: MemoriaFormularioComponent;
  let fixture: ComponentFixture<MemoriaFormularioComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MemoriaFormularioComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriaFormularioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
