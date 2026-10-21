import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import TestUtils from '@core/utils/test-utils';
import { FieldInfoHtmlComponent } from './field-info-html.component';

describe('FieldInfoHtmlComponent', () => {
  let component: FieldInfoHtmlComponent;
  let fixture: ComponentFixture<FieldInfoHtmlComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [FieldInfoHtmlComponent],
      imports: [
        TestUtils.getIdiomas()
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FieldInfoHtmlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
