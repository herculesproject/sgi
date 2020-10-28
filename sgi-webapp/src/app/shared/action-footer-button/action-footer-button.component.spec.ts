import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActionFooterButtonComponent } from './action-footer-button.component';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { MaterialDesignModule } from '@material/material-design.module';

describe('ActionFooterButtonComponent', () => {
  let component: ActionFooterButtonComponent;
  let fixture: ComponentFixture<ActionFooterButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ActionFooterButtonComponent],
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionFooterButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});