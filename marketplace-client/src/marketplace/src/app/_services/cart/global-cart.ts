import {Inject, Injectable, OnDestroy, OnInit} from "@angular/core";
import {interval, Observable, Subscription} from "rxjs";

import {CartItem} from "../../_models/cart-item.model";
import {Cart} from "./cart";
import {HttpClient} from "@angular/common/http";
import {AuthService} from "../../_auth/auth.service";
import {CartItemCreateDto} from "../../_models/cart-item-create-dto.model";
import {BrowserCart} from "./browser-cart";
import {environment} from "../../../environments/environment";

const baseUrl = `${environment.apiUrl}`;

@Injectable({
  providedIn: 'root'
})
export class GlobalCart implements Cart, OnDestroy{

  private subscription: Subscription | null = null;

  private initialized: boolean = false;

  constructor(
    @Inject(BrowserCart)private cart: Cart,
    private http: HttpClient,
    private auth: AuthService
  ) {
    if(this.auth.isAuthenticated()) {
      this.subscription = interval(10000)
        .subscribe(() => {
          console.log("polling...");
          this.http
            .get<CartItem[]>(`${baseUrl}/shopping-cart/`)
            .subscribe({
              next: items => {
                if (items !== this.cart.getItems()) {
                  this.cart.setItems(items);
                }
              },
              error: e => console.log(e)
            })
        });
    }
  }

  ngOnDestroy(): void {
    if(this.subscription!==null) {
      this.subscription.unsubscribe();
    }
  }

  private init(): void{
    if(!this.initialized){
      this.putShoppingCart(this.cart.getItems());
    }
  }

  private mapToDto(item: CartItem): CartItemCreateDto {
    return new CartItemCreateDto(item.product.id,item.addingTime,item.quantity);
  }

  addItem(item: CartItem): void {
    this.cart.addItem(item);
    if(this.auth.isAuthenticated()){
      this.putShoppingCartItem(item)
        .subscribe({error: e=>console.log(e)});
    }
  }

  empty(): void {
    this.cart.empty();
    if(this.auth.isAuthenticated()) {
      this.deleteShoppingCart()
        .subscribe({error: e => console.log(e)});
    }
  }

  getItems(): CartItem[] {
    return this.cart.getItems();
  }

  removeItem(item: CartItem): void {
    this.cart.removeItem(item);
    if(this.auth.isAuthenticated()) {
      this.deleteShoppingCartItem(item).subscribe({error: e => console.log(e)});
    }
  }



  setItems(items: CartItem[]): void {
    this.cart.setItems(items);
    if(this.auth.isAuthenticated()) {
      this.putShoppingCart(items);
    }
  }

  updateItem(item: CartItem): void {
    this.cart.updateItem(item);
    if(this.auth.isAuthenticated()) {
      this.patchShoppingCartItem(item);
    }
  }

  getItem(productId: number): CartItem | null {
    return this.cart.getItem(productId);
  }

  private deleteShoppingCartItem(item: CartItem): Observable<any>{
    return this.http
      .delete(`${baseUrl}/shopping-cart/item/${item.product.id}`);
  }

  private deleteShoppingCart(): Observable<any>{
    return this.http.delete(`${baseUrl}/shopping-cart/`);
  }

  private putShoppingCartItem(item: CartItem): Observable<any>{
    return this.http.put(`${baseUrl}/shopping-cart/`,this.mapToDto(item));
  }

  private patchShoppingCartItem(item: CartItem){
    this.http
      .patch(`${baseUrl}/shopping-cart/item/${item.product.id}`, this.mapToDto(item))
      .subscribe({error: e => console.log(e)});
  }

  private putShoppingCart(items: CartItem[]) {
    this.deleteShoppingCart().subscribe({
      next: () => items.forEach(item => this.putShoppingCartItem(item)),
      error: (e) => console.log(e)
    })
  }
}
