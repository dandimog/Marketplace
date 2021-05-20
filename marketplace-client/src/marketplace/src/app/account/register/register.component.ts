import { Component } from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AccountService} from '../../_services/account.service';
import {first} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';
import {validateConfirmPassword, validatePassword} from '../../_helpers/validators.service';

@Component({
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})

export class RegisterComponent {

  form: FormGroup;
  submitted = false;

  // icons
  loading = false;
  showPassword = true;

  constructor(
    private formBuilder: FormBuilder,
    private accountService: AccountService,
    private route: ActivatedRoute,
    private router: Router,
    // private alertService: AlertService
  ) {
    this.form = this.formBuilder.group({
      // title: ['', Validators.required],
      name: ['', Validators.required],
      surname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: [''],
      password: ['', [Validators.required, Validators.minLength(6),
        Validators.maxLength(32)]],
      confirmPassword: ['', Validators.required],
      acceptTerms: [false, Validators.requiredTrue]
    }, {
      validator: [validateConfirmPassword, validatePassword]
    });
  }

  get getForm(): { [p: string]: AbstractControl } { return this.form.controls; }

  onSubmit(): void {
    this.submitted = true;
    if (this.form.invalid) {
      return;
    }
    this.loading = true;
    this.accountService.register(this.form.value)
      .pipe(first())
      .subscribe({
        next: () => {
          console.log('Registered');
          // this.router.navigate(['../login'], { relativeTo: this.route });
          this.router.navigate(['../registration-greeting', {relativeTo: this.route}]);
        },
        error: error => {
          console.log(error);
          // this.alertService.error(error);
          // this.loading = false;
        }
      });
  }

}
