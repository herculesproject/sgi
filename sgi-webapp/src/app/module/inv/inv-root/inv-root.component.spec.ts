import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiAuthModule, SgiAuthService } from '@sgi/framework/auth';
import { BreadcrumbComponent } from '@shared/breadcrumb/breadcrumb.component';
import { RootComponent } from '@shared/root/root.component';
import { NGXLogger } from 'ngx-logger';

import { InvMenuPrincipalComponent } from '../inv-menu-principal/inv-menu-principal.component';
import { InvRootComponent } from './inv-root.component';

describe('InvRootComponent', () => {
  let component: InvRootComponent;
  let fixture: ComponentFixture<InvRootComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InvRootComponent,
        RootComponent,
        InvMenuPrincipalComponent,
        BreadcrumbComponent
      ],
      imports: [
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        RouterTestingModule.withRoutes([]),
        SgiAuthModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
        SgiAuthService
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InvRootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
