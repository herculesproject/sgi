import { StepperSelectionEvent, STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { MatStepper } from '@angular/material/stepper';
import { FragmentComponent } from '@core/component/fragment.component';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { MemoriaActionService } from '../../memoria.action.service';
import { IBlock, MemoriaFormularioFragment } from './memoria-formulario.fragment';

@Component({
  selector: 'sgi-memoria-formulario',
  templateUrl: './memoria-formulario.component.html',
  styleUrls: ['./memoria-formulario.component.scss'],
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false }
    }
  ],
  encapsulation: ViewEncapsulation.None
})
export class MemoriaFormularioComponent extends FragmentComponent implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];

  blocks: IBlock[] = [];

  @ViewChild(MatStepper, { static: true }) private stepper: MatStepper;

  private memoriaFormularioFragment: MemoriaFormularioFragment;

  constructor(
    protected readonly logger: NGXLogger,
    actionService: MemoriaActionService
  ) {
    super(actionService.FRAGMENT.FORMULARIO, actionService);
    this.memoriaFormularioFragment = (this.fragment as MemoriaFormularioFragment);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.logger.debug(MemoriaFormularioComponent.name, 'ngOnInit()', 'start');
    this.subscriptions.push(
      this.memoriaFormularioFragment.blocks$.subscribe((values) => this.blocks = values)
    );
    // Mark first block as selected
    this.memoriaFormularioFragment.selectedIndex$.next(this.stepper.selectedIndex);
    this.logger.debug(MemoriaFormularioComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(MemoriaFormularioComponent.name, 'ngOnDestroy()', 'start');
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
    this.logger.debug(MemoriaFormularioComponent.name, 'ngOnDestroy()', 'end');
  }

  onStepChange(stepperEvent: StepperSelectionEvent): void {
    this.memoriaFormularioFragment.selectedIndex$.next(stepperEvent.selectedIndex);
  }

  nextStep(): void {
    this.memoriaFormularioFragment.performChecks(true);
    this.stepper.next();
  }

  previousStep(): void {
    this.stepper.previous();
  }
}
