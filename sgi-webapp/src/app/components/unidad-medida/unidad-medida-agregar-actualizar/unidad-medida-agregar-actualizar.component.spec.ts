import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnidadMedidaAgregarActualizarComponent } from './unidad-medida-agregar-actualizar.component';
import { NGXLogger } from 'ngx-logger';
import TestUtils from '@core/utils/test-utils';
import { AppRoutingModule } from 'src/app/app-routing.module';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { SnackBarService } from '@core/services/snack-bar.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { from, Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { MaterialDesignModule } from '@material/material-design.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MenuSecundarioComponent } from '@shared/componentes-layout/menu-secundario/menu-secundario.component';
import { ReactiveFormsModule } from '@angular/forms';

class FakeRouter {
  navigate(params) {}
}
class FakeActivatedRoute {
  public params: Observable<any> = from([{ id: '2' }]);
}

describe('UnidadMedidaAgregarActualizarComponent', () => {
  let component: UnidadMedidaAgregarActualizarComponent;
  let fixture: ComponentFixture<UnidadMedidaAgregarActualizarComponent>;
  const loggerSpy: jasmine.SpyObj<NGXLogger> = jasmine.createSpyObj(
    NGXLogger.name,
    TestUtils.getOwnMethodNames(NGXLogger.prototype)
  );

  // Mock SnackBarService
  const snackBarServiceSpy: jasmine.SpyObj<SnackBarService> = jasmine.createSpyObj(
    SnackBarService.name,
    TestUtils.getOwnMethodNames(SnackBarService.prototype)
  );

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        UnidadMedidaAgregarActualizarComponent,
        MenuSecundarioComponent,
      ],
      providers: [
        { provide: NGXLogger, useValue: loggerSpy },
        { provide: SnackBarService, useValue: snackBarServiceSpy },
        { provide: Router, useClass: FakeRouter },
        { provide: ActivatedRoute, useClass: FakeActivatedRoute },
      ],
      imports: [
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: (http: HttpClient) => {
              return new TranslateHttpLoader(http);
            },
            deps: [HttpClient],
          },
        }),
        AppRoutingModule,
        HttpClientTestingModule,
        CommonModule,
        TranslateModule,
        MaterialDesignModule.forRoot(),
        BrowserAnimationsModule,
        FlexLayoutModule,
        ReactiveFormsModule,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnidadMedidaAgregarActualizarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
