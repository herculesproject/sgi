import { Component, OnDestroy } from '@angular/core';
import { CAT_ROUTE_NAMES } from '../cat-route-names';
import { LayoutService } from '@core/services/layout.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'sgi-cat-menu-principal',
  templateUrl: './cat-menu-principal.component.html',
  styleUrls: ['./cat-menu-principal.component.scss'],
})
export class CatMenuPrincipalComponent implements OnDestroy {
  CAT_ROUTE_NAMES = CAT_ROUTE_NAMES;

  opened: boolean;

  private subcription: Subscription;

  constructor(private layout: LayoutService) {
    this.subcription = this.layout.menuOpened$.subscribe((val) => this.opened = val);
  }

  ngOnDestroy(): void {
    this.subcription.unsubscribe();
  }
}
