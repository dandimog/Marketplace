import { Component } from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AccountService} from '../../_services/account.service';
import {first} from 'rxjs/operators';
import {Router} from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './reset-password.component.html',
  // styleUrls: ['./register.component.css']
})

export class ResetPasswordComponent {

  form: FormGroup;
  submitted = false;

  constructor(
    private formBuilder: FormBuilder,
    private accountService: AccountService,
    // private route: ActivatedRoute,
    private router: Router,
    // private alertService: AlertService
  ) {
    this.form = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
    }, {
      // validator: MustMatch('password', 'validateConfirmPassword')
    });
  }

  get getForm(): { [p: string]: AbstractControl } { return this.form.controls; }

  onSubmit(): void {
    this.submitted = true;

    // stop here if form is invalid
    if (this.form.invalid) {
      return;
    }

    // this.loading = true;
    this.accountService.register(this.form.value)
      .pipe(first())
      .subscribe({
        next: () => {
          console.log('Registered');
          // this.router.navigate(['../login'], { relativeTo: this.route });
          this.router.navigate(['../login']);
        },
        error: error => {
          console.log(error);
          // this.alertService.error(error);
          // this.loading = false;
        }
      });
  }

}
