import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductListComponent } from './product-list/product-list.component';
import { ProductComponent } from './product/product.component';
import {AddProductComponent} from "./add-product/add-product.component";
import {AccountListComponent} from "../system-accounts/account-list/account-list.component";
import {RegisterStuffComponent} from "../system-accounts/register-stuff/register-stuff.component";
import {ProfileComponent} from "../system-accounts/profile/profile.component";
import {UpdateInfoComponent} from "../system-accounts/update-info/update-info.component";
import {UpdateProductComponent} from "./update-product/update-product.component";


const routes: Routes = [
  {
    path: '',
    component: ProductListComponent,
    children: [

      /*{
        path: '',
        pathMatch: 'prefix',
        redirectTo: 'couriers',
      },*/
      //{ path: 'couriers/:id', component: CouriersDetails },
    ],
  },
  { path: ':id', component: UpdateProductComponent }, //ProductComponent
  ];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProductCatalogRoutingModule {}
