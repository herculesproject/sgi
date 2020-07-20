import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {HeaderComponent} from './header.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialDesignModule} from '@material/material-design.module';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import {RouterTestingModule} from '@angular/router/testing';
import {HttpClientModule} from '@angular/common/http';
import {MenuSecundarioComponent} from '../menu-secundario/menu-secundario.component';
import {ReactiveFormsModule} from '@angular/forms';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        RouterTestingModule,
        HttpClientModule,
        TestUtils.getIdiomas(),
        ReactiveFormsModule
      ],
      providers: [{provide: NGXLogger, useValue: TestUtils.getLoggerSpy()}],
      declarations: [HeaderComponent, MenuSecundarioComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
