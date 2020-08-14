import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {EbtRootComponent} from './ebt-root.component';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';

describe('EbtRootComponent', () => {
  let component: EbtRootComponent;
  let fixture: ComponentFixture<EbtRootComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [EbtRootComponent],
      imports: [
        TestUtils.getIdiomas(),
      ],
      providers: [{provide: NGXLogger, useValue: TestUtils.getLoggerSpy()}],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EbtRootComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
