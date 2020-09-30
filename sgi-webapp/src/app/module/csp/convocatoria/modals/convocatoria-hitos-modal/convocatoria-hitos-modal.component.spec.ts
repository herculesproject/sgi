import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvocatoriaHitosModalComponent } from './convocatoria-hitos-modal.component';

describe('ConvocatoriaHitosModalComponent', () => {
  let component: ConvocatoriaHitosModalComponent;
  let fixture: ComponentFixture<ConvocatoriaHitosModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConvocatoriaHitosModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaHitosModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
