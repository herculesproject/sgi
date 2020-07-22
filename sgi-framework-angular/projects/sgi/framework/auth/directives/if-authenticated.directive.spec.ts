import { IfAuthenticatedDirective } from './if-authenticated.directive';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SgiAuthService, IAuthStatus, defaultAuthStatus } from '../auth.service';
import { BehaviorSubject } from 'rxjs';
import { By } from '@angular/platform-browser';

@Component({
  template: '',
})
class TestComponent {
}

function createTestComponent(template: string): ComponentFixture<TestComponent> {
  return TestBed.overrideComponent(TestComponent, { set: { template } })
    .createComponent(TestComponent);
}

function createAuthStatus(authenticated: boolean): IAuthStatus {
  const authStatus = defaultAuthStatus;
  authStatus.isAuthenticated = authenticated;
  return authStatus;
}

describe('IfAuthenticatedDirective', () => {
  const subjectMock = new BehaviorSubject<IAuthStatus>(defaultAuthStatus);
  const mockAuthService = {
    authStatus$: subjectMock
  };

  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestComponent, IfAuthenticatedDirective],
      providers: [{ provide: SgiAuthService, useValue: mockAuthService }]
    });
  });


  it('With authStatus authenticated, then should be rendered', () => {
    // The template to test
    const template = `<div *sgiIfAuthenticated>Test with authenticated</div>`;
    // Publish the auth status
    subjectMock.next(createAuthStatus(true));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With authStatus unauthenticated, then shouldn\'t be rendered', () => {
    // The template to test
    const template = `<div *sgiIfAuthenticated>Test with unauthenticated</div>`;
    // Publish the auth status
    subjectMock.next(createAuthStatus(false));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change to authenticated, then should be rendered', () => {
    // The template to test
    const template = `<div *sgiIfAuthenticated>Test with authenticated</div>`;

    // Publish the auth status
    subjectMock.next(createAuthStatus(false));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);

    // Publish new auth status
    subjectMock.next(createAuthStatus(true));

    // Refresh component
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('After authStatus change to unauthenticated, then shouldn\'t be rendered', () => {
    // The template to test
    const template = `<div *sgiIfAuthenticated>Test with unauthenticated</div>`;

    // Publish the auth status
    subjectMock.next(createAuthStatus(true));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    // Publish the new auth status
    subjectMock.next(createAuthStatus(false));

    // Refresh component
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change, then shouldn\'t duplicate element', () => {
    // The template to test
    const template = `<div *sgiIfAuthenticated>Test with authenticated</div>`;

    // Publish the auth status
    subjectMock.next(createAuthStatus(true));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    // Publish the new auth status
    subjectMock.next(createAuthStatus(true));

    // Refresh component
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With authState authenticated and attribute value, shouldn\'t throw a error and will be rendered', () => {
    // The template to test
    const template = `<div *sgiIfAuthenticated="'DA'">Test with attribute value</div>`;
    // Publish the auth status
    subjectMock.next(createAuthStatus(true));

    // Build the test component
    fixture = createTestComponent(template);

    // Refresh component shouldn't throw a error
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

});
