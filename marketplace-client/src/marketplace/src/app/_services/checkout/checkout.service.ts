import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { CartItem } from "src/app/_models/cart-item.model";
import { User } from "src/app/_models/user";
import { environment } from "src/environments/environment";

const baseUrl = environment.apiUrl;

@Injectable({
  providedIn: 'root'
})
export class Checkout {

  constructor(private http: HttpClient) {}

  sendOrderDetails(user: User, cart: CartItem[]) {
  }

  getUserByEmail(email: string | null): Observable<User> {
    return this.http.get<User>(`${baseUrl}/getByEmail/?email=` + email);
  }
}