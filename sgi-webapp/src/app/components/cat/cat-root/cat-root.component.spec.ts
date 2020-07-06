import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { BreadcrumbComponent } from '@shared/breadcrumb/breadcrumb.component';
import { NGXLogger } from 'ngx-logger';

import { RootComponent } from '../../root/root.component';
import { CatMenuPrincipalComponent } from '../cat-menu-principal/cat-menu-principal.component';
import { CatRootComponent } from './cat-root.component';

describe('CatRootComponent', () => {
  let component: CatRootComponent;
  let fixture: ComponentFixture<CatRootComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CatRootComponent,
        RootComponent,
        CatMenuPrincipalComponent,
        BreadcrumbComponent
      ],
      imports: [
        BrowserAnimationsModule,
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        RouterTestingModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CatRootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
