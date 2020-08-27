import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FooterGuardarComponent } from './footer-guardar.component';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { MaterialDesignModule } from '@material/material-design.module';

describe('FooterGuardarComponent', () => {
  let component: FooterGuardarComponent;
  let fixture: ComponentFixture<FooterGuardarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FooterGuardarComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FooterGuardarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
