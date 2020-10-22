import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MemoriaInformesComponent } from './memoria-informes.component';

describe('MemoriaInformesComponent', () => {
  let component: MemoriaInformesComponent;
  let fixture: ComponentFixture<MemoriaInformesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MemoriaInformesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriaInformesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
