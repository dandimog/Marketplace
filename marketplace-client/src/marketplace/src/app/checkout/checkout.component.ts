import { Component, OnInit } from "@angular/core";
import { CartItem } from "../_models/cart-item.model";
import { CartService } from "../_services/cart/cart.service";

@Component({
  selector: 'mg-checkout',
  templateUrl: './checkout.component.html'
})
export class CheckoutComponent implements OnInit {
  items: CartItem[] = [];

  constructor(private cartService: CartService) {}

  ngOnInit(): void {
    this.items = this.cartService.getCart().getItems();
  }


}