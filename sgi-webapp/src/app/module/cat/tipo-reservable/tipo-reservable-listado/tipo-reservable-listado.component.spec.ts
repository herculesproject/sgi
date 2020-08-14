import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TipoReservableListadoComponent } from './tipo-reservable-listado.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { RouterTestingModule } from '@angular/router/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('TipoReservableListadoComponent', () => {
  let component: TipoReservableListadoComponent;
  let fixture: ComponentFixture<TipoReservableListadoComponent>;

  beforeEach(async(() => {
    // Mock logger
    const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(NGXLogger.name, TestUtils.getOwnMethodNames(NGXLogger.prototype));

    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy }
      ],
      declarations: [TipoReservableListadoComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TipoReservableListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
