import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MemoriaDocumentacionMemoriaModalComponent } from './memoria-documentacion-memoria-modal.component';

describe('MemoriaDocumentacionMemoriaModalComponent', () => {
  let component: MemoriaDocumentacionMemoriaModalComponent;
  let fixture: ComponentFixture<MemoriaDocumentacionMemoriaModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MemoriaDocumentacionMemoriaModalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MemoriaDocumentacionMemoriaModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
