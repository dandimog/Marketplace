import {  Component, Input, OnInit } from '@angular/core';
import { Product } from 'src/app/_models/products/product';
import {AuthService} from "../../_auth/auth.service";

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css'],
})
export class ProductListComponent {
  @Input() products: Product[] = [];
  direction: string = "ASC";
  sort: string = "name";
  role: string | null;

  constructor(private authService: AuthService) {
    this.role = authService.getRole();
  }

  ngOnInit(){
    this.direction = "ASC";
    this.sort="name";
  }

  setDirection():void{
    if(this.direction == "ASC"){
      this.direction = "DESC";
    }
    else{
      this.direction = "ASC";
    }
  }

  setSort(value:string):void{
    this.sort=value;
  }
}
