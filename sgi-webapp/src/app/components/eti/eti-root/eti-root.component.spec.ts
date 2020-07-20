import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {EtiRootComponent} from './eti-root.component';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import {RootComponent} from '../../root/root.component';
import {MaterialDesignModule} from '@material/material-design.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RouterTestingModule} from '@angular/router/testing';
import {BreadcrumbComponent} from '@shared/breadcrumb/breadcrumb.component';
import {EtiMenuPrincipalComponent} from '../eti-menu-principal/eti-menu-principal.component';

describe('EtiRootComponent', () => {
  let component: EtiRootComponent;
  let fixture: ComponentFixture<EtiRootComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        EtiRootComponent,
        RootComponent,
        EtiMenuPrincipalComponent,
        BreadcrumbComponent
      ],
      imports: [
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        RouterTestingModule.withRoutes([])
      ],
      providers: [
        {provide: NGXLogger, useValue: TestUtils.getLoggerSpy()},
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EtiRootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
