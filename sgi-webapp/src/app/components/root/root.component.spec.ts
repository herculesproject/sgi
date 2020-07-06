import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {RootComponent} from './root.component';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MaterialDesignModule} from '@material/material-design.module';
import {RouterTestingModule} from '@angular/router/testing';
import {BreadcrumbComponent} from '@shared/breadcrumb/breadcrumb.component';

describe('RootComponent', () => {
  let component: RootComponent;
  let fixture: ComponentFixture<RootComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        RootComponent,
        BreadcrumbComponent
      ],
      imports: [
        TestUtils.getIdiomas(),
        BrowserAnimationsModule,
        MaterialDesignModule,
        RouterTestingModule
      ],
      providers: [
        {provide: NGXLogger, useValue: TestUtils.getLoggerSpy()},
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
