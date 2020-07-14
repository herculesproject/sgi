import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {AgrupacionServicioGestorComponent} from './agrupacion-servicio-gestor.component';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import {MaterialDesignModule} from '@material/material-design.module';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FlexModule} from '@angular/flex-layout';
import {ReactiveFormsModule} from '@angular/forms';

describe('AgrupacionServicioGestorComponent', () => {
  let component: AgrupacionServicioGestorComponent;
  let fixture: ComponentFixture<AgrupacionServicioGestorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        FlexModule,
      ],
      providers: [
        {provide: NGXLogger, useValue: TestUtils.getLoggerSpy()},
      ],
      declarations: [AgrupacionServicioGestorComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgrupacionServicioGestorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
