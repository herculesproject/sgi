import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AgrupacionServicioDatosGeneralesComponent } from './agrupacion-servicio-datos-generales.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('AgrupacionServicioDatosGeneralesComponent', () => {
  let component: AgrupacionServicioDatosGeneralesComponent;
  let fixture: ComponentFixture<AgrupacionServicioDatosGeneralesComponent>;

  // Mock logger
  const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(
    NGXLogger.name,
    TestUtils.getOwnMethodNames(NGXLogger.prototype)
  );

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy }
      ],
      declarations: [AgrupacionServicioDatosGeneralesComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgrupacionServicioDatosGeneralesComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
