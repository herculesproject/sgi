import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActionFooterComponent } from './action-footer.component';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { MaterialDesignModule } from '@material/material-design.module';

describe('ActionFooterComponent', () => {
  let component: ActionFooterComponent;
  let fixture: ComponentFixture<ActionFooterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ActionFooterComponent],
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
    fixture = TestBed.createComponent(ActionFooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
