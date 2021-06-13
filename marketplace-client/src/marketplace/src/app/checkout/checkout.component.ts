import { Component, OnInit } from "@angular/core";
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { AuthService } from "../_auth/auth.service";
import { CartItem } from "../_models/cart-item.model";
import { User } from "../_models/user";
import { BrowserCart } from "../_services/cart/browser-cart";
import { CartService } from "../_services/cart/cart.service";
import { Checkout } from "../_services/checkout/checkout.service";

@Component({
  selector: 'mg-checkout',
  templateUrl: './checkout.component.html'
})
export class CheckoutComponent implements OnInit {
  items: CartItem[] = [];
  orderDetailsForm: FormGroup;
  submitted = false;
  authUser: User = {};
  isVisibleBanner = true;

  constructor(
    private cartService: CartService, 
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private checkoutService: Checkout,
    private browserCart: BrowserCart
  ) {
    this.orderDetailsForm = this.formBuilder.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      phone: ['', [Validators.pattern(/\+380[0-9]{9}/), Validators.required]],
      address: ['', Validators.required],
      deliveryTime: ['', Validators.required],
      comment: [''],
      disturb: [false]
    })
  }

  ngOnInit(): void {
    this.items = this.cartService.getCart().getItems();
    this.getAuthUserInfo();
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

  isAuth(): boolean {
    return this.authService.isAuthenticated();
  }

  get getForm(): { [p: string]: AbstractControl } { return this.orderDetailsForm.controls; }

  onSubmit(): void {
    if(this.isAuth()) {
      this.orderDetailsForm.patchValue({
        name: this.authUser.name,
        surname: this.authUser.surname,
        phone: '+380687564849'//this.authUser.phone
      });
    }

    if(this.orderDetailsForm.invalid) {
      return;
    }
    this.submitted = true;
    
  }

  private getAuthUserInfo() {
    if(this.isAuth()) {
      this.checkoutService.getUserByEmail(this.authService.getMail())
        .subscribe((user: User) => this.authUser=user);
      
    }
  }

  doOrder() {
    this.submitted = false;
    this.browserCart.empty();
    this.items = [];
  }

  hideBanner() {
    this.isVisibleBanner = false;
  }


}