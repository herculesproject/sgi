import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActaListadoComponent } from './acta-listado.component';
import { RouterTestingModule } from '@angular/router/testing';
import { MaterialDesignModule } from '@material/material-design.module';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { SnackBarService } from '@core/services/snack-bar.service';

describe('ActaListadoComponent', () => {
  let component: ActaListadoComponent;
  let fixture: ComponentFixture<ActaListadoComponent>;

  beforeEach(async(() => {


    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        MaterialDesignModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() }
      ],
      declarations: [ActaListadoComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActaListadoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
