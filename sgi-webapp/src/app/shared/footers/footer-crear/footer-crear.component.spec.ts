import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {FooterCrearComponent} from './footer-crear.component';
import {NGXLogger} from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import {RouterTestingModule} from '@angular/router/testing';

describe('FooterCrearComponent', () => {
  let component: FooterCrearComponent;
  let fixture: ComponentFixture<FooterCrearComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FooterCrearComponent],
      imports: [
        RouterTestingModule,
        TestUtils.getIdiomas()
      ],
      providers: [
        {provide: NGXLogger, useValue: TestUtils.getLoggerSpy()},
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FooterCrearComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
