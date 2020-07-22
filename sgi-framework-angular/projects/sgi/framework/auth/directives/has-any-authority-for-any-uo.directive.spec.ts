import { HasAnyAuthorityForAnyUODirective } from './has-any-authority-for-any-uo.directive';
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

describe('HasAnyAuthorityForAnyUODirective', () => {
  const subjectMock = new BehaviorSubject<IAuthStatus>(defaultAuthStatus);
  const mockAuthService = {
    authStatus$: subjectMock
  };

  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestComponent, HasAnyAuthorityForAnyUODirective],
      providers: [{ provide: SgiAuthService, useValue: mockAuthService }]
    });
  });


  it('With authStatus with authority VIEW_UO, then should be rendered', () => {
    // The template to test
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'LIST']">Test with authorities: VIEW and LIST</div>`;
    // The userAuthorities
    const userAuthorities = ['VIEW_UO'];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With authStatus without authority VIEW_UO, then shouldn\'t be rendered', () => {
    // The template to test
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'LIST']">Test with authorities: VIEW and LIST</div>`;
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

  it('After authStatus change authority, then should be rendered', () => {
    // The template to test
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'LIST']">Test with authorities: VIEW and LIST</div>`;
    // The userAuthorities
    const userAuthorities = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);

    // Add new user authority
    userAuthorities.push('VIEW_UO');

    // Publish the new auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Refresh component
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('After authStatus change authority, then shouldn\'t be rendered', () => {
    // The template to test
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'LIST']">Test with authorities: VIEW and LIST</div>`;
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
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'LIST']">Test with authorities: VIEW and LIST</div>`;
    // The userAuthorities
    const userAuthorities = ['VIEW_UO'];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    // Add user authority
    userAuthorities.push('UPDATE_UO');

    // Publish the new auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Refresh component
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With empty authority error will be throw', () => {
    // The template to test
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW','']">Test with empty authority in authorities</div>`;
    // The userAuthorities
    const userAuthorities = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);

    // Expect a error
    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an authority'));
  });

  it('With no type string array authorities error will be throw', () => {
    // The template to test
    const template = `<div *sgiHasAnyAuthorityForAnyUO="1">Test with authorities: 1</div>`;
    // The userAuthorities
    const userAuthorities = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);

    // Expect a error
    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an array of authorities'));
  });

  it('With authorities that contains UO error will be throw', () => {
    // The template to test
    const template = `<div *sgiHasAnyAuthorityForAnyUO="['VIEW', 'VIEW_UO']">Test with authorities: VIEW and VIEW_UO</div>`;
    // The userAuthorities
    const userAuthorities = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);

    // Expect a error
    expect(() => fixture.detectChanges()).toThrow(Error('Authority cannot contain an underscore'));
  });

  it('With no authorities value error will be throw', () => {
    // The template to test
    const template = `<div *sgiHasAnyAuthorityForAnyUO>Test without authorities value</div>`;
    // The userAuthorities
    const userAuthorities = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userAuthorities));

    // Build the test component
    fixture = createTestComponent(template);

    // Expect a error
    expect(() => fixture.detectChanges()).toThrow(Error('Must provide an array of authorities'));
  });

});
