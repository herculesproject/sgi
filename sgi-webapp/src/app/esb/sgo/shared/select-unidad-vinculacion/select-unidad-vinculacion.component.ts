import { FocusMonitor } from '@angular/cdk/a11y';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import { hasModifierKey } from '@angular/cdk/keycodes';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Input, OnDestroy, OnInit, Optional, Self } from '@angular/core';
import { ControlValueAccessor, FormControl, NgControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { IUnidadVinculacion } from '@core/models/sgo/unidad-vinculacion';
import { Subject } from 'rxjs';
import { SearchUnidadVinculacionModalComponent, SearchUnidadVinculacionModalData } from './dialog/search-unidad-vinculacion.component';

@Component({
  selector: 'sgi-select-unidad-vinculacion',
  templateUrl: './select-unidad-vinculacion.component.html',
  styleUrls: ['./select-unidad-vinculacion.component.scss'],
  // tslint:disable-next-line: no-inputs-metadata-property
  inputs: ['disabled', 'disableRipple'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // tslint:disable-next-line: no-host-metadata-property
  host: {
    role: 'search',
    'aria-autocomplete': 'none',
    '[attr.id]': 'id',
    '[attr.aria-label]': 'ariaLabel || null',
    '[attr.aria-required]': 'required.toString()',
    '[attr.aria-disabled]': 'disabled.toString()',
    '[attr.aria-invalid]': 'errorState',
    '[attr.aria-describedby]': 'ariaDescribedby || null',
    '(keydown)': 'handleKeydown($event)'
  },
  providers: [
    {
      provide: MatFormFieldControl,
      useExisting: SelectUnidadVinculacionComponent
    }
  ]
})
export class SelectUnidadVinculacionComponent
  implements MatFormFieldControl<IUnidadVinculacion>, ControlValueAccessor, OnInit, OnDestroy {

  static nextId = 0;
  id = `sgi-select-unidad-vinculacion-${SelectUnidadVinculacionComponent.nextId++}`;
  stateChanges = new Subject<void>();
  focused = false;
  controlType = 'input-button';
  errorState = false;
  autofilled?: boolean;
  userAriaDescribedBy?: string;

  // tslint:disable-next-line: variable-name
  private _value: IUnidadVinculacion;
  // tslint:disable-next-line: variable-name
  private _placeholder: string;
  // tslint:disable-next-line: variable-name
  private _required = false;
  // tslint:disable-next-line: variable-name
  private _disabled = false;

  inputControl = new FormControl('');

  // tslint:disable-next-line: variable-name
  private _destroy = new Subject<void>();

  /** Refs a excluir de la selección (ya asignadas). */
  @Input() excludedRefs: string[] = [];

  /** Si es true, sólo carga unidades activas. */
  @Input() onlyActive = false;

  /** Aria label of the select. If not specified, the placeholder will be used as label. */
  @Input('aria-label') ariaLabel = '';

  /** Input that can be used to specify the `aria-describedby` attribute. */
  @Input('aria-describedby') ariaDescribedby = '';

  private onChange: (value: IUnidadVinculacion) => void = () => { };
  private onTouched = () => { };

  @Input()
  get placeholder(): string {
    return this._placeholder || '';
  }
  set placeholder(value: string) {
    this._placeholder = value;
    this.stateChanges.next();
  }

  @Input()
  get required(): boolean {
    return this._required;
  }
  set required(value: boolean) {
    this._required = coerceBooleanProperty(value);
    this.stateChanges.next();
  }

  @Input()
  get disabled(): boolean {
    return this._disabled;
  }
  set disabled(value: boolean) {
    this._disabled = coerceBooleanProperty(value);
    this._disabled ? this.inputControl.disable() : this.inputControl.enable();
    this.stateChanges.next();
  }

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private elementRef: ElementRef,
    private dialog: MatDialog,
    @Self() @Optional() public ngControl: NgControl,
    private focusMonitor: FocusMonitor
  ) {
    if (this.ngControl) {
      this.ngControl.valueAccessor = this;
    }
    this.id = this.id;
  }

  ngOnInit(): void { }

  showDialog(): void {
    const dialogRef = this.dialog.open<SearchUnidadVinculacionModalComponent>(SearchUnidadVinculacionModalComponent, {
      data: {
        selectedUnidadesVinculacionRefs: this.excludedRefs ?? [],
        onlyActive: this.onlyActive
      } as SearchUnidadVinculacionModalData
    });

    dialogRef.afterClosed().subscribe(result => {
      this.onTouched();
      if (result !== undefined && result !== null) {
        this.propagateChanges(result);
      }
    });
  }

  get value(): IUnidadVinculacion {
    return this._value;
  }

  set value(value: IUnidadVinculacion) {
    if (value !== this._value) {
      this._value = value;
      this.propagateChanges(value);
    }
  }

  writeValue(value: IUnidadVinculacion): void {
    if (value !== this._value) {
      this._value = value;
      this.inputControl.setValue(value?.nombre ?? '', { emitEvent: false });
    }
  }

  registerOnChange(fn: (value: IUnidadVinculacion) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
    this.stateChanges.next();
  }

  onContainerClick(): void {
    this.focused = true;
    this.stateChanges.next();
  }

  get shouldLabelFloat(): boolean {
    return this.focused || !!this.value || !!this.inputControl.value;
  }

  get empty(): boolean {
    return !this.value && !this.inputControl.value;
  }

  setDescribedByIds(ids: string[]): void {
    this.userAriaDescribedBy = ids.join(' ');
    this.elementRef.nativeElement.setAttribute('aria-describedby', this.userAriaDescribedBy);
  }

  ngOnDestroy(): void {
    this.stateChanges.complete();
    this._destroy.next();
    this._destroy.complete();
    this.focusMonitor.stopMonitoring(this.elementRef);
  }

  protected handleKeydown(event: KeyboardEvent): void {
    if (!this.disabled && this.focused) {
      if ((event.key === 'Backspace' || event.key === 'Delete') && !hasModifierKey(event)) {
        event.preventDefault();
        this.propagateChanges(null);
      } else {
        event.preventDefault();
        this.showDialog();
      }
    }
  }

  private propagateChanges(value: IUnidadVinculacion): void {
    this._value = value;
    this.inputControl.setValue(value?.nombre ?? '', { emitEvent: false });
    this.onChange(value);
    this.stateChanges.next();
    this.changeDetectorRef.markForCheck();
  }
}
