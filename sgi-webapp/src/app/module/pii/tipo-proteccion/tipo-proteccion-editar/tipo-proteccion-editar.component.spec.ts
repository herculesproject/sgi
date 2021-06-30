import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TipoProteccionEditarComponent } from './tipo-proteccion-editar.component';

describe('TipoProteccionEditarComponent', () => {
  let component: TipoProteccionEditarComponent;
  let fixture: ComponentFixture<TipoProteccionEditarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TipoProteccionEditarComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TipoProteccionEditarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
