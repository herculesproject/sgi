import { Component, OnDestroy } from '@angular/core';
import { LayoutService } from '@core/services/layout.service';
import { INV_ROUTE_NAMES } from '../inv-route-names';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-inv-menu-principal',
  templateUrl: './inv-menu-principal.component.html',
  styleUrls: ['./inv-menu-principal.component.scss']
})
export class InvMenuPrincipalComponent implements OnDestroy {
  INV_ROUTE_NAMES = INV_ROUTE_NAMES;

  opened: boolean;

  private subcription: Subscription;

  constructor(private layout: LayoutService) {
    this.subcription = this.layout.menuOpened$.subscribe((val) => this.opened = val);
  }

  ngOnDestroy(): void {
    this.subcription.unsubscribe();
  }

}
