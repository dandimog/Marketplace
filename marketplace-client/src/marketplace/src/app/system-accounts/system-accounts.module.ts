import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SystemAccountsRoutingModule } from './system-accounts-routing-module';
import { SystemAccountComponent } from './system-account/system-account.component';
import { PaginationComponent } from './pagination/pagination.component';
import { SearchComponent } from './search/search.component';
import { FilterComponent } from './filter/filter.component';
import { RegisterStuffComponent } from './register-stuff/register-stuff.component';
import { ReactiveFormsModule } from '@angular/forms';
import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { AccountListComponent } from './account-list/account-list.component';
import {ProfileComponent} from "./profile/profile.component";
import {UpdateInfoComponent} from "./update-info/update-info.component";
import {UpdateProductComponent} from "../product-catalog/update-product/update-product.component";
import {AddProductComponent} from "../product-catalog/add-product/add-product.component";


@NgModule({
  declarations: [
    SystemAccountComponent,
    PaginationComponent,
    SearchComponent,
    FilterComponent,
    RegisterStuffComponent,
    AccountListComponent,
    ProfileComponent,
    UpdateInfoComponent,
    UpdateProductComponent,
    AddProductComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SystemAccountsRoutingModule,
    NgbCollapseModule,
  ],
  exports: [SystemAccountComponent],
})
export class SystemAccountsModule {}
