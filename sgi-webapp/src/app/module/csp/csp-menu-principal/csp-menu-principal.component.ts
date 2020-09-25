import { Component, OnDestroy } from '@angular/core';
import { LayoutService } from '@core/services/layout.service';
import { CSP_ROUTE_NAMES } from '../csp-route-names';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-csp-menu-principal',
  templateUrl: './csp-menu-principal.component.html',
  styleUrls: ['./csp-menu-principal.component.scss']
})
export class CspMenuPrincipalComponent implements OnDestroy {
  CSP_ROUTE_NAMES = CSP_ROUTE_NAMES;

  opened: boolean;

  private subcription: Subscription;

  constructor(private layout: LayoutService) {
    this.subcription = this.layout.menuOpened$.subscribe((val) => this.opened = val);
  }

  ngOnDestroy(): void {
    this.subcription.unsubscribe();
  }

}
