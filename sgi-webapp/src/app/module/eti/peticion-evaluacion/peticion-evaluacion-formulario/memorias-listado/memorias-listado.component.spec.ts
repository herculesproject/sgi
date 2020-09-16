import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MemoriasListadoComponent } from './memorias-listado.component';

describe('MemoriasListadoComponent', () => {
  let component: MemoriasListadoComponent;
  let fixture: ComponentFixture<MemoriasListadoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MemoriasListadoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriasListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
