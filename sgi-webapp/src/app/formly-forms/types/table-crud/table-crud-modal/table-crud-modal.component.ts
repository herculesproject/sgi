import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { marker } from '@biesbjerg/ngx-translate-extract-marker';
import { MSG_PARAMS } from '@core/i18n';
import { FormlyFieldConfig } from '@ngx-formly/core';
import { TranslateService } from '@ngx-translate/core';
import { switchMap } from 'rxjs/operators';

const TITLE_NEW_ENTITY = marker('title.new.entity');

export interface TableCRUDModalData {
  fieldGroup: FormlyFieldConfig[];
  formModel: any;
  entity: {
    name: string;
    gender: string;
  };
}

@Component({
  templateUrl: './table-crud-modal.component.html',
  styleUrls: ['./table-crud-modal.component.scss']
})
export class TableCRUDModalComponent implements OnInit {
  title: string;
  private entity: string;
  private gender: string;

  formGroup: FormGroup = new FormGroup({});
  fields: FormlyFieldConfig[];
  model: any = {};

  constructor(
    public readonly matDialogRef: MatDialogRef<TableCRUDModalComponent>,
    private readonly translate: TranslateService,
    @Inject(MAT_DIALOG_DATA) public tableCRUDModalData: TableCRUDModalData,
  ) { }

  ngOnInit(): void {
    this.initFormlyData();

    this.setupI18N();
  }

  private initFormlyData() {
    if (this.tableCRUDModalData?.fieldGroup) {
      this.fields = [...this.tableCRUDModalData.fieldGroup];
    }
    this.model = this.tableCRUDModalData?.formModel ? this.tableCRUDModalData.formModel : {};
    this.entity = this.tableCRUDModalData?.entity?.name;
    this.gender = this.tableCRUDModalData?.entity?.gender;
  }

  /**
   * Checks the formGroup, returns the entered data and closes the modal
   */
  saveOrUpdate(): void {
    this.formGroup.markAllAsTouched();

    if (this.formGroup.valid) {
      this.matDialogRef.close(this.model);
    }
  }

  private setupI18N(): void {
    if (this.entity) {
      const gender = this.gender === MSG_PARAMS.GENDER.MALE.gender ? MSG_PARAMS.GENDER.MALE : MSG_PARAMS.GENDER.FEMALE;

      if (this.tableCRUDModalData.formModel) {
        this.translate.get(
          this.entity,
          MSG_PARAMS.CARDINALIRY.SINGULAR
        ).subscribe((value) => this.title = value);
      } else {
        this.translate.get(
          this.entity,
          MSG_PARAMS.CARDINALIRY.SINGULAR
        ).pipe(
          switchMap((value) => {
            return this.translate.get(
              TITLE_NEW_ENTITY,
              { entity: value, ...gender }
            );
          })
        ).subscribe((value) => this.title = value);
      }
    }
  }
}

