import { HasAuthorityDirective } from './has-authority.directive';
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

function createAuthStatus(authorities: string[]): IAuthStatus {
  const authStatus = defaultAuthStatus;
  authStatus.authorities = authorities;
  return authStatus;
}

describe('HasAuthorityDirective', () => {
  const subjectMock = new BehaviorSubject<IAuthStatus>(defaultAuthStatus);
  const mockAuthService = {
    authStatus$: subjectMock
  };

  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestComponent, HasAuthorityDirective],
      providers: [{ provide: SgiAuthService, useValue: mockAuthService }]
    });
  });


  it('With authStatus with authority VIEW, then should be rendered', () => {
    // The template to test
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    // The userAuthorities
    const userAuthorities = ['VIEW'];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With authStatus without authority VIEW, then shouldn\'t be rendered', () => {
    // The template to test
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    // The userAuthorities
    const userAuthorities = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('With authStatus with authority VIEW_UO, then shouldn\'t be rendered', () => {
    // The template to test
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    // The userAuthorities
    const userAuthorities = ['VIEW_UO'];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change authority, then should be rendered', () => {
    // The template to test
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    // The userAuthorities
    const userAuthorities = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);

    // Add authority to user
    userAuthorities.push('VIEW');

    // Publish new auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Refresh component
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('After authStatus change authority, then shouldn\'t be rendered', () => {
    // The template to test
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    // The userAuthorities
    const userAuthorities = ['VIEW'];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    // Remove user authority
    userAuthorities.pop();

    // Publish the new auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Refresh component
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change authority, then shouldn\'t duplicate element', () => {
    // The template to test
    const template = `<div *sgiHasAuthority="'VIEW'">Test with authority: VIEW</div>`;
    // The userAuthorities
    const userAuthorities = ['VIEW'];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    // Add authority to user
    userAuthorities.push('UPDATE');

    // Publish the new auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Refresh component
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With empty authority error will be throw', () => {
    // The template to test
    const template = `<div *sgiHasAuthority="''">Test with empty authority</div>`;
    // The userAuthorities
    const userAuthorities = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);

    // Expect a error
    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an authority'));
  });

  it('With no type string authority error will be throw', () => {
    // The template to test
    const template = `<div *sgiHasAuthority="1">Test with authority: 1</div>`;
    // The userAuthorities
    const userAuthorities = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);

    // Expect a error
    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an authority'));
  });

  it('With no authority value error will be throw', () => {
    // The template to test
    const template = `<div *sgiHasAuthority>Test without authority value</div>`;
    // The userAuthorities
    const userAuthorities = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);

    // Expect a error
    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an authority'));
  });

});



