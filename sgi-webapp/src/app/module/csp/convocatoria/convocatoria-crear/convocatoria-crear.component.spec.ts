import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConvocatoriaCrearComponent } from './convocatoria-crear.component';

describe('ConvocatoriaCrearComponent', () => {
  let component: ConvocatoriaCrearComponent;
  let fixture: ComponentFixture<ConvocatoriaCrearComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConvocatoriaCrearComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvocatoriaCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
