import { Component } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialDesignModule } from '@material/material-design.module';
import { SgiHtmlTooltipDirective } from './sgi-html-tooltip.directive';

@Component({
  template: '<span sgiHtmlTooltip="<p><strong>html</strong></p>"></span>'
})
class TestHostComponent { }

describe('SgiHtmlTooltipDirective', () => {
  let fixture: ComponentFixture<TestHostComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        SgiHtmlTooltipDirective,
        TestHostComponent
      ],
      imports: [
        BrowserAnimationsModule,
        MaterialDesignModule
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.debugElement.query(By.directive(SgiHtmlTooltipDirective))).toBeTruthy();
  });
});
