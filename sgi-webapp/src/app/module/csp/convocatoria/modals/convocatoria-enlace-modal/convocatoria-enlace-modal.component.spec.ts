import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvocatoriaEnlaceModalComponent } from './convocatoria-enlace-modal.component';

describe('ConvocatoriaEnlaceModalComponent', () => {
  let component: ConvocatoriaEnlaceModalComponent;
  let fixture: ComponentFixture<ConvocatoriaEnlaceModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConvocatoriaEnlaceModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaEnlaceModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
