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

  getSubtotalPrice(cartItem: CartItem): number {
    return cartItem.quantity*this.getPrice(cartItem);
  }

  getTotalPrice(cartItems: CartItem[]): number {
    let totalPrice: number = 0;
    cartItems.forEach( cartItem => {
      totalPrice+=this.getSubtotalPrice(cartItem);
    })
    return totalPrice;
  }

  getPrice(cartItem: CartItem): number{
    return cartItem.goods.price-cartItem.goods.price*(cartItem.goods.discount/100);
  }


}