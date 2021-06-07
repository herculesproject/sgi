import { Component } from '@angular/core';
import { MSG_PARAMS } from '@core/i18n';
import { PII_ROUTE_NAMES } from '../pii-route-names';

@Component({
  selector: 'sgi-pii-menu-principal',
  templateUrl: './pii-menu-principal.component.html',
  styleUrls: ['./pii-menu-principal.component.scss']
})
export class PiiMenuPrincipalComponent {

  get PII_ROUTE_NAMES() {
    return PII_ROUTE_NAMES;
  }

  get MSG_PARAMS() {
    return MSG_PARAMS;
  }

}
