import {AbstractControl} from '@angular/forms';

export function validateConfirmPassword(control: AbstractControl): void{
  const password = control.get('password')?.value;
  const confirmedPassword = control.get('confirmPassword')?.value;
  const passwordField = control.get('confirmPassword');
  if (passwordField && (password !== confirmedPassword)) {
    passwordField.setErrors({NoPasswordMatch: true});
  }
}

export function validatePassword(control: AbstractControl): void{
  const password = control.get('password')?.value;
  const digitRegex = /\d/;
  const uppercaseRegex = /[A-Z]/;
  const lowercaseRegex = /[a-z]/;
  const passwordField = control.get('password');
  if (passwordField) {
    if (!digitRegex.test(password)) {
      passwordField.setErrors({NoDigit: true});
    }
    if (!uppercaseRegex.test(password)) {
      passwordField.setErrors({NoUppercase: true});
    }
    if (!lowercaseRegex.test(password)) {
      passwordField.setErrors({NoLowercase: true});
    }
  }
}
