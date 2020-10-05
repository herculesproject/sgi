import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MemoriaDocumentacionComponent } from './memoria-documentacion.component';

describe('MemoriaDocumentacionComponent', () => {
  let component: MemoriaDocumentacionComponent;
  let fixture: ComponentFixture<MemoriaDocumentacionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MemoriaDocumentacionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriaDocumentacionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
