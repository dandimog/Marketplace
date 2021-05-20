import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RegisterComponent } from './register/register.component';
import {LayoutComponent} from './layout/layout.component';
import {LoginComponent} from './login/login.component';
import {ForgotPasswordComponent} from './forgot-password/forgot-password.component';
import {ResetPasswordComponent} from './reset-password/reset-password.component';
import {RegistrationGreetingComponent} from './registration-greeting/registration-greeting.component';
import {ConfirmedComponent} from './confirmed/confirmed.component';

const routes: Routes = [
  {
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'forgot-password', component: ForgotPasswordComponent },
      { path: 'reset-password', component: ResetPasswordComponent },
      { path: 'registration-greeting', component: RegistrationGreetingComponent},
      { path: 'confirmed', component: ConfirmedComponent}
    ],
    path: '', component: LayoutComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AccountRoutingModule { }
