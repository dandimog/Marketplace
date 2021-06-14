import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { AccountService } from '../../_services/account.service';
import { validateBirthday } from '../../_helpers/validators.service';
import { first } from 'rxjs/operators';
import { Role } from '../../_models/role';
import { StaffMember } from '../../_models/staff-member';

@Component({
  selector: 'app-product',
  templateUrl: './add-product.component.html',
  styleUrls: ['./add-product.component.css'],
})
export class AddProductComponent {
  form: FormGroup;

  submitted = false;


  loading = false;
  registered = false;

  constructor(
    private formBuilder: FormBuilder,
    private accountService: AccountService
  ) {
    this.form = this.formBuilder.group(
      {
        name: ['', Validators.required],
        surname: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        phone: ['', Validators.pattern(/\+380[0-9]{9}/)],
        birthday: [''],
        role: ['', Validators.required],
        status: ['', Validators.required],
      },
      {
        validator: [validateBirthday],
      }
    );
  }

  get getForm(): { [p: string]: AbstractControl } {
    return this.form.controls;
  }

  courierRoleSelected(): boolean {
    return this.form.value.role == Role.Courier;
  }

  pmRoleSelected(): boolean {
    return this.form.value.role == Role.ProductManager;
  }

  private mapToStaffMember(o: any): StaffMember {
    return {
      id: -1,
      name: o.name,
      surname: o.surname,
      email: o.email,
      dateOfBirth: o.dateOfBirth,
      phone: o.phone,
      role: o.role,
      status: o.status,
    };
  }

  onSubmit(): void {
    this.submitted = true;
    if (this.form.invalid) {
      return;
    }
    this.loading = true;

    let observable = null;
    if (this.courierRoleSelected()) {
      observable = this.accountService.registerCourier(
        this.mapToStaffMember(this.form.value)
      );
    } else if (this.pmRoleSelected()) {
      observable = this.accountService.registerProductManager(
        this.mapToStaffMember(this.form.value)
      );
    } else {
      return;
    }
    observable.pipe(first()).subscribe({
      next: () => {
        this.loading = false;
        this.registered = true;
      },
      error: (error) => {
        if (error.error.message === 'Email  already exists') {
          this.getForm.email.setErrors({ EmailAlreadyExists: true });
        }
        this.loading = false;
      },
    });
  }
}
