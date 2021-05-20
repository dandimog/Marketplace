import { Component } from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {AccountService} from '../../_services/account.service';
import {first} from 'rxjs/operators';

@Component({
    selector: 'app-login-form',
    templateUrl: 'login.form.component.html',
  }
)
export class LoginFormComponent {
  form: FormGroup;
  submitted = false;

  // icons
  loading = false;
  showPassword = true;

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
  ) {
    this.form = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  get getForm(): { [p: string]: AbstractControl } { return this.form.controls; }

  onSubmit(): void {
    this.submitted = true;
    if (this.form.invalid) {
      return;
    }
    this.loading = true;
    this.accountService.login(this.getForm.email.value, this.getForm.password.value)
      .pipe(first())
      .subscribe({
        next: () => {
          const returnUrl = 'home';
          this.router.navigateByUrl(returnUrl);
        },
        error: error => {
          console.log(error);
          this.loading = false;
        }
      });
  }

  showHidePassword(): void {
    this.showPassword = !this.showPassword;
  }
}
