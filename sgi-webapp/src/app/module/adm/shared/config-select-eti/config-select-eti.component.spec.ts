import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SnackBarService } from '@core/services/snack-bar.service';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { ConfigSelectEtiComponent } from './config-select-eti.component';

describe('ConfigSelectEtiComponent', () => {
  let component: ConfigSelectEtiComponent;
  let fixture: ComponentFixture<ConfigSelectEtiComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        MaterialDesignModule,
        TestUtils.getIdiomas(),
        SharedModule
      ],
      declarations: [ConfigSelectEtiComponent],
      providers: [
        { provide: SnackBarService, useValue: TestUtils.getSnackBarServiceSpy() }
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigSelectEtiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
