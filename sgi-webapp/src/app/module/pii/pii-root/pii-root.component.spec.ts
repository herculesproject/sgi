import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PiiRootComponent} from './pii-root.component';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('PiiRootComponent', () => {
  let component: PiiRootComponent;
  let fixture: ComponentFixture<PiiRootComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PiiRootComponent],
      imports: [
        TestUtils.getIdiomas()
      ],
      providers: [{provide: NGXLogger, useValue: TestUtils.getLoggerSpy()}],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PiiRootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
