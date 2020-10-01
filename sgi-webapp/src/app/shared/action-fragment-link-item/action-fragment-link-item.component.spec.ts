import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActionFragmentLinkItemComponent } from './action-fragment-link-item.component';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { MaterialDesignModule } from '@material/material-design.module';
import { RouterTestingModule } from '@angular/router/testing';

describe('ActionFragmentLinkItemComponent', () => {
  let component: ActionFragmentLinkItemComponent;
  let fixture: ComponentFixture<ActionFragmentLinkItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule,
        RouterTestingModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
      declarations: [ActionFragmentLinkItemComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionFragmentLinkItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
