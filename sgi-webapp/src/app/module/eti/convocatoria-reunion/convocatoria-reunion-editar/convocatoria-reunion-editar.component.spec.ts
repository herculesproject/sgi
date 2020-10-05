import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvocatoriaReunionEditarComponent } from './convocatoria-reunion-editar.component';

describe('ConvocatoriaReunionEditarComponent', () => {
  let component: ConvocatoriaReunionEditarComponent;
  let fixture: ComponentFixture<ConvocatoriaReunionEditarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConvocatoriaReunionEditarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaReunionEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
