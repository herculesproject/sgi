import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericTabLabelComponent } from './generic-tab-label.component';
import TestUtils from '@core/utils/test-utils';
import { NGXLogger } from 'ngx-logger';
import { MaterialDesignModule } from '@material/material-design.module';

describe('GenericTabLabelComponent', () => {
  let component: GenericTabLabelComponent;
  let fixture: ComponentFixture<GenericTabLabelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        TestUtils.getIdiomas(),
        MaterialDesignModule
      ],
      providers: [
        { provide: NGXLogger, useValue: TestUtils.getLoggerSpy() },
      ],
      declarations: [GenericTabLabelComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericTabLabelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
