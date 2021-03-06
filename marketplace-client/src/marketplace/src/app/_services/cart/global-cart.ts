import {Inject, Injectable, OnDestroy} from "@angular/core";
import {interval, Observable, of, Subscription} from "rxjs";

import {CartItem} from "../../_models/cart-item.model";
import {Cart} from "./cart";
import {HttpClient} from "@angular/common/http";
import {AuthService} from "../../_auth/auth.service";
import {CartItemCreateDto} from "../../_models/cart-item-create-dto.model";
import {BrowserCart} from "./browser-cart";
import {environment} from "../../../environments/environment";
import {switchMap} from "rxjs/operators";
import {Role} from "../../_models/role";
import {AlertService} from "../alert.service";
import {ApiError} from "../../_models/ApiError";
import {AlertType} from "../../_models/alert";

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
    private auth: AuthService,
    private alertService: AlertService
  ) {
  }

  public startPolling(): void {
    if(this.alowedToSendRequests()) {
      this.updateItems();
    }
    this.subscription = interval(10000)
      .subscribe(() => {
        if (this.alowedToSendRequests()) {
          this.updateItems();
        }
      });
  }

  private updateItems() {
    this.init().subscribe(() => {
      if (!document.hidden) {
        this.getShoppingCart()
          .subscribe({
            next: items => {
              if (JSON.stringify(items) !== JSON.stringify(this.cart.getItems())) {
                this.cart.setItems(items);
              }
            }
          })
      }
    })
  }

  public stopPolling(): void{
    if(this.subscription!==null) {
      this.subscription.unsubscribe();
    }
  }

  private getShoppingCart() {
    return this.http.get<CartItem[]>(`${baseUrl}/shopping-cart/`);
  }

  ngOnDestroy(): void {
    this.stopPolling();
  }

  private init(): Observable<any>{
    if(!this.initialized){
      this.initialized = true;
      if(this.cart.getItems().length>0){
        return this.putShoppingCart(this.cart.getItems());
      }
      return this.getShoppingCart()
        .pipe(switchMap(items => {
          this.cart.setItems(items);
          return of({});
        }))
    }
    return of({});
  }

  private mapToDto(item: CartItem): CartItemCreateDto {
    return new CartItemCreateDto(item.goods.id,item.addingTime,item.quantity);
  }

  addItem(item: CartItem): void {
    this.cart.addItem(item);
    if(this.alowedToSendRequests()){
      this.init().subscribe(()=>{
        this.putShoppingCartItem(item)
          .subscribe({error: e=>{
              let apiError = e.error as ApiError;
              if(apiError){
                this.alertService.addAlert(apiError.message,AlertType.Danger);
              }
          }});
      })
    }
  }

  empty(): void {
    this.cart.empty();
    if(this.alowedToSendRequests()) {
      this.init().subscribe(()=>{
        this.deleteShoppingCart()
          .subscribe({error: e => {
              let apiError = e.error as ApiError;
              if(apiError){
                this.alertService.addAlert(apiError.message,AlertType.Danger);
              }
            }});
      })
    }
  }

  private alowedToSendRequests() {
    return this.auth.isAuthenticated()&&this.auth.isExpectedRole(Role.User);
  }

  getItems(): CartItem[] {
    return this.cart.getItems();
  }

  removeItem(item: CartItem): void {
    this.cart.removeItem(item);
    if(this.alowedToSendRequests()) {
      this.init().subscribe(()=>{
        this.deleteShoppingCartItem(item)
          .subscribe({error: e => {
              let apiError = e.error as ApiError;
              if(apiError){
                this.alertService.addAlert(apiError.message,AlertType.Danger);
              }
            }});
      })
    }
  }



  setItems(items: CartItem[]): void {
    this.cart.setItems(items);
    if(this.alowedToSendRequests()) {
      this.init().subscribe(()=>{
        this.putShoppingCart(items)
          .subscribe({
            error: e=>{
              let apiError = e.error as ApiError;
              if(apiError){
                this.alertService.addAlert(apiError.message,AlertType.Danger);
              }
            }
          });
      })
    }
  }

  updateItem(item: CartItem): void {
    this.cart.updateItem(item);
    if(this.alowedToSendRequests()) {
      this.init().subscribe(()=>{
        this.patchShoppingCartItem(item)
          .subscribe({error: e => {
              let apiError = e.error as ApiError;
              if(apiError){
                this.alertService.addAlert(apiError.message,AlertType.Danger);
              }
            }});
      })
    }
  }

  getItem(productId: number): CartItem | null {
    return this.cart.getItem(productId);
  }

  private deleteShoppingCartItem(item: CartItem): Observable<any>{
    return this.http
      .delete(`${baseUrl}/shopping-cart/item/${item.goods.id}/`);
  }

  private deleteShoppingCart(): Observable<any>{
    return this.http.delete(`${baseUrl}/shopping-cart/`);
  }

  private putShoppingCartItem(item: CartItem): Observable<any>{
    return this.http.put(`${baseUrl}/shopping-cart/item/`,this.mapToDto(item));
  }

  private patchShoppingCartItem(item: CartItem): Observable<any>{
    return this.http
      .patch(`${baseUrl}/shopping-cart/item/${item.goods.id}/`, this.mapToDto(item))
  }

  private putShoppingCart(items: CartItem[]): Observable<any>{
    let itemDtos: CartItemCreateDto[] = [];
    items.forEach((item)=>{
      itemDtos.push(this.mapToDto(item));
    })
    return this.http.put(`${baseUrl}/shopping-cart/`,itemDtos);
  }
}
