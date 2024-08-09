import { coerceBooleanProperty } from '@angular/cdk/coercion';
import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  Directive,
  DoCheck,
  ElementRef,
  Inject,
  Input,
  OnDestroy,
  Optional,
  Self,
  ViewChild
} from '@angular/core';
import { ControlValueAccessor, NG_VALIDATORS, NgControl } from '@angular/forms';
import { CanUpdateErrorState, ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { Subject, Subscription } from 'rxjs';
import { LanguageService } from '@core/services/language.service';
import { Language } from '@core/i18n/language';
import { I18nFieldValue } from '@core/i18n/i18n-field';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';


@Directive()
export abstract class InputI18nBaseComponent implements
  OnDestroy, AfterViewInit, DoCheck, CanUpdateErrorState, ControlValueAccessor, MatFormFieldControl<I18nFieldValue[]> {

  /** True when the component is initialized and ready  */
  private ready = false;

  abstract get id(): string;

  /** Store subscriptions to be unsuscribed onDestroy */
  private subscriptions: Subscription[] = [];

  readonly stateChanges: Subject<void> = new Subject<void>();

  /** Value of the component. */
  @Input()
  get value(): I18nFieldValue[] {
    return this._value;
  }
  set value(newValue: I18nFieldValue[]) {
    if (newValue === null || !(newValue instanceof Array)) {
      this._value = [];
    }
    else {
      this._value = [...newValue];
    }

    this.selectLanguage(this.selectedLang);

    this.propagateChanges(this._value)

  }
  // tslint:disable-next-line: variable-name
  private _value: I18nFieldValue[] = [];

  /** Whether the component is required. */
  @Input()
  get required(): boolean {
    return this._required;
  }
  set required(value: boolean) {
    this._required = coerceBooleanProperty(value);
  }
  // tslint:disable-next-line: variable-name
  private _required = false;

  @Input()
  get placeholder() {
    return this._placeholder;
  }
  set placeholder(plh) {
    this._placeholder = plh;
    this.stateChanges.next();
  }
  private _placeholder: string;


  @Input()
  get disabled(): boolean {
    return this._disabled;
  }
  set disabled(value: boolean) {
    this._disabled = coerceBooleanProperty(value);
  }
  // tslint:disable-next-line: variable-name
  private _disabled = false;

  // tslint:disable-next-line: variable-name
  /** Object used to control when error messages are shown. */
  @Input()
  set errorStateMatcher(errorStateMatcher: ErrorStateMatcher) {
    this._errorStateMatcher = errorStateMatcher;
  }
  get errorStateMatcher() {
    return this._errorStateMatcher;
  }
  // tslint:disable-next-line: variable-name
  private _errorStateMatcher: ErrorStateMatcher;


  _languages = Language.values();

  get languages(): Language[] {
    return this._languages;
  }

  selectedLang = this._languages[0];
  valueToEdit = "";

  /** Whether the component is focused. */
  get focused(): boolean {
    return (this.matInput?.focused || this.matSelect?.focused) ?? false;
  }

  get errorState(): boolean {
    return this._errorState;
  }
  set errorState(value: boolean) {
    if (this._errorState !== value) {
      this._errorState = value;
      this.stateChanges.next();
    }
  }
  // tslint:disable-next-line: variable-name
  private _errorState = true;

  get shouldLabelFloat(): boolean {
    return !this.empty || this.focused;
  }

  /** Whether the component has a value. */
  get empty(): boolean {
    return this.inputComponent.nativeElement.value.length < 1;
  }

  get tabIndex(): number { return 1; }


  /** `View -> model callback called when value changes` */
  // tslint:disable-next-line: variable-name
  private _onChange: (value: any) => void = () => { };
  /** `View -> model callback called when component has been touched` */
  // tslint:disable-next-line: variable-name
  private _onTouched = () => { };

  @Input('aria-describedby') userAriaDescribedBy: string;

  @ViewChild('inputI18nControl', { static: true }) private inputComponent: ElementRef;

  @ViewChild(MatInput) protected readonly matInput: MatInput;

  @ViewChild(MatSelect) protected readonly matSelect: MatSelect;

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    @Self() @Optional() public ngControl: NgControl,
    private defaultErrorStateMatcher: ErrorStateMatcher,
    private readonly languageService: LanguageService, // Subscribirse a los idiomas disponibles
    @Optional() @Self() @Inject(NG_VALIDATORS) private validators: any[],
    public dialog: MatDialog
  ) {
    if (this.ngControl) {
      // Note: we provide the value accessor through here, instead of
      // the `providers` to avoid running into a circular import.
      this.ngControl.valueAccessor = this;
      this._languages = this.languageService.getAvailableLanguages();
      this.selectedLang = this.languageService.getLanguage();
    }
  }

  ngAfterViewInit(): void {

    // Defer setting the value in order to avoid the "Expression
    // has changed after it was checked" errors from Angular.

    Promise.resolve().then(() => {
      this.subscriptions.push(this.matInput.ngControl.valueChanges.subscribe(cambio => {
        let i18nFieldValue: I18nFieldValue = this.getValueToEditLang();
        if (cambio.length > 0) {
          if (i18nFieldValue == null) {
            i18nFieldValue = { value: cambio, lang: this.selectedLang } as I18nFieldValue
            this._value.push(i18nFieldValue);
          } else {
            i18nFieldValue.value = cambio;
          }
        } else {
          if (i18nFieldValue != null) {
            this.remove(i18nFieldValue)
          }
        }
        this.propagateChanges(this._value)

      }));

      this.errorStateMatcher = this._errorStateMatcher || this.defaultErrorStateMatcher;

      this.matInput.onContainerClick = () => {
        this._onTouched();
      }
      this.selectLanguage(this.selectedLang);

      this.ready = true;
      this.stateChanges.next();
    });

  }

  private getValueToEditLang(): I18nFieldValue {
    for (var i = 0; i < this._value.length; i++) {
      if (this._value[i].lang.code == this.selectedLang.code) {
        return this._value[i];
      }
    }
    return null;
  }

  private getValueToEdit(): string {
    var i18nFieldValue = this.getValueToEditLang();
    if (i18nFieldValue == null) { return "" }
    return i18nFieldValue.value;
  }

  private remove(element: I18nFieldValue): void {
    const index = this.value.indexOf(element);

    if (index >= 0) {
      const current = [...this.value];
      current.splice(index, 1);
      this.propagateChanges(current);
    }
  }

  private propagateChanges(value: I18nFieldValue[]) {
    this._value = value;
    this._onChange(this._value);
    this.changeDetectorRef.markForCheck();
  }

  selectLanguage(language: Language): void {
    this.selectedLang = language;
    this.valueToEdit = this.getValueToEdit();
    this.propagateChanges(this._value)
  }


  // CanUpdateErrorState interface ***************************
  updateErrorState(): void {
    this.errorState = this.ngControl.invalid;
  }


  ngDoCheck(): void {
    if (this.ngControl) {
      this.updateErrorState();
    }
  }

  ngOnDestroy() {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  // MatFormField interface ***************************
  onContainerClick(event: MouseEvent): void {
    this.inputComponent.nativeElement.focus();
  }

  setDescribedByIds(ids: string[]) {
    this.inputComponent.nativeElement.setAttribute('aria-describedby', ids.join(' '));
  }

  // Value Accesor ***************************
  writeValue(value: any): void {
    this.value = value;
    this.propagateChanges(this.value);
  }

  registerOnChange(fn: (value: any) => void): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: () => {}): void {
    this._onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }


}
