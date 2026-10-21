import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import TestUtils from '@core/utils/test-utils';
import { MaterialDesignModule } from '@material/material-design.module';
import { SharedModule } from '@shared/shared.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { AdmSharedModule } from '../adm-shared.module';
import { ConfigI18nCkeditorEtiComponent } from './config-i18n-ckeditor-eti.component';

describe('ConfigI18nCkeditorEtiComponent', () => {
  let component: ConfigI18nCkeditorEtiComponent;
  let fixture: ComponentFixture<ConfigI18nCkeditorEtiComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        MaterialDesignModule,
        TestUtils.getIdiomas(),
        SharedModule,
        AdmSharedModule
      ],
      declarations: [ConfigI18nCkeditorEtiComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigI18nCkeditorEtiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
