import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {MenuSecundarioComponent} from './menu-secundario.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialDesignModule} from '@material/material-design.module';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import {HttpClientModule} from '@angular/common/http';

describe('MenuSecundarioComponent', () => {
  let component: MenuSecundarioComponent;
  let fixture: ComponentFixture<MenuSecundarioComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule,
        HttpClientModule,
        TestUtils.getIdiomas()
      ],
      providers: [{provide: NGXLogger, useValue: TestUtils.getLoggerSpy()}],
      declarations: [MenuSecundarioComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuSecundarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
