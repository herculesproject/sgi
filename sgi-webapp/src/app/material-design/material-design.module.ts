import { NgModule } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MAT_CHECKBOX_DEFAULT_OPTIONS, MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MAT_RADIO_DEFAULT_OPTIONS, MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTreeModule } from '@angular/material/tree';
import { TIME_ZONE } from '@core/time-zone';
import { NGX_MAT_DATE_FORMATS, NgxMatDateAdapter, NgxMatDatetimePickerModule } from '@herculesproject/datetime-picker';
import { MatTimepickerModule } from 'mat-timepicker';
import { EndDateDirective } from './end-date.directive';
import { LUXON_DATE_ADAPTER_OPTIONS, LUXON_DATE_FORMATS, LuxonDateAdapter } from './luxon-date-adapter';
import { LUXON_DATETIME_FORMATS, LuxonDateTimeAdapter } from './luxon-datetime-adapter';

@NgModule({
  declarations: [
    EndDateDirective
  ],
  exports: [
    MatSidenavModule,
    MatIconModule,
    MatToolbarModule,
    MatButtonModule,
    MatListModule,
    MatCardModule,
    MatProgressBarModule,
    MatInputModule,
    MatSnackBarModule,
    MatMenuModule,
    MatProgressSpinnerModule,
    MatDatepickerModule,
    MatAutocompleteModule,
    MatTableModule,
    MatDialogModule,
    MatTabsModule,
    MatTooltipModule,
    MatSelectModule,
    MatPaginatorModule,
    MatChipsModule,
    MatButtonToggleModule,
    MatSlideToggleModule,
    MatBadgeModule,
    MatCheckboxModule,
    MatExpansionModule,
    MatSortModule,
    MatGridListModule,
    MatRadioModule,
    FlexLayoutModule,
    MatTreeModule,
    MatStepperModule,
    NgxMatDatetimePickerModule,
    MatTimepickerModule,
    EndDateDirective
  ],
  providers: [
    {
      provide: DateAdapter,
      useClass: LuxonDateAdapter,
      deps: [MAT_DATE_LOCALE, TIME_ZONE, LUXON_DATE_ADAPTER_OPTIONS]
    },
    {
      provide: MAT_DATE_FORMATS,
      useValue: LUXON_DATE_FORMATS
    },
    {
      provide: NgxMatDateAdapter,
      useClass: LuxonDateTimeAdapter,
      deps: [MAT_DATE_LOCALE, TIME_ZONE]
    },
    {
      provide: NGX_MAT_DATE_FORMATS,
      useValue: LUXON_DATETIME_FORMATS
    },
    {
      provide: MAT_RADIO_DEFAULT_OPTIONS,
      useValue: { color: 'primary' }
    },
    {
      provide: MAT_CHECKBOX_DEFAULT_OPTIONS,
      useValue: { color: 'primary' }
    }
  ]
})
export class MaterialDesignModule { }

