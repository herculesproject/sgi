import { Component, OnDestroy, OnInit } from '@angular/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { LayoutService, BreadcrumbData } from '@core/services/layout.service';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss']
})
export class BreadcrumbComponent implements OnInit, OnDestroy {
  private subscription: Subscription;
  data: BreadcrumbData;

  constructor(
    private readonly logger: NGXLogger,
    private layoutService: LayoutService
  ) { }

  ngOnInit(): void {
    this.logger.debug(BreadcrumbComponent.name, 'ngOnInit()', 'start');
    this.subscription = this.layoutService.breadcrumData$.subscribe((data) => this.data = data);
    this.logger.debug(BreadcrumbComponent.name, 'ngOnInit()', 'end');
  }

  ngOnDestroy(): void {
    this.logger.debug(BreadcrumbComponent.name, 'ngOnDestroy()', 'start');
    this.subscription?.unsubscribe();
    this.logger.debug(BreadcrumbComponent.name, 'ngOnDestroy()', 'end');
  }

}
