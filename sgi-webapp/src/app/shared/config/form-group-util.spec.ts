import { FormGroup, FormControl, Validators } from '@angular/forms';
import { FormGroupUtil } from './form-group-util';

describe('Pruebas de FormGroupUtil', () => {
  let formGroup: FormGroup;

  beforeEach(() => {
    formGroup = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      contraseña: new FormControl('', [Validators.required]),
    });
  });

  it('Comprueba el método setValue con una clave que existe', () => {
    const email = 'email@gmail.com';
    const key = 'email';
    expect(formGroup.get(key).value).toBe('');
    FormGroupUtil.setValue(formGroup, key, email);
    expect(formGroup.get(key).value).toBe(email);
  });

  it('Comprueba el método setValue con una clave que no existe', () => {
    const email = 'email@gmail.com';
    const key = 'email2';
    expect(formGroup.get(key)).toBeNull();
    FormGroupUtil.setValue(formGroup, key, email);
    expect(formGroup.get(key)).toBeNull();
  });

  it('Comprueba el método getValue con una clave que existe', () => {
    const email = 'email@gmail.com';
    const key = 'email';
    expect(FormGroupUtil.getValue(formGroup, key)).toBe('');
    FormGroupUtil.setValue(formGroup, key, email);
    expect(FormGroupUtil.getValue(formGroup, key)).toBe(email);
  });

  it('Comprueba el método getValue con una clave que no existe', () => {
    const key = 'email2';
    expect(FormGroupUtil.getValue(formGroup, key)).toBeNull();
  });
});
