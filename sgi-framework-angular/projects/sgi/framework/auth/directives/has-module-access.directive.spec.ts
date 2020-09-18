import { HasModuleAccessDirective } from './has-module-access.directive';
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

function createAuthStatus(modules: string[]): IAuthStatus {
  const authStatus = defaultAuthStatus;
  authStatus.modules = modules;
  return authStatus;
}

describe('HasModuleAccessDirective', () => {
  const subjectMock = new BehaviorSubject<IAuthStatus>(defaultAuthStatus);
  const mockAuthService = {
    authStatus$: subjectMock
  };

  let fixture: ComponentFixture<TestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TestComponent, HasModuleAccessDirective],
      providers: [{ provide: SgiAuthService, useValue: mockAuthService }]
    });
  });

  it('With authStatus with module GEN, then should be rendered', () => {
    // The template to test
    const template = `<div *sgiHasModuleAccess="'GEN'">Test with module: GEN</div>`;
    // The userModules
    const userModules = ['GEN'];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userModules));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With authStatus without module GEN, then shouldn\'t be rendered', () => {
    // The template to test
    const template = `<div *sgiHasModuleAccess="'GEN'">Test with module: GEN</div>`;
    // The userModules
    const userModules = ['LOCK'];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userModules));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change module, then should be rendered', () => {
    // The template to test
    const template = `<div *sgiHasModuleAccess="'GEN'">Test with module: GEN</div>`;
    // The userModules
    const userModules = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userModules));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);

    // Add authority to user
    userModules.push('GEN');

    // Publish new auth status
    subjectMock.next(createAuthStatus(userModules));

    // Refresh component
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('After authStatus change module, then shouldn\'t be rendered', () => {
    // The template to test
    const template = `<div *sgiHasModuleAccess="'GEN'">Test with module: GEN</div>`;
    // The userModules
    const userModules = ['GEN'];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userModules));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    // Remove user authority
    userModules.pop();

    // Publish the new auth status
    subjectMock.next(createAuthStatus(userModules));

    // Refresh component
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(0);
  });

  it('After authStatus change module, then shouldn\'t duplicate element', () => {
    // The template to test
    const template = `<div *sgiHasModuleAccess="'GEN'">Test with module: GEN</div>`;
    // The userModules
    const userModules = ['GEN'];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userModules));

    // Build the test component
    fixture = createTestComponent(template);
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);

    // Add authority to user
    userModules.push('MASTER');

    // Publish the new auth status
    subjectMock.next(createAuthStatus(userModules));

    // Refresh component
    fixture.detectChanges();

    // Then:
    expect(fixture.debugElement.queryAll(By.css('div')).length).toEqual(1);
  });

  it('With empty module error will be throw', () => {
    // The template to test
    const template = `<div *sgiHasModuleAccess="''">Test with empty module</div>`;
    // The userModules
    const userModules = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userModules));

    // Build the test component
    fixture = createTestComponent(template);

    // Expect a error
    expect(() => fixture.detectChanges()).toThrow(Error('Must provide a module name'));
  });

  it('With no type string module name error will be throw', () => {
    // The template to test
    const template = `<div *sgiHasModuleAccess="1">Test with module: 1</div>`;
    // The userModules
    const userModules = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userModules));

    // Build the test component
    fixture = createTestComponent(template);

    // Expect a error
    expect(() => fixture.detectChanges()).toThrow(Error('Must provide a module name'));
  });

  it('With no module value error will be throw', () => {
    // The template to test
    const template = `<div *sgiHasModuleAccess>Test without module value</div>`;
    // The userModules
    const userModules = [''];
    // Publish the auth status
    subjectMock.next(createAuthStatus(userModules));

    // Build the test component
    fixture = createTestComponent(template);

    // Expect a error
    expect(() => fixture.detectChanges()).toThrow(Error('Must provide a module name'));
  });
});
